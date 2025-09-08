package org.smarteye.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity @Table(name = "pointcloud_files")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PointcloudFile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "object_key", nullable = false)
    private String objectKey;

    @Column(name = "format")
    private String format;

    @Column(name = "size_bytes")
    private Long sizeBytes;

    @Column(name = "sha256")
    private String sha256;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}
