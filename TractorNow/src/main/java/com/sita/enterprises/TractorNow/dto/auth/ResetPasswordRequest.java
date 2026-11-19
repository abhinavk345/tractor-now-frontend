package com.sita.enterprises.TractorNow.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record ResetPasswordRequest(

        @NotBlank(message = "New password is required")
        @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
        String newPassword,

        @NotBlank(message = "Confirm password is required")
        String confirmPassword

) {}