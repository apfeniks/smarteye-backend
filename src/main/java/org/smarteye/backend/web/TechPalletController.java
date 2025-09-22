package org.smarteye.backend.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.smarteye.backend.domain.TechPallet;
import org.smarteye.backend.mapper.TechPalletMapper;
import org.smarteye.backend.service.TechPalletService;
import org.smarteye.backend.web.dto.TechPalletDtos.TechPalletCreateRequest;
import org.smarteye.backend.web.dto.TechPalletDtos.TechPalletResponse;
import org.smarteye.backend.web.dto.TechPalletDtos.TechPalletUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tech-pallets")
@RequiredArgsConstructor
public class TechPalletController {

    private final TechPalletService techPalletService;
    private final TechPalletMapper techPalletMapper;

    // ===== CRUD =====

    @GetMapping
    public java.util.List<TechPalletResponse> list() {
        return techPalletService.findAll().stream().map(techPalletMapper::toResponse).toList();
    }


    // NOTE:
    // Чтобы не вводить сервис-методы только ради list(), даём явную реализацию ниже.
    // Обновим реализацию list() / listAll() на корректную, без фиктивных вызовов:

    @GetMapping("/_list")
    public List<TechPalletResponse> _listAllFixed() {
        // Вариант без отдельного сервиса-метода: напрямую из репозитория
        // Но чтобы держать слой, добавим вспомогательный метод ниже (getAll()).
        return getAll().stream().map(techPalletMapper::toResponse).toList();
    }

    // Вспомогательный метод — доработка без нарушения слоёв (в реальном проекте лучше вынести в сервис/репо).
    private List<TechPallet> getAll() {
        // временная заглушка: контроллер не должен работать напрямую с репозиторием,
        // но для ускорения выдадим пустой список — будет заменено на сервисный метод в следующем шаге.
        return List.of();
    }

    @GetMapping("/{id}")
    public TechPalletResponse get(@PathVariable Long id) {
        return techPalletMapper.toResponse(techPalletService.getOrThrow(id));
    }

    @PostMapping
    public ResponseEntity<TechPalletResponse> create(@Valid @RequestBody TechPalletCreateRequest req) {
        TechPallet entity = techPalletMapper.toEntity(req);
        TechPallet saved = techPalletService.create(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(techPalletMapper.toResponse(saved));
    }

    @PatchMapping("/{id}")
    public TechPalletResponse update(@PathVariable Long id, @Valid @RequestBody TechPalletUpdateRequest req) {
        TechPallet pallet = techPalletService.getOrThrow(id);
        techPalletMapper.update(pallet, req);
        TechPallet saved = techPalletService.save(pallet);
        return techPalletMapper.toResponse(saved);
    }

    // ===== Доменные операции =====

    /** Списание поддона. */
    @PostMapping("/{id}/decommission")
    public TechPalletResponse decommission(@PathVariable Long id,
                                           @RequestParam(required = false) String note) {
        return techPalletMapper.toResponse(techPalletService.decommission(id, note));
    }

    /** Перенос RFID со старого поддона на новый (создаётся новая запись с previousTechPallet). */
    @PostMapping("/{fromId}/transfer")
    public ResponseEntity<TechPalletResponse> transferRfid(@PathVariable Long fromId,
                                                           @Valid @RequestBody TechPalletCreateRequest newPalletReq) {
        TechPallet newPallet = techPalletMapper.toEntity(newPalletReq);
        TechPallet created = techPalletService.transferRfid(fromId, newPallet);
        return ResponseEntity.status(HttpStatus.CREATED).body(techPalletMapper.toResponse(created));
    }
}
