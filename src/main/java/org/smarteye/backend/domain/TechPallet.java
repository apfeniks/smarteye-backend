package org.smarteye.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity @Table(name = "tech_pallets")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TechPallet {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rfid_tag")
    private String rfidTag;

    @Column(name = "status")
    private String status; // IN_CYCLE / ON_STOCK / RETIRED ...

    @Column(name = "length_mm")
    private Integer lengthMm;

    @Column(name = "width_mm")
    private Integer widthMm;

    @Column(name = "height_mm")
    private Integer heightMm;

    @Column(name = "tare_weight_kg")
    private Double tareWeightKg;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
