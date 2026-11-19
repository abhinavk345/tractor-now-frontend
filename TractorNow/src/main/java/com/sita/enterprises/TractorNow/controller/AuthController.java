package com.sita.enterprises.TractorNow.controller;

import com.sita.enterprises.TractorNow.dto.auth.LoginRequest;
import com.sita.enterprises.TractorNow.dto.auth.RegisterRequest;
import com.sita.enterprises.TractorNow.dto.auth.SendOtpRequest;
import com.sita.enterprises.TractorNow.dto.auth.VerifyOtpRequest;
import com.sita.enterprises.TractorNow.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/farmer")
    public ResponseEntity<?> registerFarmer(
            @Valid @RequestBody RegisterRequest request) {

        return ResponseEntity.ok(
                authService.registerFarmer(request)
        );
    }

    @PostMapping("/register/owner")
    public ResponseEntity<?> registerOwner(
            @Valid @RequestBody RegisterRequest request) {

        return ResponseEntity.ok(
                authService.registerOwner(request)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(
                authService.login(request)
        );
    }

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(
            @RequestBody SendOtpRequest request) {

        return ResponseEntity.ok(
                authService.sendOtp(request)
        );
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {

        return ResponseEntity.ok(
                authService.verifyOtp(request)
        );
    }
}