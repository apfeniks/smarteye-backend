package org.smarteye.backend.web.dto;

import jakarta.validation.constraints.NotBlank;

public record TechPalletCreateRequest(
        String rfidTag,
        @NotBlank String status,
        Integer lengthMm, Integer widthMm, Integer heightMm,
        Double tareWeightKg
) {}