package com.sita.enterprises.TractorNow.controller;

import com.sita.enterprises.TractorNow.dto.ApiResponse;
import com.sita.enterprises.TractorNow.dto.auth.*;
import com.sita.enterprises.TractorNow.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    // Set true in production behind HTTPS.
    @org.springframework.beans.factory.annotation.Value(
            "${app.security.cookie-secure:false}"
    )
    private boolean cookieSecure;

    private static final String RESET_COOKIE =
            "TRACTORNOW_RESET_SESSION";

    private final AuthService authService;

    @PostMapping("/register/farmer")
    public ResponseEntity<?> registerFarmer(
            @Valid @RequestBody RegisterRequest request) {
log.info("api started:registerFarmer");
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

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        authService.sendPasswordResetOtp(request);

        return ResponseEntity.ok(
                new ApiResponse(
                        true,
                        "If an account exists, an OTP has been sent."
                )
        );
    }

    // ===================================
    // VERIFY OTP
    // ===================================

    @PostMapping("/verify-reset-otp")
    public ResponseEntity<ApiResponse> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {

        String resetToken =
                authService.verifyPasswordResetOtp(request);

        ResponseCookie cookie =
                ResponseCookie.from(RESET_COOKIE, resetToken)
                        .httpOnly(true)
                        .secure(cookieSecure)
                        .sameSite("Lax")
                        .path("/api/auth/reset-password")
                        .maxAge(Duration.ofMinutes(10))
                        .build();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        cookie.toString()
                )
                .body(
                        new ApiResponse(
                                true,
                                "OTP verified successfully."
                        )
                );
    }

    // ===================================
    // RESET PASSWORD
    // ===================================

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(
            @CookieValue(
                    name = RESET_COOKIE,
                    required = false
            ) String resetToken,
            @Valid @RequestBody ResetPasswordRequest request,
            HttpServletResponse servletResponse) {

        authService.resetPassword(resetToken, request);

        // Delete reset cookie after successful reset
        ResponseCookie deleteCookie =
                ResponseCookie.from(RESET_COOKIE, "")
                        .httpOnly(true)
                        .secure(cookieSecure)
                        .sameSite("Lax")
                        .path("/api/auth/reset-password")
                        .maxAge(Duration.ZERO)
                        .build();

        servletResponse.addHeader(
                HttpHeaders.SET_COOKIE,
                deleteCookie.toString()
        );

        return ResponseEntity.ok(
                new ApiResponse(
                        true,
                        "Password updated successfully."
                )
        );
    }


}