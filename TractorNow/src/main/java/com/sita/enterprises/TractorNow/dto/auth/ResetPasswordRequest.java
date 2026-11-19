package com.sita.enterprises.TractorNow.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(

    @NotBlank
    String resetToken,

    @NotBlank
    @Size(min = 8, max = 64)
    String newPassword,

    @NotBlank
    String confirmPassword

) {}