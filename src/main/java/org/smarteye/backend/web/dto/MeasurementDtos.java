package org.smarteye.backend.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.smarteye.backend.domain.enums.MeasurementMode;
import org.smarteye.backend.domain.enums.MeasurementStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public final class MeasurementDtos {

    // ===== CREATE (start) =====
    public record MeasurementCreateRequest(
            @com.fasterxml.jackson.annotation.JsonAlias("pallet_rfid") String palletRfid,
            Long techPalletId,                       // по-прежнему поддерживаем
            @com.fasterxml.jackson.annotation.JsonAlias("recipe") String recipeCode,
            Long recipeId,
            @jakarta.validation.constraints.NotNull
            @com.fasterxml.jackson.annotation.JsonAlias("device_id") Long deviceId,
            org.smarteye.backend.domain.enums.MeasurementMode mode, // SILENT/OPERATOR (внутри AUTO допускаем)
            @com.fasterxml.jackson.annotation.JsonAlias("signals") java.util.Map<String, Boolean> signals,
            @com.fasterxml.jackson.annotation.JsonAlias("fw_version") String fwVersion,
            @com.fasterxml.jackson.annotation.JsonAlias("ts") java.time.OffsetDateTime ts
    ) { }

    // ===== RESPONSE =====
    public record MeasurementResponse(
            Long id,
            Long techPalletId,
            Long recipeId,
            Long deviceId,
            Long fileId,
            MeasurementMode mode,
            MeasurementStatus status,
            String issueCode,
            BigDecimal massKg,
            String summaryMetrics,        // JSON
            OffsetDateTime startedAt,
            OffsetDateTime finishedAt,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) { }



}
