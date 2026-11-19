package com.sita.enterprises.TractorNow.service.impl;

import com.sita.enterprises.TractorNow.dto.equipment.EquipmentRequest;
import com.sita.enterprises.TractorNow.entity.Equipment;
import com.sita.enterprises.TractorNow.entity.User;
import com.sita.enterprises.TractorNow.enums.EquipmentType;
import com.sita.enterprises.TractorNow.enums.UserRole;
import com.sita.enterprises.TractorNow.repository.EquipmentRepository;
import com.sita.enterprises.TractorNow.repository.UserRepository;
import com.sita.enterprises.TractorNow.service.EquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Object getAll() {

        return equipmentRepository
                .findByActiveTrue()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Object getById(Long id) {

        Equipment equipment = equipmentRepository
                .findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Equipment not found with id: " + id
                        ));

        return toResponse(equipment);
    }

    @Override
    @Transactional(readOnly = true)
    public Object search(
            String search,
            EquipmentType category,
            Boolean available,
            Boolean operator,
            Double maxDistance,
            Double maxPrice,
            String sortBy
    ) {

        List<Equipment> equipmentList =
                equipmentRepository.findByActiveTrue();

        String searchText =
                search == null
                        ? ""
                        : search.trim().toLowerCase();

        List<Map<String, Object>> result =
                equipmentList.stream()

                        .filter(e -> {

                            if (searchText.isEmpty()) {
                                return true;
                            }

                            return contains(e.getName(), searchText)
                                    || contains(e.getVillage(), searchText)
                                    || contains(e.getDistrict(), searchText);
                        })

                        .filter(e ->
                                category == null
                                        || e.getType() == category
                        )

                        .filter(e ->
                                available == null
                                        || Objects.equals(
                                                e.getAvailable(),
                                                available
                                        )
                        )

                        .filter(e ->
                                operator == null
                                        || Objects.equals(
                                                e.getOperatorAvailable(),
                                                operator
                                        )
                        )

                        .filter(e ->
                                maxPrice == null
                                        || e.getPricePerHour() <= maxPrice
                        )

                        .map(this::toResponse)

                        .filter(e -> {

                            if (maxDistance == null) {
                                return true;
                            }

                            Object distance =
                                    e.get("distance");

                            if (distance == null) {
                                return true;
                            }

                            return ((Number) distance)
                                    .doubleValue()
                                    <= maxDistance;
                        })

                        .collect(Collectors.toList());

        sortResult(result, sortBy);

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Object nearby(
            Double latitude,
            Double longitude,
            Double radius
    ) {

        validateCoordinates(latitude, longitude);

        double searchRadius =
                radius == null ? 20.0 : radius;

        if (searchRadius <= 0) {
            throw new IllegalArgumentException(
                    "Radius must be greater than zero"
            );
        }

        List<Map<String, Object>> result =
                equipmentRepository
                        .findByActiveTrue()
                        .stream()
                        .map(e -> {

                            Map<String, Object> response =
                                    toResponse(e);

                            if (e.getLatitude() != null
                                    && e.getLongitude() != null) {

                                double distance =
                                        calculateDistance(
                                                latitude,
                                                longitude,
                                                e.getLatitude(),
                                                e.getLongitude()
                                        );

                                response.put(
                                        "distance",
                                        Math.round(distance * 100.0)
                                                / 100.0
                                );
                            }

                            return response;
                        })

                        .filter(e -> {

                            Object distance =
                                    e.get("distance");

                            return distance != null
                                    && ((Number) distance)
                                    .doubleValue()
                                    <= searchRadius;
                        })

                        .sorted(
                                Comparator.comparingDouble(
                                        e -> ((Number)
                                                e.get("distance"))
                                                .doubleValue()
                                )
                        )

                        .collect(Collectors.toList());

        return result;
    }

    @Override
    public Object create(EquipmentRequest request) {

        validateEquipmentRequest(request);

        User owner = userRepository
                .findById(request.getOwnerId())
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Owner not found"
                        ));

        if (owner.getRole() != UserRole.EQUIPMENT_OWNER) {
            throw new IllegalArgumentException(
                    "Only equipment owners can add equipment"
            );
        }

        Equipment equipment = new Equipment();

        mapRequestToEntity(request, equipment);

        equipment.setOwner(owner);
        equipment.setActive(true);

        if (equipment.getAvailable() == null) {
            equipment.setAvailable(true);
        }

        Equipment saved =
                equipmentRepository.save(equipment);

        return toResponse(saved);
    }

    @Override
    public Object update(
            Long id,
            EquipmentRequest request
    ) {

        validateEquipmentRequest(request);

        Equipment equipment =
                equipmentRepository.findById(id)
                        .orElseThrow(() ->
                                new NoSuchElementException(
                                        "Equipment not found"
                                ));

        if (request.getOwnerId() != null) {

            User owner =
                    userRepository.findById(
                                    request.getOwnerId()
                            )
                            .orElseThrow(() ->
                                    new NoSuchElementException(
                                            "Owner not found"
                                    ));

            if (owner.getRole()
                    != UserRole.EQUIPMENT_OWNER) {

                throw new IllegalArgumentException(
                        "Invalid equipment owner"
                );
            }

            equipment.setOwner(owner);
        }

        mapRequestToEntity(request, equipment);

        Equipment saved =
                equipmentRepository.save(equipment);

        return toResponse(saved);
    }

    @Override
    public void delete(Long id) {

        Equipment equipment =
                equipmentRepository.findById(id)
                        .orElseThrow(() ->
                                new NoSuchElementException(
                                        "Equipment not found"
                                ));

        // Soft delete
        equipment.setActive(false);
        equipment.setAvailable(false);

        equipmentRepository.save(equipment);
    }

    @Override
    public Object updateAvailability(
            Long id,
            Boolean available
    ) {

        if (available == null) {
            throw new IllegalArgumentException(
                    "Availability value is required"
            );
        }

        Equipment equipment =
                equipmentRepository.findById(id)
                        .orElseThrow(() ->
                                new NoSuchElementException(
                                        "Equipment not found"
                                ));

        equipment.setAvailable(available);

        return toResponse(
                equipmentRepository.save(equipment)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Object getOwnerEquipment(Long ownerId) {

        userRepository.findById(ownerId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Owner not found"
                        ));

        return equipmentRepository
                .findByOwnerIdAndActiveTrue(ownerId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private void mapRequestToEntity(
            EquipmentRequest request,
            Equipment equipment
    ) {

        equipment.setName(request.getName());
        equipment.setType(request.getType());

        equipment.setDescription(
                request.getDescription()
        );

        equipment.setPricePerHour(
                request.getPricePerHour()
        );

        equipment.setVillage(
                request.getVillage()
        );

        equipment.setDistrict(
                request.getDistrict()
        );

        equipment.setState(
                request.getState()
        );

        equipment.setLatitude(
                request.getLatitude()
        );

        equipment.setLongitude(
                request.getLongitude()
        );

        equipment.setOperatorAvailable(
                request.getOperatorAvailable()
        );

        equipment.setImageUrl(
                request.getImageUrl()
        );

        if (request.getAvailable() != null) {
            equipment.setAvailable(
                    request.getAvailable()
            );
        }
    }

    private Map<String, Object> toResponse(
            Equipment equipment
    ) {

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put("id", equipment.getId());
        response.put("name", equipment.getName());
        response.put("type", equipment.getType());
        response.put(
                "description",
                equipment.getDescription()
        );

        response.put(
                "pricePerHour",
                equipment.getPricePerHour()
        );

        response.put(
                "available",
                equipment.getAvailable()
        );

        response.put(
                "operatorAvailable",
                equipment.getOperatorAvailable()
        );

        response.put(
                "village",
                equipment.getVillage()
        );

        response.put(
                "district",
                equipment.getDistrict()
        );

        response.put(
                "state",
                equipment.getState()
        );

        response.put(
                "latitude",
                equipment.getLatitude()
        );

        response.put(
                "longitude",
                equipment.getLongitude()
        );

        response.put(
                "imageUrl",
                equipment.getImageUrl()
        );

        if (equipment.getOwner() != null) {

            response.put(
                    "ownerId",
                    equipment.getOwner().getId()
            );

            response.put(
                    "ownerName",
                    equipment.getOwner().getFullName()
            );
        }

        return response;
    }

    private void sortResult(
            List<Map<String, Object>> result,
            String sortBy
    ) {

        if (sortBy == null) {
            return;
        }

        switch (sortBy.toLowerCase()) {

            case "nearest":
                result.sort(
                        Comparator.comparingDouble(
                                e -> getDouble(
                                        e.get("distance"),
                                        Double.MAX_VALUE
                                )
                        )
                );
                break;

            case "lowest":
            case "lowestprice":
            case "price_low":
                result.sort(
                        Comparator.comparingDouble(
                                e -> getDouble(
                                        e.get("pricePerHour"),
                                        Double.MAX_VALUE
                                )
                        )
                );
                break;

            case "highest":
            case "highestprice":
            case "price_high":
                result.sort(
                        Comparator.comparingDouble(
                                e -> getDouble(
                                        e.get("pricePerHour"),
                                        0
                                )
                        ).reversed()
                );
                break;

            default:
                break;
        }
    }

    private boolean contains(
            String value,
            String search
    ) {

        return value != null
                && value.toLowerCase()
                .contains(search);
    }

    private double getDouble(
            Object value,
            double defaultValue
    ) {

        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        return defaultValue;
    }

    private double calculateDistance(
            double lat1,
            double lon1,
            double lat2,
            double lon2
    ) {

        final double earthRadius = 6371.0;

        double latDistance =
                Math.toRadians(lat2 - lat1);

        double lonDistance =
                Math.toRadians(lon2 - lon1);

        double a =
                Math.sin(latDistance / 2)
                        * Math.sin(latDistance / 2)
                        + Math.cos(Math.toRadians(lat1))
                        * Math.cos(Math.toRadians(lat2))
                        * Math.sin(lonDistance / 2)
                        * Math.sin(lonDistance / 2);

        double c =
                2 * Math.atan2(
                        Math.sqrt(a),
                        Math.sqrt(1 - a)
                );

        return earthRadius * c;
    }

    private void validateCoordinates(
            Double latitude,
            Double longitude
    ) {

        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException(
                    "Latitude and longitude are required"
            );
        }

        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException(
                    "Invalid latitude"
            );
        }

        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException(
                    "Invalid longitude"
            );
        }
    }

    private void validateEquipmentRequest(
            EquipmentRequest request
    ) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Equipment request cannot be null"
            );
        }

        if (isBlank(request.getName())) {
            throw new IllegalArgumentException(
                    "Equipment name is required"
            );
        }

        if (request.getType() == null) {
            throw new IllegalArgumentException(
                    "Equipment type is required"
            );
        }

        if (request.getPricePerHour() == null
                || request.getPricePerHour() <= 0) {

            throw new IllegalArgumentException(
                    "Price per hour must be greater than zero"
            );
        }

        if (request.getOwnerId() == null) {
            throw new IllegalArgumentException(
                    "Owner ID is required"
            );
        }
    }

    private boolean isBlank(String value) {
        return value == null
                || value.trim().isEmpty();
    }
}