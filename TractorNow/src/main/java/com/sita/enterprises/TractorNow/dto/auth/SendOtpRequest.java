package com.sita.enterprises.TractorNow.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendOtpRequest {

    private String mobile;

    private String email;
}