package org.smarteye.backend.repository;

import org.smarteye.backend.domain.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeasurementRepository extends JpaRepository<Measurement, Long> {}

