package com.sita.enterprises.TractorNow.service;

public interface OtpService {

    String generateAndSaveOtp(
            String mobile,
            String email
    );

    boolean verifyOtp(
            String mobile,
            String email,
            String otp
    );

    void invalidateOtp(
            String mobile,
            String email
    );
}