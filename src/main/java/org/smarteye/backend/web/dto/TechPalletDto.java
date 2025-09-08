package org.smarteye.backend.web.dto;

public record TechPalletDto(
        Long id, String rfidTag, String status,
        Integer lengthMm, Integer widthMm, Integer heightMm,
        Double tareWeightKg
) {}