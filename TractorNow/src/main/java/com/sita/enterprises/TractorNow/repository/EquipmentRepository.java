package com.sita.enterprises.TractorNow.repository;

import com.tractornow.entity.Equipment;
import com.tractornow.enums.EquipmentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipmentRepository
        extends JpaRepository<Equipment, Long> {

    List<Equipment> findByActiveTrue();

    List<Equipment> findByTypeAndActiveTrue(
            EquipmentType type
    );

    List<Equipment> findByVillageContainingIgnoreCaseAndActiveTrue(
            String village
    );

    List<Equipment> findByOwnerIdAndActiveTrue(
            Long ownerId
    );
}