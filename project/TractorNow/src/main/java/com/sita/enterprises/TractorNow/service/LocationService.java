package com.sita.enterprises.TractorNow.service;

public interface LocationService {

    Object findNearbyEquipment(
            Double latitude,
            Double longitude,
            Double radius
    );

    Object getEquipmentDistance(
            Long equipmentId,
            Double latitude,
            Double longitude
    );
}