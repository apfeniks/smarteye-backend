package org.smarteye.backend.repository;

import org.smarteye.backend.domain.Weight;
import org.smarteye.backend.domain.enums.WeightPhase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WeightRepository extends JpaRepository<Weight, Long> {

    List<Weight> findAllByMeasurementId(Long measurementId);

    Optional<Weight> findFirstByMeasurementIdAndPhase(Long measurementId, WeightPhase phase);

    List<Weight> findTop50ByPhaseOrderByCreatedAtDesc(WeightPhase phase);
}
