package org.smarteye.backend.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public final class RecipeDtos {

    // ===== CREATE =====
    public record RecipeCreateRequest(
            @NotBlank @Size(max = 64) String code,
            @NotBlank @Size(max = 128) String name,
            @Size(max = 64) String productCode,
            @Size(max = 1024) String description
    ) {}

    // ===== UPDATE (PATCH) =====
    public record RecipeUpdateRequest(
            @Size(max = 64) String code,
            @Size(max = 128) String name,
            @Size(max = 64) String productCode,
            @Size(max = 1024) String description
    ) {}

    // ===== RESPONSE =====
    public record RecipeResponse(
            Long id,
            String code,
            String name,
            String productCode,
            String description,
            OffsetDateTime createdAt
    ) {}
}
