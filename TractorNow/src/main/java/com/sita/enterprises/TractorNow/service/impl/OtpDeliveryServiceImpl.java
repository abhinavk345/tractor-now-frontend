package com.sita.enterprises.TractorNow.service.impl;


import com.sita.enterprises.TractorNow.service.OtpDeliveryService;
import lombok.RequiredArgsConstructor;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OtpDeliveryServiceImpl
        implements OtpDeliveryService {

    private final JavaMailSender mailSender;

    @Override
    public void sendEmail(String email, String otp) {

        SimpleMailMessage message =
            new SimpleMailMessage();

        message.setTo(email);
        message.setSubject("TractorNow Password Reset OTP");

        message.setText(
            "Your TractorNow password reset OTP is: "
            + otp
            + "\n\nThis OTP is valid for 5 minutes."
            + "\nDo not share this OTP with anyone."
        );

        mailSender.send(message);
    }

    @Override
    public void sendSms(String mobile, String otp) {

        // Integrate your SMS provider here.
        // Never log the OTP or include it in an exception.

        throw new UnsupportedOperationException(
            "Configure your SMS provider before enabling mobile OTP."
        );
    }
}
