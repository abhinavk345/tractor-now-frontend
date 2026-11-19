package com.sita.enterprises.TractorNow.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(
    name = "password_reset_challenges",
    indexes = {
        @Index(
            name = "idx_reset_identifier",
            columnList = "method, identifier"
        ),
        @Index(
            name = "idx_reset_user",
            columnList = "user_id"
        )
    }
)
public class PasswordResetChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, length = 10)
    private String method;

    @Column(nullable = false, length = 254)
    private String identifier;

    @Column(name = "otp_hash", length = 64)
    private String otpHash;

    private LocalDateTime otpExpiresAt;

    private LocalDateTime lastSentAt;

    private LocalDateTime verifiedAt;

    private int attempts;

    @Column(name = "reset_token_hash", length = 64)
    private String resetTokenHash;

    private LocalDateTime resetTokenExpiresAt;

    private LocalDateTime usedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Version
    private Long version;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

}