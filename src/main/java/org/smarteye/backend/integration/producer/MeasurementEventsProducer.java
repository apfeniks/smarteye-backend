package org.smarteye.backend.integration.producer;

import lombok.RequiredArgsConstructor;
import org.smarteye.backend.config.KafkaTopicsConfig;
import org.smarteye.backend.integration.events.MeasurementCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MeasurementEventsProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendCreated(MeasurementCreatedEvent event) {
        // key = measurementId (строкой), для упорядочивания по ключу
        kafkaTemplate.send(KafkaTopicsConfig.TOPIC_MEASUREMENT_CREATED,
                String.valueOf(event.id()), event);
    }
}
