package org.smarteye.backend.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.smarteye.backend.domain.Device;
import org.smarteye.backend.mapper.DeviceMapper;
import org.smarteye.backend.service.DeviceService;
import org.smarteye.backend.web.dto.DeviceDtos.DeviceCreateRequest;
import org.smarteye.backend.web.dto.DeviceDtos.DeviceResponse;
import org.smarteye.backend.web.dto.DeviceDtos.DeviceUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;
    private final DeviceMapper deviceMapper;

    @GetMapping
    public List<DeviceResponse> list() {
        return deviceService.list().stream().map(deviceMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public DeviceResponse get(@PathVariable Long id) {
        return deviceMapper.toResponse(deviceService.getOrThrow(id));
    }

    @PostMapping
    public ResponseEntity<DeviceResponse> create(@Valid @RequestBody DeviceCreateRequest req) {
        Device entity = deviceMapper.toEntity(req);
        Device saved = deviceService.create(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(deviceMapper.toResponse(saved));
    }

    @PatchMapping("/{id}")
    public DeviceResponse update(@PathVariable Long id, @Valid @RequestBody DeviceUpdateRequest req) {
        Device patch = deviceMapper.toEntity(new DeviceCreateRequest(
                // поля code/name из create нам не нужны при patch; заранее null
                null, null, req.deviceType(), req.description(), req.ipAddress(),
                req.active() != null && req.active()
        ));
        Device updated = deviceService.update(id, patch);
        // вручную проставим имя (Mapper generate не подходит для частичного обновления)
        if (req.name() != null) updated.setName(req.name());
        return deviceMapper.toResponse(updated);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        deviceService.delete(id);
    }
}
