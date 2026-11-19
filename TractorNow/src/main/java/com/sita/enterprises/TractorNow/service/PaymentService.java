package com.sita.enterprises.TractorNow.service;

import com.sita.enterprises.TractorNow.enums.PaymentMethod;

public interface PaymentService {

    Object createPayment(
            Long bookingId,
            PaymentMethod paymentMethod
    );

    Object getPaymentById(Long id);

    Object getPaymentByBooking(
            Long bookingId
    );

    Object markSuccess(
            Long paymentId,
            String transactionId
    );

    Object markFailed(
            Long paymentId
    );
}