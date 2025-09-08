package org.smarteye.backend.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.smarteye.backend.service.MeasurementService;
import org.smarteye.backend.web.dto.MeasurementCreateRequest;
import org.smarteye.backend.web.dto.MeasurementDto;
import org.smarteye.backend.web.mapper.MeasurementMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/measurements")
public class MeasurementController {

    private final MeasurementService service;
    private final MeasurementMapper mapper;

    @GetMapping
    public List<MeasurementDto> list() {
        return service.list().stream().map(mapper::toDto).toList();
    }

    @PostMapping
    public ResponseEntity<MeasurementDto> create(@RequestBody @Valid MeasurementCreateRequest req) {
        var m = service.create(req.techPalletId(), req.profilesCount(), req.meta());
        return ResponseEntity.created(URI.create("/api/v1/measurements/" + m.getId()))
                .body(mapper.toDto(m));
    }
}
