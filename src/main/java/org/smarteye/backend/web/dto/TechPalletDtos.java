package org.smarteye.backend.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.smarteye.backend.domain.enums.TechPalletStatus;

import java.time.OffsetDateTime;

public final class TechPalletDtos {

    // ====== CREATE ======
    public record TechPalletCreateRequest(
            @Size(max = 64) String rfidUid,                 // может быть null в режиме без RFID
            TechPalletStatus status,                        // если null -> ACTIVE
            OffsetDateTime commissionedAt,
            @Size(max = 1024) String note,
            @Size(max = 64) String productCode,
            @Size(max = 64) String colorCode,
            Long recipeId
    ) {}

    // ====== UPDATE (PATCH) ======
    public record TechPalletUpdateRequest(
            @Size(max = 64) String rfidUid,                 // менять обычно не нужно; для сервисных операций
            TechPalletStatus status,
            OffsetDateTime commissionedAt,
            OffsetDateTime decommissionedAt,
            @Size(max = 1024) String note,
            @Size(max = 64) String productCode,
            @Size(max = 64) String colorCode,
            Long recipeId
    ) {}

    // ====== RESPONSE ======
    public record TechPalletResponse(
            Long id,
            String rfidUid,
            TechPalletStatus status,
            OffsetDateTime commissionedAt,
            OffsetDateTime decommissionedAt,
            Long previousTechPalletId,
            String note,
            String productCode,
            String colorCode,
            Long recipeId,
            OffsetDateTime createdAt
    ) {}
}
