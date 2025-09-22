package org.smarteye.backend.service;

import lombok.RequiredArgsConstructor;
import org.smarteye.backend.common.exception.NotFoundException;
import org.smarteye.backend.domain.Defect;
import org.smarteye.backend.repository.DefectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DefectService {

    private final DefectRepository defectRepository;
    private final MeasurementService measurementService;

    @Transactional(readOnly = true)
    public Defect getOrThrow(Long id) {
        return defectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Defect not found: id=" + id));
    }

    @Transactional(readOnly = true)
    public List<Defect> listByMeasurement(Long measurementId) {
        return defectRepository.findAllByMeasurementId(measurementId);
    }

    public Defect create(Long measurementId, Defect d) {
        d.setId(null);
        d.setMeasurement(measurementService.getOrThrow(measurementId));
        return defectRepository.save(d);
    }

    public Defect update(Long id, Defect patch) {
        Defect d = getOrThrow(id);
        if (patch.getCode() != null) d.setCode(patch.getCode());
        if (patch.getDescription() != null) d.setDescription(patch.getDescription());
        if (patch.getData() != null) d.setData(patch.getData());
        if (patch.getSource() != null) d.setSource(patch.getSource());
        return d;
    }

    public void delete(Long id) {
        defectRepository.deleteById(id);
    }
}
