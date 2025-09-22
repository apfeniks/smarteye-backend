package org.smarteye.backend.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public final class DefectDtos {

    // ===== CREATE =====
    public record DefectCreateRequest(
            @NotNull Long measurementId,
            @NotBlank @Size(max = 64) String code,
            @Size(max = 512) String description,
            String data,                     // JSON строка (опц.)
            @Size(max = 16) String source    // AUTO | OPERATOR | ...
    ) {}

    // ===== UPDATE (PATCH) =====
    public record DefectUpdateRequest(
            @Size(max = 64) String code,
            @Size(max = 512) String description,
            String data,
            @Size(max = 16) String source
    ) {}

    // ===== RESPONSE =====
    public record DefectResponse(
            Long id,
            Long measurementId,
            String code,
            String description,
            String data,
            String source,
            OffsetDateTime createdAt
    ) {}
}
