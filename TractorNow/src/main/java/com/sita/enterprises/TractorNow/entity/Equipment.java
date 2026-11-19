package com.sita.enterprises.TractorNow.entity;

import com.sita.enterprises.TractorNow.enums.EquipmentType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "equipment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EquipmentType type;

    private String description;

    private Double pricePerHour;

    private Boolean available;

    private Boolean operatorAvailable;

    private String village;

    private String district;

    private String state;

    private String pincode;

    private Double latitude;

    private Double longitude;

    private String imageUrl;

    private Double rating;

    private Integer reviewCount;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void beforeCreate() {

        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (available == null) {
            available = true;
        }

        if (operatorAvailable == null) {
            operatorAvailable = false;
        }

        if (rating == null) {
            rating = 0.0;
        }

        if (reviewCount == null) {
            reviewCount = 0;
        }

        if (active == null) {
            active = true;
        }
    }

    @PreUpdate
    public void beforeUpdate() {
        updatedAt = LocalDateTime.now();
    }
}