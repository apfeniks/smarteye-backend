package org.smarteye.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity @Table(name = "measurements")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Measurement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "tech_pallet_id", nullable = false)
    private TechPallet techPallet;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "pointcloud_file_id")
    private PointcloudFile pointcloudFile;

    @Column(name = "profiles_count")
    private Integer profilesCount;

    @Column(name = "status")
    private String status; // CREATED / UPLOADED / QA_IN_PROGRESS / QA_DONE / ERROR

    @Column(name = "meta", columnDefinition = "jsonb")
    private String meta; // пока строкой, без доп. зависимостей

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
