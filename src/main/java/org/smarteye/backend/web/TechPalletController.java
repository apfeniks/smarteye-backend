package org.smarteye.backend.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.smarteye.backend.domain.TechPallet;
import org.smarteye.backend.service.TechPalletService;
import org.smarteye.backend.web.dto.*;
import org.smarteye.backend.web.mapper.TechPalletMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tech-pallets")
public class TechPalletController {
    private final TechPalletService service;
    private final TechPalletMapper mapper;

    @GetMapping
    public List<TechPalletDto> list() {
        return service.list().stream().map(mapper::toDto).toList();
    }

    @PostMapping
    public ResponseEntity<TechPalletDto> create(@RequestBody @Valid TechPalletCreateRequest req) {
        TechPallet p = TechPallet.builder()
                .rfidTag(req.rfidTag())
                .status(req.status())
                .lengthMm(req.lengthMm())
                .widthMm(req.widthMm())
                .heightMm(req.heightMm())
                .tareWeightKg(req.tareWeightKg())
                .build();
        var saved = service.create(p);
        return ResponseEntity.created(URI.create("/api/v1/tech-pallets/" + saved.getId()))
                .body(mapper.toDto(saved));
    }

    @PatchMapping("/{id}/status")
    public TechPalletDto setStatus(@PathVariable Long id, @RequestParam String status) {
        return mapper.toDto(service.updateStatus(id, status));
    }
}
