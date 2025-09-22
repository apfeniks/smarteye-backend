package org.smarteye.backend.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.smarteye.backend.domain.Weight;
import org.smarteye.backend.domain.Device;
import org.smarteye.backend.domain.Measurement;
import org.smarteye.backend.common.audit.AuditLogger;
import org.smarteye.backend.mapper.WeightMapper;
import org.smarteye.backend.repository.WeightRepository;
import org.smarteye.backend.service.WeightsService;
import org.smarteye.backend.web.dto.WeightDtos.WeightCreateRequest;
import org.smarteye.backend.web.dto.WeightDtos.WeightResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Приём показаний весов BEFORE/AFTER.
 * Если measurementId не передан — Measurement создаётся автоматически (см. WeightsService).
 * RFID (если есть) передаётся query-параметром rfidUid (таблица tech_pallets при этом используется).
 */
@RestController
@RequestMapping("/api/v1/weights")
@RequiredArgsConstructor
public class WeightController {

    private final WeightsService weightsService;
    private final WeightRepository weightRepository;
    private final WeightMapper weightMapper;
    private final AuditLogger audit;


    /** Получить последние N измерений по фазе (для диагностики). */
    @GetMapping("/latest")
    public List<WeightResponse> latest(@RequestParam(defaultValue = "BEFORE") String phase) {
        var phaseEnum = org.smarteye.backend.domain.enums.WeightPhase.valueOf(phase.toUpperCase());
        return weightRepository.findTop50ByPhaseOrderByCreatedAtDesc(phaseEnum)
                .stream().map(weightMapper::toResponse).toList();
    }

    @PostMapping
    public ResponseEntity<WeightResponse> record(
            @RequestParam(required = false) String rfidUid,
            @Valid @RequestBody WeightCreateRequest req) {

        Weight entity = weightMapper.toEntity(req);
        // takenAt из ts
        entity.setTakenAt(req.ts());
        // measurement из measurement_id (если пришёл)
        if (req.measurementId() != null) {
            entity.setMeasurement(new Measurement() {{ setId(req.measurementId()); }});
        }
        // device из тела (если прислали)
        if (req.deviceId() != null) {
            entity.setDevice(new Device() {{ setId(req.deviceId()); }});
        }

        var saved = weightsService.record(entity, rfidUid);

        // логируем sensor_ok в events
        audit.info("WEIGHT_RECORDED", "weight saved",
                java.util.Map.of("sensor_ok", req.sensorOk()),
                saved.getMeasurement() != null ? saved.getMeasurement().getId() : null);

        return ResponseEntity.status(HttpStatus.CREATED).body(weightMapper.toResponse(saved));
    }

}
