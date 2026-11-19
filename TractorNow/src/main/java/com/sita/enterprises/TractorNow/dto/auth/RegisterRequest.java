package com.sita.enterprises.TractorNow.dto.auth;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    private String fullName;

    @NotBlank
    @Pattern(
        regexp = "^[6-9][0-9]{9}$",
        message = "Invalid mobile number"
    )
    private String mobile;

    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 100)
    private String password;

    @NotBlank
    private String confirmPassword;

    private String village;

    private String district;

    private String state;

    @Pattern(
        regexp = "^[0-9]{6}$",
        message = "Invalid pincode"
    )
    private String pincode;

    private Double latitude;

    private Double longitude;

    @AssertTrue(
        message = "Terms must be accepted"
    )
    private boolean termsAccepted;
}