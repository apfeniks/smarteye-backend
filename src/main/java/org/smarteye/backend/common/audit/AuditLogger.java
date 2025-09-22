package org.smarteye.backend.common.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.smarteye.backend.domain.Event;
import org.smarteye.backend.domain.Measurement;
import org.smarteye.backend.repository.EventRepository;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Аудит доменных событий в таблицу events + в лог.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogger {

    private final EventRepository eventRepository;


    private static Measurement ref(Long id) {
        if (id == null) return null;
        var m = new Measurement();
        m.setId(id);
        return m;
    }


    public void info(String type, String message, Map<String, Object> data, Long measurementId) {
        String requestId = MDC.get("requestId");
        // Пишем в БД
        Event e = Event.builder()
                .type(type)
                .level("INFO")
                .message(message)
                .data(data != null ? data.toString() : null)
                .measurement(ref(measurementId))
                .requestId(requestId)
                .createdAt(OffsetDateTime.now())
                .build();
        eventRepository.save(e);

        // Дублируем в лог
        log.info("[audit][{}] type={} mid={} msg={} data={}",
                requestId, type, measurementId, message, data);
    }

    public void warn(String type, String message, Map<String, Object> data, Long measurementId) {
        String requestId = MDC.get("requestId");
        Event e = Event.builder()
                .type(type)
                .level("WARN")
                .message(message)
                .data(data != null ? data.toString() : null)
                .measurement(ref(measurementId))
                .requestId(requestId)
                .createdAt(OffsetDateTime.now())
                .build();
        eventRepository.save(e);
        log.warn("[audit][{}] type={} mid={} msg={} data={}",
                requestId, type, measurementId, message, data);
    }

    public void error(String type, String message, Map<String, Object> data, Long measurementId) {
        String requestId = MDC.get("requestId");
        Event e = Event.builder()
                .type(type)
                .level("ERROR")
                .message(message)
                .data(data != null ? data.toString() : null)
                .measurement(ref(measurementId))
                .requestId(requestId)
                .createdAt(OffsetDateTime.now())
                .build();
        eventRepository.save(e);
        log.error("[audit][{}] type={} mid={} msg={} data={}",
                requestId, type, measurementId, message, data);
    }
}
