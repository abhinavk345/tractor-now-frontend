package com.sita.enterprises.TractorNow.service.impl;

import com.sita.enterprises.TractorNow.entity.OtpVerification;
import com.sita.enterprises.TractorNow.repository.OtpVerificationRepository;
import com.sita.enterprises.TractorNow.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class OtpServiceImpl implements OtpService {

    private final OtpVerificationRepository otpRepository;

    @Override
    public String generateAndSaveOtp(
            String mobile,
            String email
    ) {

        String otp = String.format(
                "%06d",
                new Random().nextInt(1_000_000)
        );

        OtpVerification verification =
                OtpVerification.builder()
                        .mobile(mobile)
                        .email(email)
                        .otp(otp)
                        .expiresAt(
                                LocalDateTime.now()
                                        .plusMinutes(5)
                        )
                        .verified(false)
                        .attempts(0)
                        .createdAt(
                                LocalDateTime.now()
                        )
                        .build();

        otpRepository.save(verification);

        /*
         * Development:
         * return OTP so React can be tested.
         *
         * Production:
         * send through SMS/email provider
         * and don't return the OTP.
         */
        return otp;
    }

    @Override
    public boolean verifyOtp(
            String mobile,
            String email,
            String otp
    ) {

        OtpVerification verification;

        if (mobile != null && !mobile.isBlank()) {

            verification =
                    otpRepository
                            .findTopByMobileOrderByCreatedAtDesc(
                                    mobile
                            )
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "OTP not found"
                                    )
                            );

        } else if (email != null
                && !email.isBlank()) {

            verification =
                    otpRepository
                            .findTopByEmailOrderByCreatedAtDesc(
                                    email
                            )
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "OTP not found"
                                    )
                            );

        } else {

            throw new RuntimeException(
                    "Mobile or email is required"
            );
        }

        int attempts =
                verification.getAttempts() == null
                        ? 0
                        : verification.getAttempts();

        if (attempts >= 5) {
            throw new RuntimeException(
                    "Maximum OTP attempts exceeded"
            );
        }

        if (Boolean.TRUE.equals(
                verification.getVerified()
        )) {
            throw new RuntimeException(
                    "OTP already used"
            );
        }

        if (verification.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException(
                    "OTP expired"
            );
        }

        if (!verification.getOtp().equals(otp)) {

            verification.setAttempts(
                    attempts + 1
            );

            otpRepository.save(
                    verification
            );

            throw new RuntimeException(
                    "Invalid OTP"
            );
        }

        verification.setVerified(true);

        otpRepository.save(
                verification
        );

        return true;
    }

    @Override
    public void invalidateOtp(
            String mobile,
            String email
    ) {

        if (mobile != null && !mobile.isBlank()) {

            otpRepository
                    .findTopByMobileOrderByCreatedAtDesc(
                            mobile
                    )
                    .ifPresent(otp -> {
                        otp.setVerified(true);
                        otpRepository.save(otp);
                    });
        }
    }
}