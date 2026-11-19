package com.sita.enterprises.TractorNow.repository;

import com.sita.enterprises.TractorNow.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository
        extends JpaRepository<Conversation, Long> {

    List<Conversation> findByFarmerIdOrOwnerId(
            Long farmerId,
            Long ownerId
    );

    Optional<Conversation> findByFarmerIdAndOwnerId(
            Long farmerId,
            Long ownerId
    );

    boolean existsByFarmerIdAndOwnerId(
            Long farmerId,
            Long ownerId
    );

    List<Conversation> findByFarmerIdOrderByCreatedAtDesc(
            Long farmerId
    );

    List<Conversation> findByOwnerIdOrderByCreatedAtDesc(
            Long ownerId
    );
}