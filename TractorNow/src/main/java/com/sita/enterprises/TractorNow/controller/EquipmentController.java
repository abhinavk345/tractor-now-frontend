package com.sita.enterprises.TractorNow.controller;

import com.sita.enterprises.TractorNow.dto.equipment.EquipmentRequest;
import com.sita.enterprises.TractorNow.enums.EquipmentType;
import com.sita.enterprises.TractorNow.service.EquipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @GetMapping
    public ResponseEntity<?> getAll() {

        return ResponseEntity.ok(
                equipmentService.getAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                equipmentService.getById(id)
        );
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(
            @RequestParam(required = false)
            String search,

            @RequestParam(required = false)
            EquipmentType category,

            @RequestParam(required = false)
            Boolean available,

            @RequestParam(required = false)
            Boolean operator,

            @RequestParam(required = false)
            Double maxDistance,

            @RequestParam(required = false)
            Double maxPrice,

            @RequestParam(required = false)
            String sortBy) {

        return ResponseEntity.ok(
                equipmentService.search(
                        search,
                        category,
                        available,
                        operator,
                        maxDistance,
                        maxPrice,
                        sortBy
                )
        );
    }

    @GetMapping("/nearby")
    public ResponseEntity<?> nearby(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "50")
            Double radius) {

        return ResponseEntity.ok(
                equipmentService.nearby(
                        latitude,
                        longitude,
                        radius
                )
        );
    }

    @PostMapping
    public ResponseEntity<?> create(
            @Valid @RequestBody
            EquipmentRequest request) {

        return ResponseEntity.ok(
                equipmentService.create(request)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @Valid @RequestBody
            EquipmentRequest request) {

        return ResponseEntity.ok(
                equipmentService.update(
                        id,
                        request
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id) {

        equipmentService.delete(id);

        return ResponseEntity.ok(
                "Equipment deleted successfully"
        );
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<?> availability(
            @PathVariable Long id,
            @RequestParam Boolean available) {

        return ResponseEntity.ok(
                equipmentService.updateAvailability(
                        id,
                        available
                )
        );
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<?> ownerEquipment(
            @PathVariable Long ownerId) {

        return ResponseEntity.ok(
                equipmentService.getOwnerEquipment(
                        ownerId
                )
        );
    }
}