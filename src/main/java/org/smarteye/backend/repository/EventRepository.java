package org.smarteye.backend.repository;

import org.smarteye.backend.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByMeasurementIdOrderByCreatedAtDesc(Long measurementId);
}
