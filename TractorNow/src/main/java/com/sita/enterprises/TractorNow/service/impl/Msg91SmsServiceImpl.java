
package com.sita.enterprises.TractorNow.service.impl;

import com.sita.enterprises.TractorNow.service.SmsService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class Msg91SmsServiceImpl implements SmsService {

    private final RestTemplate restTemplate;

    @Value("${msg91.enabled:false}")
    private boolean enabled;

    @Value("${msg91.base-url}")
    private String baseUrl;

    @Value("${msg91.auth-key:}")
    private String authKey;

    @Value("${msg91.template-id:}")
    private String templateId;

    @Value("${msg91.country-code:91}")
    private String countryCode;

    public Msg91SmsServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void sendOtp(String mobileNumber, String otp) {

        if (!enabled) {
            throw new IllegalStateException(
                    "MSG91 SMS integration is disabled."
            );
        }

        if (authKey == null || authKey.isBlank()
                || templateId == null || templateId.isBlank()) {
            throw new IllegalStateException(
                    "MSG91 configuration is incomplete."
            );
        }

        String recipient = normalizeMobileNumber(mobileNumber);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("authkey", authKey);

        Map<String, Object> recipientData = new HashMap<>();
        recipientData.put("mobiles", recipient);

        // Must match the variable configured in your MSG91 template.
        recipientData.put("OTP", otp);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("template_id", templateId);
        requestBody.put("recipients", List.of(recipientData));

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response =
                    restTemplate.exchange(
                            baseUrl,
                            HttpMethod.POST,
                            request,
                            String.class
                    );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new IllegalStateException(
                        "MSG91 rejected the SMS request."
                );
            }

        } catch (RestClientResponseException ex) {

            // Do not log auth key, OTP, or complete request body.
            throw new IllegalStateException(
                    "MSG91 SMS API returned HTTP "
                            + ex.getStatusCode().value(),
                    ex
            );
        }
    }

    private String normalizeMobileNumber(String mobileNumber) {

        if (mobileNumber == null) {
            throw new IllegalArgumentException(
                    "Mobile number is required."
            );
        }

        String number = mobileNumber.replaceAll("[^0-9]", "");

        if (number.length() == 10) {
            number = countryCode + number;
        }

        if (!number.matches("91[6-9][0-9]{9}")) {
            throw new IllegalArgumentException(
                    "Invalid Indian mobile number."
            );
        }

        return number;
    }
}
