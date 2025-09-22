package org.smarteye.backend.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public final class OperatorActionDtos {

    // ===== CREATE =====
    public record OperatorActionCreateRequest(
            @NotNull Long measurementId,
            Long userId,                               // может быть null (сервисное действие)
            @NotBlank @Size(max = 32) String actionType, // ACCEPT | REJECT | REWORK | COMMENT | ...
            @Size(max = 1024) String comment
    ) {}

    // ===== RESPONSE =====
    public record OperatorActionResponse(
            Long id,
            Long measurementId,
            Long userId,
            String actionType,
            String comment,
            OffsetDateTime createdAt
    ) {}
}
