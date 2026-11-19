package com.sita.enterprises.TractorNow.service.impl;

import com.tractornow.entity.Booking;
import com.tractornow.entity.Payment;
import com.tractornow.enums.BookingStatus;
import com.tractornow.enums.PaymentMethod;
import com.tractornow.enums.PaymentStatus;
import com.tractornow.repository.BookingRepository;
import com.tractornow.repository.PaymentRepository;
import com.tractornow.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl
        implements PaymentService {

    private final PaymentRepository paymentRepository;

    private final BookingRepository bookingRepository;

    @Override
    public Object createPayment(
            Long bookingId,
            PaymentMethod paymentMethod
    ) {

        Booking booking =
                bookingRepository.findById(
                        bookingId
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Booking not found"
                        )
                );

        if (booking.getStatus()
                == BookingStatus.CANCELLED) {

            throw new RuntimeException(
                    "Cancelled booking cannot be paid"
            );
        }

        if (paymentRepository
                .findByBookingId(bookingId)
                .isPresent()) {

            throw new RuntimeException(
                    "Payment already exists for this booking"
            );
        }

        Payment payment =
                Payment.builder()
                        .booking(booking)
                        .amount(
                                booking.getTotalAmount()
                        )
                        .paymentMethod(
                                paymentMethod
                        )
                        .status(
                                paymentMethod
                                        == PaymentMethod
                                        .PAY_AT_SERVICE
                                        ? PaymentStatus.PENDING
                                        : PaymentStatus.PENDING
                        )
                        .paymentDate(
                                LocalDateTime.now()
                        )
                        .build();

        Payment saved =
                paymentRepository.save(
                        payment
                );

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Object getPaymentById(
            Long id
    ) {

        Payment payment =
                paymentRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Payment not found"
                                )
                        );

        return toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public Object getPaymentByBooking(
            Long bookingId
    ) {

        Payment payment =
                paymentRepository
                        .findByBookingId(
                                bookingId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Payment not found"
                                )
                        );

        return toResponse(payment);
    }

    @Override
    public Object markSuccess(
            Long paymentId,
            String transactionId
    ) {

        Payment payment =
                paymentRepository.findById(
                        paymentId
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Payment not found"
                        )
                );

        payment.setStatus(
                PaymentStatus.SUCCESS
        );

        payment.setTransactionId(
                transactionId == null
                        ? UUID.randomUUID()
                                .toString()
                        : transactionId
        );

        payment.setPaymentDate(
                LocalDateTime.now()
        );

        return toResponse(
                paymentRepository.save(
                        payment
                )
        );
    }

    @Override
    public Object markFailed(
            Long paymentId
    ) {

        Payment payment =
                paymentRepository.findById(
                        paymentId
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Payment not found"
                        )
                );

        payment.setStatus(
                PaymentStatus.FAILED
        );

        return toResponse(
                paymentRepository.save(
                        payment
                )
        );
    }

    private Map<String, Object> toResponse(
            Payment payment
    ) {

        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "id",
                payment.getId()
        );

        response.put(
                "bookingId",
                payment.getBooking().getId()
        );

        response.put(
                "amount",
                payment.getAmount()
        );

        response.put(
                "paymentMethod",
                payment.getPaymentMethod()
        );

        response.put(
                "status",
                payment.getStatus()
        );

        response.put(
                "transactionId",
                payment.getTransactionId()
        );

        response.put(
                "paymentDate",
                payment.getPaymentDate()
        );

        return response;
    }
}