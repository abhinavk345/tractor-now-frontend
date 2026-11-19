package com.sita.enterprises.TractorNow.service;

import com.tractornow.enums.PaymentMethod;

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