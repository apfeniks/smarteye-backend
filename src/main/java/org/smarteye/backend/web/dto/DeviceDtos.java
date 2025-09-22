package org.smarteye.backend.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public final class DeviceDtos {

    // ===== CREATE =====
    public record DeviceCreateRequest(
            @NotBlank @Size(max = 64) String code,
            @NotBlank @Size(max = 128) String name,
            @Size(max = 64) String deviceType,
            @Size(max = 512) String description,
            @Size(max = 64) String ipAddress,
            boolean active
    ) {}

    // ===== UPDATE (PATCH) =====
    public record DeviceUpdateRequest(
            @Size(max = 128) String name,
            @Size(max = 64) String deviceType,
            @Size(max = 512) String description,
            @Size(max = 64) String ipAddress,
            Boolean active
    ) {}

    // ===== RESPONSE =====
    public record DeviceResponse(
            Long id,
            String code,
            String name,
            String deviceType,
            String description,
            String ipAddress,
            boolean active,
            OffsetDateTime createdAt
    ) {}
}
