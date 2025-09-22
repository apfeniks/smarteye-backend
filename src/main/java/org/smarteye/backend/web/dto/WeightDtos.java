package org.smarteye.backend.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.smarteye.backend.domain.enums.WeightPhase;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public final class WeightDtos {

    // ===== CREATE =====
    public record WeightCreateRequest(
            @com.fasterxml.jackson.annotation.JsonAlias("measurement_id") Long measurementId,
            @jakarta.validation.constraints.NotNull org.smarteye.backend.domain.enums.WeightPhase phase,
            @jakarta.validation.constraints.NotNull
            @jakarta.validation.constraints.DecimalMin("0.000")
            @com.fasterxml.jackson.annotation.JsonAlias("value_kg") java.math.BigDecimal valueKg,
            @com.fasterxml.jackson.annotation.JsonAlias("sensor_ok") Boolean sensorOk, // в БД не пишем, только лог
            @com.fasterxml.jackson.annotation.JsonAlias({"ts","taken_at"}) java.time.OffsetDateTime ts,
            Long deviceId
    ) {}


    // ===== RESPONSE =====
    public record WeightResponse(
            Long id,
            Long measurementId,
            WeightPhase phase,
            BigDecimal valueKg,
            Long deviceId,
            OffsetDateTime takenAt,
            OffsetDateTime createdAt
    ) {}
}
