package org.smarteye.backend.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.smarteye.backend.domain.Defect;
import org.smarteye.backend.mapper.DefectMapper;
import org.smarteye.backend.service.DefectService;
import org.smarteye.backend.web.dto.DefectDtos.DefectCreateRequest;
import org.smarteye.backend.web.dto.DefectDtos.DefectResponse;
import org.smarteye.backend.web.dto.DefectDtos.DefectUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/defects")
@RequiredArgsConstructor
public class DefectController {

    private final DefectService defectService;
    private final DefectMapper defectMapper;

    @GetMapping("/by-measurement/{measurementId}")
    public List<DefectResponse> listByMeasurement(@PathVariable Long measurementId) {
        return defectService.listByMeasurement(measurementId)
                .stream().map(defectMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public DefectResponse get(@PathVariable Long id) {
        return defectMapper.toResponse(defectService.getOrThrow(id));
    }

    @PostMapping
    public ResponseEntity<DefectResponse> create(@Valid @RequestBody DefectCreateRequest req) {
        Defect entity = defectMapper.toEntity(req);
        Defect saved = defectService.create(req.measurementId(), entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(defectMapper.toResponse(saved));
    }

    @PatchMapping("/{id}")
    public DefectResponse update(@PathVariable Long id, @Valid @RequestBody DefectUpdateRequest req) {
        Defect patch = defectMapper.toEntity(new DefectCreateRequest(
                // measurementId обязателен только при create
                0L, req.code(), req.description(), req.data(), req.source()
        ));
        Defect updated = defectService.update(id, patch);
        return defectMapper.toResponse(updated);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        defectService.delete(id);
    }
}
