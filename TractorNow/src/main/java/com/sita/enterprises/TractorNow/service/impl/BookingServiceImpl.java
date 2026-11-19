package com.sita.enterprises.TractorNow.service.impl;

import com.sita.enterprises.TractorNow.dto.booking.BookingRequest;
import com.sita.enterprises.TractorNow.entity.Booking;
import com.sita.enterprises.TractorNow.entity.Equipment;
import com.sita.enterprises.TractorNow.entity.User;
import com.sita.enterprises.TractorNow.enums.BookingStatus;
import com.sita.enterprises.TractorNow.enums.UserRole;
import com.sita.enterprises.TractorNow.repository.BookingRepository;
import com.sita.enterprises.TractorNow.repository.EquipmentRepository;
import com.sita.enterprises.TractorNow.repository.UserRepository;
import com.sita.enterprises.TractorNow.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final EquipmentRepository equipmentRepository;
    private final UserRepository userRepository;

    @Override
    public Object create(BookingRequest request) {

        validateBookingRequest(request);

        User farmer =
                userRepository.findById(
                                request.getFarmerId()
                        )
                        .orElseThrow(() ->
                                new NoSuchElementException(
                                        "Farmer not found"
                                ));

        if (farmer.getRole() != UserRole.FARMER) {
            throw new IllegalArgumentException(
                    "Only farmers can create bookings"
            );
        }

        Equipment equipment =
                equipmentRepository.findById(
                                request.getEquipmentId()
                        )
                        .orElseThrow(() ->
                                new NoSuchElementException(
                                        "Equipment not found"
                                ));

        if (!Boolean.TRUE.equals(
                equipment.getActive())) {

            throw new IllegalStateException(
                    "Equipment is not active"
            );
        }

        if (!Boolean.TRUE.equals(
                equipment.getAvailable())) {

            throw new IllegalStateException(
                    "Equipment is currently unavailable"
            );
        }

        if (equipment.getOwner() == null) {
            throw new IllegalStateException(
                    "Equipment owner not found"
            );
        }

        if (equipment.getOwner()
                .getId()
                .equals(farmer.getId())) {

            throw new IllegalArgumentException(
                    "You cannot book your own equipment"
            );
        }

        validateBookingTime(request);

        boolean conflict =
                bookingRepository.existsConflictingBooking(
                        equipment.getId(),
                        request.getFromDateTime(),
                        request.getToDateTime()
                );

        if (conflict) {
            throw new IllegalStateException(
                    "Equipment is already booked for the selected time"
            );
        }

        Booking booking = new Booking();

        booking.setFarmer(farmer);
        booking.setEquipment(equipment);

        booking.setFromDateTime(
                request.getFromDateTime()
        );

        booking.setToDateTime(
                request.getToDateTime()
        );

        booking.setDurationHours(
                calculateDurationHours(
                        request.getFromDateTime(),
                        request.getToDateTime()
                )
        );

        booking.setFieldLocation(
                request.getFieldLocation()
        );

        booking.setOperatorRequired(
                Boolean.TRUE.equals(
                        request.getOperatorRequired()
                )
        );

        booking.setReason(
                request.getReason()
        );

        double totalAmount =
                booking.getDurationHours()
                        * equipment.getPricePerHour();

        if (Boolean.TRUE.equals(
                request.getOperatorRequired())) {

            /*
             * Optional operator charge.
             * Keep zero until operator pricing is introduced.
             */
            totalAmount +=
                    getOperatorCharge(
                            equipment,
                            booking.getDurationHours()
                    );
        }

        booking.setTotalAmount(totalAmount);

        booking.setStatus(
                BookingStatus.PENDING
        );

        booking.setCreatedAt(
                LocalDateTime.now()
        );

        Booking saved =
                bookingRepository.save(booking);

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Object getById(Long id) {

        Booking booking =
                bookingRepository.findById(id)
                        .orElseThrow(() ->
                                new NoSuchElementException(
                                        "Booking not found"
                                ));

        return toResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public Object getFarmerBookings(
            Long farmerId
    ) {

        return bookingRepository
                .findByFarmerIdOrderByCreatedAtDesc(
                        farmerId
                )
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Object getOwnerBookings(
            Long ownerId
    ) {

        return bookingRepository
                .findByEquipmentOwnerIdOrderByCreatedAtDesc(
                        ownerId
                )
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Object confirm(Long id) {

        Booking booking =
                getBooking(id);

        if (booking.getStatus()
                != BookingStatus.PENDING) {

            throw new IllegalStateException(
                    "Only pending bookings can be confirmed"
            );
        }

        booking.setStatus(
                BookingStatus.CONFIRMED
        );

        return toResponse(
                bookingRepository.save(booking)
        );
    }

    @Override
    public Object cancel(Long id) {

        Booking booking =
                getBooking(id);

        if (booking.getStatus()
                == BookingStatus.COMPLETED) {

            throw new IllegalStateException(
                    "Completed booking cannot be cancelled"
            );
        }

        if (booking.getStatus()
                == BookingStatus.CANCELLED) {

            throw new IllegalStateException(
                    "Booking is already cancelled"
            );
        }

        booking.setStatus(
                BookingStatus.CANCELLED
        );

        return toResponse(
                bookingRepository.save(booking)
        );
    }

    @Override
    public Object ongoing(Long id) {

        Booking booking =
                getBooking(id);

        if (booking.getStatus()
                != BookingStatus.CONFIRMED) {

            throw new IllegalStateException(
                    "Only confirmed bookings can become ongoing"
            );
        }

        booking.setStatus(
                BookingStatus.ONGOING
        );

        return toResponse(
                bookingRepository.save(booking)
        );
    }

    @Override
    public Object complete(Long id) {

        Booking booking =
                getBooking(id);

        if (booking.getStatus()
                != BookingStatus.ONGOING) {

            throw new IllegalStateException(
                    "Only ongoing bookings can be completed"
            );
        }

        booking.setStatus(
                BookingStatus.COMPLETED
        );

        return toResponse(
                bookingRepository.save(booking)
        );
    }

    private Booking getBooking(Long id) {

        return bookingRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Booking not found with id: "
                                        + id
                        ));
    }

    private void validateBookingRequest(
            BookingRequest request
    ) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Booking request cannot be null"
            );
        }

        if (request.getFarmerId() == null) {
            throw new IllegalArgumentException(
                    "Farmer ID is required"
            );
        }

        if (request.getEquipmentId() == null) {
            throw new IllegalArgumentException(
                    "Equipment ID is required"
            );
        }

        if (request.getFromDateTime() == null) {
            throw new IllegalArgumentException(
                    "Start date and time is required"
            );
        }

        if (request.getToDateTime() == null) {
            throw new IllegalArgumentException(
                    "End date and time is required"
            );
        }

        if (request.getFieldLocation() == null
                || request.getFieldLocation()
                .trim()
                .isEmpty()) {

            throw new IllegalArgumentException(
                    "Field location is required"
            );
        }
    }

    private void validateBookingTime(
            BookingRequest request
    ) {

        LocalDateTime from =
                request.getFromDateTime();

        LocalDateTime to =
                request.getToDateTime();

        if (!to.isAfter(from)) {
            throw new IllegalArgumentException(
                    "End time must be after start time"
            );
        }

        if (from.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Booking cannot be created for a past time"
            );
        }

        long minutes =
                Duration.between(from, to)
                        .toMinutes();

        if (minutes < 30) {
            throw new IllegalArgumentException(
                    "Minimum booking duration is 30 minutes"
            );
        }

        if (minutes > 24 * 60) {
            throw new IllegalArgumentException(
                    "Maximum booking duration is 24 hours"
            );
        }
    }

    private double calculateDurationHours(
            LocalDateTime from,
            LocalDateTime to
    ) {

        long minutes =
                Duration.between(from, to)
                        .toMinutes();

        return minutes / 60.0;
    }

    private double getOperatorCharge(
            Equipment equipment,
            double durationHours
    ) {

        /*
         * Operator pricing can later come from DB.
         *
         * For now there is no additional charge.
         */
        return 0.0;
    }

    private Map<String, Object> toResponse(
            Booking booking
    ) {

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put(
                "id",
                booking.getId()
        );

        response.put(
                "status",
                booking.getStatus()
        );

        response.put(
                "fromDateTime",
                booking.getFromDateTime()
        );

        response.put(
                "toDateTime",
                booking.getToDateTime()
        );

        response.put(
                "durationHours",
                booking.getDurationHours()
        );

        response.put(
                "fieldLocation",
                booking.getFieldLocation()
        );

        response.put(
                "operatorRequired",
                booking.getOperatorRequired()
        );

        response.put(
                "reason",
                booking.getReason()
        );

        response.put(
                "totalAmount",
                booking.getTotalAmount()
        );

        response.put(
                "createdAt",
                booking.getCreatedAt()
        );

        if (booking.getFarmer() != null) {

            response.put(
                    "farmerId",
                    booking.getFarmer().getId()
            );

            response.put(
                    "farmerName",
                    booking.getFarmer().getFullName()
            );
        }

        if (booking.getEquipment() != null) {

            Equipment equipment =
                    booking.getEquipment();

            response.put(
                    "equipmentId",
                    equipment.getId()
            );

            response.put(
                    "equipmentName",
                    equipment.getName()
            );

            response.put(
                    "equipmentType",
                    equipment.getType()
            );

            if (equipment.getOwner() != null) {

                response.put(
                        "ownerId",
                        equipment.getOwner().getId()
                );

                response.put(
                        "ownerName",
                        equipment.getOwner().getFullName()
                );
            }
        }

        return response;
    }
}