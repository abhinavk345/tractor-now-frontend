package com.sita.enterprises.TractorNow.repository;


import com.sita.enterprises.TractorNow.entity.PasswordResetChallenge;
import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PasswordResetChallengeRepository
        extends JpaRepository<PasswordResetChallenge, Long> {

//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @Query("""
//        SELECT p FROM PasswordResetChallenge p
//        WHERE p.method = :method
//        AND p.identifier = :identifier
//        ORDER BY p.id DESC
//        """)
//    Optional<PasswordResetChallenge> findLatestForUpdate(
//        @Param("method") String method,
//        @Param("identifier") String identifier
//    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PasswordResetChallenge>
    findFirstByMethodAndIdentifierOrderByIdDesc(
            String method,
            String identifier
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT p FROM PasswordResetChallenge p
        WHERE p.resetTokenHash = :tokenHash
        """)
    Optional<PasswordResetChallenge> findByTokenHashForUpdate(
        @Param("tokenHash") String tokenHash
    );

    @Modifying
    @Query("""
        UPDATE PasswordResetChallenge p
        SET p.usedAt = CURRENT_TIMESTAMP
        WHERE p.userId = :userId
        AND p.usedAt IS NULL
        """)
    int invalidateAllForUser(
        @Param("userId") Long userId
    );
}
