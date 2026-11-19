package com.sita.enterprises.TractorNow.service;

import com.sita.enterprises.TractorNow.dto.auth.*;


public interface AuthService {

    Object registerFarmer(RegisterRequest request);

    Object registerOwner(RegisterRequest request);

    Object login(LoginRequest request);

    void sendPasswordResetOtp( ForgotPasswordRequest request);

    String verifyPasswordResetOtp(VerifyOtpRequest request);

    void resetPassword(String resetToken, ResetPasswordRequest request);
    Object sendOtp(SendOtpRequest request);
    Object verifyOtp(VerifyOtpRequest request);

}