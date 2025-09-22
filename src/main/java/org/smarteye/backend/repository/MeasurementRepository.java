package org.smarteye.backend.repository;

import org.smarteye.backend.domain.Measurement;
import org.smarteye.backend.domain.enums.MeasurementStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeasurementRepository extends JpaRepository<Measurement, Long> {

    List<Measurement> findAllByStatus(MeasurementStatus status);
}
