package com.sita.enterprises.TractorNow.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(

    @NotBlank
    String method,

    @NotBlank
    String identifier

) {}