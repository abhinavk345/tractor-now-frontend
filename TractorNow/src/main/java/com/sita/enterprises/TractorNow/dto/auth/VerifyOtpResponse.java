package com.sita.enterprises.TractorNow.dto.auth;

public record VerifyOtpResponse(
    boolean success,
    String message,
    String resetToken
) {}