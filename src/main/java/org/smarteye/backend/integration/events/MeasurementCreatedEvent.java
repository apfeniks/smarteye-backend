package org.smarteye.backend.integration.events;

import java.time.OffsetDateTime;

public record MeasurementCreatedEvent(
        Long id,
        Long techPalletId,
        Integer profilesCount,
        String status,
        OffsetDateTime createdAt
) {}
