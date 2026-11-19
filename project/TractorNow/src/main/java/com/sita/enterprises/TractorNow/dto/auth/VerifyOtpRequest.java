package com.sita.enterprises.TractorNow.dto.auth;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyOtpRequest {

    private String mobile;

    private String email;

    private String method;

@NotBlank
String identifier;

    @NotBlank
    @Pattern(
        regexp = "^[0-9]{6}$",
        message = "OTP must contain 6 digits"
    )
    private String otp;
}