package org.smarteye.backend.web.dto;

import jakarta.validation.constraints.NotNull;

public record MeasurementCreateRequest(
        @NotNull Long techPalletId,
        Integer profilesCount,
        String meta
) {}