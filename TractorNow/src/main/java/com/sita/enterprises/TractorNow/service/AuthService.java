package com.sita.enterprises.TractorNow.service;

import com.sita.enterprises.TractorNow.dto.auth.LoginRequest;
import com.sita.enterprises.TractorNow.dto.auth.RegisterRequest;
import com.sita.enterprises.TractorNow.dto.auth.SendOtpRequest;
import com.sita.enterprises.TractorNow.dto.auth.VerifyOtpRequest;


public interface AuthService {

    Object registerFarmer(RegisterRequest request);

    Object registerOwner(RegisterRequest request);

    Object login(LoginRequest request);

    Object sendOtp(SendOtpRequest request);

    Object verifyOtp(VerifyOtpRequest request);
}