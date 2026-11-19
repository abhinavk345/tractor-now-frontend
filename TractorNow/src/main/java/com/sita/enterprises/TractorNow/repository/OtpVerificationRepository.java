package com.sita.enterprises.TractorNow.repository;


import com.sita.enterprises.TractorNow.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpVerificationRepository
        extends JpaRepository<OtpVerification, Long> {

    Optional<OtpVerification>
    findTopByMobileOrderByCreatedAtDesc(
            String mobile
    );

    Optional<OtpVerification>
    findTopByEmailOrderByCreatedAtDesc(
            String email
    );
}