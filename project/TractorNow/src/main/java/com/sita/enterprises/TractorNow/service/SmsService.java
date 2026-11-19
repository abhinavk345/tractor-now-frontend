package com.sita.enterprises.TractorNow.service;

public interface SmsService {

    void sendOtp(String mobileNumber, String otp);
}