package com.sita.enterprises.TractorNow.service;

public interface OtpDeliveryService {

    void sendSms(String mobile, String otp);

    void sendEmail(String email, String otp);

}
