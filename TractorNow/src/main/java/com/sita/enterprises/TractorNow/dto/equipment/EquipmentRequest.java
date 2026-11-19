package com.sita.enterprises.TractorNow.dto.equipment;

import com.sita.enterprises.TractorNow.enums.EquipmentType;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class EquipmentRequest {

    @NotBlank
    private String name;

    @NotNull
    private EquipmentType type;

    private String description;

    @NotNull
    @Positive
    private Double pricePerHour;

    private Boolean operatorAvailable;

    @NotBlank
    private String village;

    private String district;

    private String state;

    private String pincode;

    private Double latitude;

    private Double longitude;

    private String imageUrl;
}