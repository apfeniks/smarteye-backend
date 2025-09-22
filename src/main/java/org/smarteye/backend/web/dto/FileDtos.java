package org.smarteye.backend.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.smarteye.backend.domain.enums.StorageType;

import java.time.OffsetDateTime;

public final class FileDtos {

    // ===== CREATE =====
    public record FileCreateRequest(
            @NotBlank @Size(max = 512) String objectKey,
            @Size(max = 32) String format,          // parquet|ply|csv|pdf...
            StorageType storage,                    // если null -> MINIO в сервисе
            @Size(max = 256) String filename,
            @Size(max = 128) String contentType,
            Long sizeBytes,
            @Size(max = 128) String checksum
    ) {}

    // ===== UPDATE (PATCH) =====
    public record FileUpdateRequest(
            @Size(max = 32) String format,
            @Size(max = 256) String filename,
            @Size(max = 128) String contentType,
            Long sizeBytes,
            @Size(max = 128) String checksum
    ) {}

    // ===== RESPONSE =====
    public record FileResponse(
            Long id,
            String objectKey,
            String format,
            StorageType storage,
            String filename,
            String contentType,
            Long sizeBytes,
            String checksum,
            OffsetDateTime createdAt
    ) {}
}
