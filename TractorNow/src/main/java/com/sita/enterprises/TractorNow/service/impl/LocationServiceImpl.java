package com.sita.enterprises.TractorNow.service.impl;

import com.sita.enterprises.TractorNow.entity.Equipment;
import com.sita.enterprises.TractorNow.repository.EquipmentRepository;
import com.sita.enterprises.TractorNow.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationServiceImpl
        implements LocationService {

    private final EquipmentRepository
            equipmentRepository;

    @Override
    public Object findNearbyEquipment(
            Double latitude,
            Double longitude,
            Double radius
    ) {

        if (latitude == null
                || longitude == null) {

            throw new RuntimeException(
                    "Latitude and longitude are required"
            );
        }

        double maxRadius =
                radius == null
                        ? 50.0
                        : radius;

        return equipmentRepository
                .findByActiveTrue()
                .stream()
                .filter(e ->
                        e.getLatitude() != null
                                &&
                        e.getLongitude() != null
                )
                .map(equipment -> {

                    double distance =
                            distance(
                                    latitude,
                                    longitude,
                                    equipment.getLatitude(),
                                    equipment.getLongitude()
                            );

                    Map<String, Object> result =
                            new HashMap<>();

                    result.put(
                            "id",
                            equipment.getId()
                    );

                    result.put(
                            "name",
                            equipment.getName()
                    );

                    result.put(
                            "type",
                            equipment.getType()
                    );

                    result.put(
                            "pricePerHour",
                            equipment.getPricePerHour()
                    );

                    result.put(
                            "available",
                            equipment.getAvailable()
                    );

                    result.put(
                            "village",
                            equipment.getVillage()
                    );

                    result.put(
                            "district",
                            equipment.getDistrict()
                    );

                    result.put(
                            "latitude",
                            equipment.getLatitude()
                    );

                    result.put(
                            "longitude",
                            equipment.getLongitude()
                    );

                    result.put(
                            "distance",
                            Math.round(
                                    distance * 100
                            ) / 100.0
                    );

                    return result;
                })
                .filter(result ->
                        (Double) result.get(
                                "distance"
                        ) <= maxRadius
                )
                .sorted(
                        Comparator.comparing(
                                result ->
                                        (Double) result.get(
                                                "distance"
                                        )
                        )
                )
                .toList();
    }

    @Override
    public Object getEquipmentDistance(
            Long equipmentId,
            Double latitude,
            Double longitude
    ) {

        Equipment equipment =
                equipmentRepository.findById(
                        equipmentId
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Equipment not found"
                        )
                );

        if (equipment.getLatitude() == null
                || equipment.getLongitude() == null) {

            throw new RuntimeException(
                    "Equipment location not available"
            );
        }

        double distance =
                distance(
                        latitude,
                        longitude,
                        equipment.getLatitude(),
                        equipment.getLongitude()
                );

        return Map.of(
                "equipmentId",
                equipmentId,
                "distance",
                Math.round(
                        distance * 100
                ) / 100.0,
                "unit",
                "KM"
        );
    }

    private double distance(
            double lat1,
            double lon1,
            double lat2,
            double lon2
    ) {

        final double earthRadius = 6371.0;

        double dLat =
                Math.toRadians(
                        lat2 - lat1
                );

        double dLon =
                Math.toRadians(
                        lon2 - lon1
                );

        double a =
                Math.sin(dLat / 2)
                        *
                        Math.sin(dLat / 2)
                        +
                        Math.cos(
                                Math.toRadians(lat1)
                        )
                        *
                        Math.cos(
                                Math.toRadians(lat2)
                        )
                        *
                        Math.sin(dLon / 2)
                        *
                        Math.sin(dLon / 2);

        double c =
                2 * Math.atan2(
                        Math.sqrt(a),
                        Math.sqrt(1 - a)
                );

        return earthRadius * c;
    }
}