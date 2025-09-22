package org.smarteye.backend.ws;

import lombok.RequiredArgsConstructor;
import org.smarteye.backend.domain.Measurement;
import org.smarteye.backend.domain.Weight;
import org.smarteye.backend.mapper.MeasurementMapper;
import org.smarteye.backend.mapper.WeightMapper;
import org.springframework.stereotype.Component;

/**
 * Шлюз для публикации «живых» событий в WebSocket/STOMP.
 * Сервисы могут дергать методы emit* с доменными сущностями —
 * здесь они маппятся в DTO и отправляются в соответствующие топики.
 */
@Component
@RequiredArgsConstructor
public class LiveEventsGateway {

    private final WsEventsPublisher publisher;
    private final MeasurementMapper measurementMapper;
    private final WeightMapper weightMapper;

    /** Отправить обновление по Measurement в /topic/measurements. */
    public void emitMeasurement(Measurement measurement) {
        if (measurement == null) return;
        publisher.measurement(measurementMapper.toResponse(measurement));
    }

    /** Отправить новое показание весов в /topic/weights. */
    public void emitWeight(Weight weight) {
        if (weight == null) return;
        publisher.weight(weightMapper.toResponse(weight));
    }

    /** Системное сообщение в /topic/system. */
    public void system(String message) {
        publisher.system(message);
    }
}
