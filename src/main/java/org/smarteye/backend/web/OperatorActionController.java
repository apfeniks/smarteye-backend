package org.smarteye.backend.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.smarteye.backend.domain.OperatorAction;
import org.smarteye.backend.service.OperatorActionService;
import org.smarteye.backend.web.dto.OperatorActionDtos.OperatorActionCreateRequest;
import org.smarteye.backend.web.dto.OperatorActionDtos.OperatorActionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/operator-actions")
@RequiredArgsConstructor
public class OperatorActionController {

    private final OperatorActionService operatorActionService;

    @GetMapping("/by-measurement/{measurementId}")
    public List<OperatorActionResponse> listByMeasurement(@PathVariable Long measurementId) {
        return operatorActionService.listByMeasurement(measurementId).stream()
                .map(oa -> new OperatorActionResponse(
                        oa.getId(),
                        oa.getMeasurement().getId(),
                        oa.getUser() != null ? oa.getUser().getId() : null,
                        oa.getActionType(),
                        oa.getComment(),
                        oa.getCreatedAt()))
                .toList();
    }

    @GetMapping("/by-user/{userId}")
    public List<OperatorActionResponse> listByUser(@PathVariable Long userId) {
        return operatorActionService.listByUser(userId).stream()
                .map(oa -> new OperatorActionResponse(
                        oa.getId(),
                        oa.getMeasurement().getId(),
                        oa.getUser() != null ? oa.getUser().getId() : null,
                        oa.getActionType(),
                        oa.getComment(),
                        oa.getCreatedAt()))
                .toList();
    }

    @GetMapping("/{id}")
    public OperatorActionResponse get(@PathVariable Long id) {
        OperatorAction oa = operatorActionService.getOrThrow(id);
        return new OperatorActionResponse(
                oa.getId(),
                oa.getMeasurement().getId(),
                oa.getUser() != null ? oa.getUser().getId() : null,
                oa.getActionType(),
                oa.getComment(),
                oa.getCreatedAt()
        );
    }

    @PostMapping
    public ResponseEntity<OperatorActionResponse> create(@Valid @RequestBody OperatorActionCreateRequest req) {
        var saved = operatorActionService.create(req.measurementId(), req.userId(), req.actionType(), req.comment());
        return ResponseEntity.status(HttpStatus.CREATED).body(new OperatorActionResponse(
                saved.getId(),
                saved.getMeasurement().getId(),
                saved.getUser() != null ? saved.getUser().getId() : null,
                saved.getActionType(),
                saved.getComment(),
                saved.getCreatedAt()
        ));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        operatorActionService.delete(id);
    }
}
