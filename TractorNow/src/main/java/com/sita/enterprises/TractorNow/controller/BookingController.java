package com.sita.enterprises.TractorNow.controller;

import com.tractornow.dto.booking.BookingRequest;
import com.tractornow.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<?> create(
            @Valid @RequestBody
            BookingRequest request) {

        return ResponseEntity.ok(
                bookingService.create(request)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                bookingService.getById(id)
        );
    }

    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<?> farmerBookings(
            @PathVariable Long farmerId) {

        return ResponseEntity.ok(
                bookingService.getFarmerBookings(
                        farmerId
                )
        );
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<?> ownerBookings(
            @PathVariable Long ownerId) {

        return ResponseEntity.ok(
                bookingService.getOwnerBookings(
                        ownerId
                )
        );
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<?> confirm(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                bookingService.confirm(id)
        );
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                bookingService.cancel(id)
        );
    }

    @PutMapping("/{id}/ongoing")
    public ResponseEntity<?> ongoing(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                bookingService.ongoing(id)
        );
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<?> complete(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                bookingService.complete(id)
        );
    }
}