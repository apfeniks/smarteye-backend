package org.smarteye.backend.service;

import lombok.RequiredArgsConstructor;
import org.smarteye.backend.common.exception.NotFoundException;
import org.smarteye.backend.domain.Device;
import org.smarteye.backend.repository.DeviceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DeviceService {

    private final DeviceRepository deviceRepository;

    @Transactional(readOnly = true)
    public Device getOrThrow(Long id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Device not found: id=" + id));
    }

    @Transactional(readOnly = true)
    public Device getByCodeOrThrow(String code) {
        return deviceRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Device not found: code=" + code));
    }

    public Device create(Device device) {
        return deviceRepository.save(device);
    }

    public Device update(Long id, Device patch) {
        Device d = getOrThrow(id);
        if (patch.getName() != null) d.setName(patch.getName());
        if (patch.getDeviceType() != null) d.setDeviceType(patch.getDeviceType());
        if (patch.getDescription() != null) d.setDescription(patch.getDescription());
        if (patch.getIpAddress() != null) d.setIpAddress(patch.getIpAddress());
        d.setActive(patch.isActive());
        return d;
    }

    public void delete(Long id) {
        deviceRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Device> list() {
        return deviceRepository.findAll();
    }
}
