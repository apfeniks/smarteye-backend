package org.smarteye.backend.web.dto;


public record MeasurementDto(
        Long id, Long techPalletId, Long pointcloudFileId,
        Integer profilesCount, String status
) {}