package org.smarteye.backend.api;

import org.smarteye.backend.api.dto.DeviceCreateRequest;
import org.smarteye.backend.entity.Device;
import org.smarteye.backend.repository.DeviceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/devices")
public class DeviceController {
    private final DeviceRepository repo;
    public DeviceController(DeviceRepository repo){ this.repo = repo; }

    @GetMapping
    public List<Device> list(){ return repo.findAll(); }

    @PostMapping
    public ResponseEntity<Device> create(@RequestBody DeviceCreateRequest req){
        var saved = repo.save(Device.builder()
                .type(req.type())
                .serial(req.serial())
                .status(req.status())
                .build());
        return ResponseEntity.created(URI.create("/api/v1/devices/" + saved.getId())).body(saved);
    }
}
