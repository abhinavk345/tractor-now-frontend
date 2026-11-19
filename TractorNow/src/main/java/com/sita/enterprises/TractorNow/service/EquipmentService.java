package com.sita.enterprises.TractorNow.service;

import com.tractornow.dto.equipment.EquipmentRequest;
import com.tractornow.enums.EquipmentType;

public interface EquipmentService {

    Object getAll();

    Object getById(Long id);

    Object search(
            String search,
            EquipmentType category,
            Boolean available,
            Boolean operator,
            Double maxDistance,
            Double maxPrice,
            String sortBy
    );

    Object nearby(
            Double latitude,
            Double longitude,
            Double radius
    );

    Object create(
            EquipmentRequest request
    );

    Object update(
            Long id,
            EquipmentRequest request
    );

    void delete(Long id);

    Object updateAvailability(
            Long id,
            Boolean available
    );

    Object getOwnerEquipment(
            Long ownerId
    );
}