package com.sita.enterprises.TractorNow.repository;

import com.sita.enterprises.TractorNow.entity.Booking;
import com.sita.enterprises.TractorNow.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository
        extends JpaRepository<Booking, Long> {

    List<Booking> findByFarmerIdOrderByCreatedAtDesc(
            Long farmerId
    );

    List<Booking> findByOwnerIdOrderByCreatedAtDesc(
            Long ownerId
    );

    List<Booking> findByEquipmentId(
            Long equipmentId
    );

    List<Booking> findByStatus(
            BookingStatus status
    );

    @Query("""
    SELECT COUNT(b) > 0
    FROM Booking b
    WHERE b.equipment.id = :equipmentId
      AND b.status IN (
          com.sita.enterprises.TractorNow.enums.BookingStatus.PENDING,
          com.sita.enterprises.TractorNow.enums.BookingStatus.CONFIRMED,
          com.sita.enterprises.TractorNow.enums.BookingStatus.ONGOING
      )
      AND b.fromDateTime < :toDateTime
      AND b.toDateTime > :fromDateTime
""")
    boolean existsConflictingBooking(
            Long equipmentId,
            LocalDateTime fromDateTime,
            LocalDateTime toDateTime
    );

    Collection<Booking> findByEquipmentOwnerIdOrderByCreatedAtDesc(Long ownerId);
}