package org.smarteye.backend.repository;

import org.smarteye.backend.domain.OperatorAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OperatorActionRepository extends JpaRepository<OperatorAction, Long> {

    List<OperatorAction> findAllByMeasurementId(Long measurementId);

    List<OperatorAction> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}
