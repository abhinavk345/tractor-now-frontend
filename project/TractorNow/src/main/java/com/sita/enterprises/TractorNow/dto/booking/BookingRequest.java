package com.sita.enterprises.TractorNow.dto.booking;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class BookingRequest {

    @NotNull
    private Long equipmentId;

    @NotNull
    private LocalDate bookingDate;

    @NotNull
    private LocalTime startTime;

    private String reason;

    private Long farmerId;
    @NotNull
    @Min(1)
    @Max(24)
    private Integer durationHours;

    @NotNull
    private Boolean operatorRequired;

    @NotBlank
    private String fieldLocation;

    private Double fieldLatitude;

    private Double fieldLongitude;

    private String additionalNote;

     private LocalDateTime fromDateTime;
     private  LocalDateTime toDateTime;

}