package org.smarteye.backend.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

import org.smarteye.backend.common.audit.AuditLogger;
import org.smarteye.backend.common.exception.NotFoundException;
import org.smarteye.backend.domain.FileRef;
import org.smarteye.backend.domain.Measurement;
import org.smarteye.backend.domain.enums.MeasurementStatus;
import org.smarteye.backend.mapper.MeasurementMapper;
import org.smarteye.backend.repository.DeviceRepository;
import org.smarteye.backend.repository.RecipeRepository;
import org.smarteye.backend.service.MeasurementService;
import org.smarteye.backend.web.dto.MeasurementDtos.MeasurementCreateRequest;
import org.smarteye.backend.web.dto.MeasurementDtos.MeasurementResponse;
import org.smarteye.backend.service.TechPalletService;




import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

/**
 * Управление жизненным циклом измерений.
 * NOTE: завершение измерения с сохранением файла делается через /{id}/finish (см. FinishRequest).
 */
@RestController
@RequestMapping("/api/v1/measurements")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MeasurementController {

    private final MeasurementService measurementService;
    private final MeasurementMapper measurementMapper;
    private final ObjectMapper objectMapper;
    private final TechPalletService techPalletService;
    private final RecipeRepository recipeRepository;
    private final DeviceRepository deviceRepository;
    private final AuditLogger audit;

    // ===== READ =====

    @GetMapping("/{id}")
    public MeasurementResponse get(@PathVariable Long id) {
        return measurementMapper.toResponse(measurementService.getOrThrow(id));
    }

    @GetMapping
    public List<MeasurementResponse> listByStatus(@RequestParam(required = false) MeasurementStatus status) {
        List<Measurement> list = (status == null)
                ? measurementService.listByStatus(MeasurementStatus.IN_PROGRESS) // по умолчанию активные
                : measurementService.listByStatus(status);
        return list.stream().map(measurementMapper::toResponse).toList();
    }

    // ===== CREATE / START =====

    @PostMapping("/start")
    public ResponseEntity<MeasurementResponse> start(
            @Valid @RequestBody MeasurementCreateRequest req,
            @RequestHeader(value = "Idempotency-Key", required = false) String idemKey) {

        // 1) Разрешаем pallet_rfid → tech_pallet и recipe → recipe_id
        var techPallet = (req.palletRfid() != null && !req.palletRfid().isBlank())
                ? techPalletService.findByRfid(req.palletRfid()).orElse(null)
                : (req.techPalletId() != null ? techPalletService.tryGet(req.techPalletId()).orElse(null) : null);

        var recipe = (req.recipeId() != null) ? recipeRepository.findById(req.recipeId()).orElse(null)
                : (req.recipeCode() != null ? recipeRepository.findByCode(req.recipeCode()).orElse(null) : null);

        var device = deviceRepository.findById(req.deviceId())
                .orElseThrow(() -> new NotFoundException("Device not found: " + req.deviceId()));

        var m = new Measurement();
        m.setTechPallet(techPallet);
        m.setRecipe(recipe);
        m.setDevice(device);
        m.setMode(req.mode() != null ? req.mode() : org.smarteye.backend.domain.enums.MeasurementMode.SILENT);
        m.setStatus(org.smarteye.backend.domain.enums.MeasurementStatus.CREATED);
        m.setStartedAt(req.ts() != null ? req.ts() : org.smarteye.backend.common.util.TimeUtil.nowUtc());

        var saved = measurementService.start(m, m.getMode());

        // 2) Аудит/журнал: signals/fw_version/idemKey
        audit.info("MEASUREMENT_START", "edge start",
                java.util.Map.of(
                        "pallet_rfid", req.palletRfid(),
                        "signals", req.signals(),
                        "fw_version", req.fwVersion(),
                        "idempotency_key", idemKey
                ), saved.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(measurementMapper.toResponse(saved));
    }

    // ===== FINISH (attach file + metrics, set status=PENDING_REVIEW) =====

    public record FinishRequest(
            @com.fasterxml.jackson.annotation.JsonAlias("file_key") String fileKey,
            @com.fasterxml.jackson.annotation.JsonAlias("file_format") String fileFormat,
            org.smarteye.backend.domain.enums.StorageType storage,
            @com.fasterxml.jackson.annotation.JsonAlias("summary_metrics") java.util.Map<String, Object> summaryMetrics
    ) {}

    @PostMapping("/{id}/finish")
    public MeasurementResponse finish(@PathVariable Long id, @Valid @RequestBody FinishRequest body) {
        org.smarteye.backend.domain.FileRef file = new org.smarteye.backend.domain.FileRef();
        file.setObjectKey(body.fileKey());
        file.setFormat(body.fileFormat());
        file.setStorage(body.storage() != null ? body.storage() : org.smarteye.backend.domain.enums.StorageType.MINIO);

        String summaryJson = null;
        try {
            if (body.summaryMetrics() != null) {
                summaryJson = objectMapper.writeValueAsString(body.summaryMetrics());
            }
        } catch (Exception e) { /* проглатывать не надо, но укорочено */ }

        var m = measurementService.finish(id, file, summaryJson);
        return measurementMapper.toResponse(m);
    }


    // ===== STATUS OPS =====

    @PostMapping("/{id}/status/finished")
    public MeasurementResponse markFinished(@PathVariable Long id) {
        return measurementMapper.toResponse(measurementService.markFinished(id));
    }

    @PostMapping("/{id}/status/rejected")
    public MeasurementResponse markRejected(@PathVariable Long id,
                                            @RequestParam(required = false, defaultValue = "REJECTED_BY_OPERATOR")
                                            String reason) {
        return measurementMapper.toResponse(measurementService.markRejected(id, reason));
    }

    @PostMapping("/{id}/status/error")
    public MeasurementResponse markError(@PathVariable Long id,
                                         @RequestParam @NotNull String issueCode) {
        return measurementMapper.toResponse(measurementService.markError(id, issueCode));
    }

    // ===== PARTIAL UPDATES =====

    @PatchMapping("/{id}/summary")
    public MeasurementResponse setSummary(@PathVariable Long id, @RequestBody String summaryJson) {
        return measurementMapper.toResponse(measurementService.setSummaryMetrics(id, summaryJson));
    }

    @PatchMapping("/{id}/attach-file")
    public MeasurementResponse attachFile(@PathVariable Long id, @RequestBody FileRef file) {
        return measurementMapper.toResponse(measurementService.attachFile(id, file));
    }
}
