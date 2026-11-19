package com.sita.enterprises.TractorNow.service.impl;

import com.sita.enterprises.TractorNow.entity.Booking;
import com.sita.enterprises.TractorNow.enums.BookingStatus;
import com.sita.enterprises.TractorNow.repository.BookingRepository;
import com.sita.enterprises.TractorNow.repository.EquipmentRepository;
import com.sita.enterprises.TractorNow.repository.UserRepository;
import com.sita.enterprises.TractorNow.service.OwnerDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OwnerDashboardServiceImpl
        implements OwnerDashboardService {

    private final UserRepository userRepository;

    private final EquipmentRepository
            equipmentRepository;

    private final BookingRepository
            bookingRepository;

    @Override
    public Object getDashboard(
            Long ownerId
    ) {

        validateOwner(ownerId);

        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "equipment",
                getEquipment(ownerId)
        );

        response.put(
                "bookings",
                getBookings(ownerId)
        );

        response.put(
                "earnings",
                getEarnings(ownerId)
        );

        response.put(
                "statistics",
                getStatistics(ownerId)
        );

        return response;
    }

    @Override
    public Object getEquipment(
            Long ownerId
    ) {

        return equipmentRepository
                .findByOwnerIdAndActiveTrue(
                        ownerId
                )
                .stream()
                .map(equipment ->
                        Map.of(
                                "id",
                                equipment.getId(),
                                "name",
                                equipment.getName(),
                                "type",
                                equipment.getType(),
                                "pricePerHour",
                                equipment.getPricePerHour(),
                                "available",
                                equipment.getAvailable(),
                                "rating",
                                equipment.getRating()
                        )
                )
                .toList();
    }

    @Override
    public Object getBookings(
            Long ownerId
    ) {

        return bookingRepository
                .findByOwnerIdOrderByCreatedAtDesc(
                        ownerId
                )
                .stream()
                .map(booking ->
                        Map.of(
                                "id",
                                booking.getId(),
                                "bookingNumber",
                                booking.getBookingNumber(),
                                "equipment",
                                booking.getEquipment()
                                        .getName(),
                                "farmer",
                                booking.getFarmer()
                                        .getFullName(),
                                "date",
                                booking.getBookingDate(),
                                "status",
                                booking.getStatus(),
                                "amount",
                                booking.getTotalAmount()
                        )
                )
                .toList();
    }

    @Override
    public Object getEarnings(
            Long ownerId
    ) {

        var bookings =
                bookingRepository
                        .findByOwnerIdOrderByCreatedAtDesc(
                                ownerId
                        );

        double total =
                bookings.stream()
                        .filter(b ->
                                b.getStatus()
                                        == BookingStatus
                                        .COMPLETED
                        )
                        .mapToDouble(
                                Booking::getTotalAmount
                        )
                        .sum();

        return Map.of(
                "totalEarnings",
                total
        );
    }

    @Override
    public Object getStatistics(
            Long ownerId
    ) {

        var bookings =
                bookingRepository
                        .findByOwnerIdOrderByCreatedAtDesc(
                                ownerId
                        );

        long total =
                bookings.size();

        long pending =
                bookings.stream()
                        .filter(b ->
                                b.getStatus()
                                        == BookingStatus
                                        .PENDING
                        )
                        .count();

        long confirmed =
                bookings.stream()
                        .filter(b ->
                                b.getStatus()
                                        == BookingStatus
                                        .CONFIRMED
                        )
                        .count();

        long ongoing =
                bookings.stream()
                        .filter(b ->
                                b.getStatus()
                                        == BookingStatus
                                        .ONGOING
                        )
                        .count();

        long completed =
                bookings.stream()
                        .filter(b ->
                                b.getStatus()
                                        == BookingStatus
                                        .COMPLETED
                        )
                        .count();

        long cancelled =
                bookings.stream()
                        .filter(b ->
                                b.getStatus()
                                        == BookingStatus
                                        .CANCELLED
                        )
                        .count();

        return Map.of(
                "totalBookings",
                total,
                "pendingBookings",
                pending,
                "confirmedBookings",
                confirmed,
                "ongoingBookings",
                ongoing,
                "completedBookings",
                completed,
                "cancelledBookings",
                cancelled
        );
    }

    private void validateOwner(
            Long ownerId
    ) {

        var owner =
                userRepository.findById(ownerId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Owner not found"
                                )
                        );

        if (owner.getRole()
                != com.sita.enterprises.TractorNow.enums.UserRole
                .EQUIPMENT_OWNER) {

            throw new RuntimeException(
                    "User is not an equipment owner"
            );
        }
    }
}