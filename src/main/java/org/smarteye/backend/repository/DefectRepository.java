package org.smarteye.backend.repository;

import org.smarteye.backend.domain.Defect;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DefectRepository extends JpaRepository<Defect, Long> {

    List<Defect> findAllByMeasurementId(Long measurementId);
}
