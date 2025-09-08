package org.smarteye.backend.integration.consumer;

import lombok.extern.slf4j.Slf4j;
import org.smarteye.backend.config.KafkaTopicsConfig;
import org.smarteye.backend.integration.events.MeasurementCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MeasurementEventsListener {

    @KafkaListener(
            topics = KafkaTopicsConfig.TOPIC_MEASUREMENT_CREATED,
            groupId = "smarteye-consumers"
    )
    public void onCreated(@Payload MeasurementCreatedEvent event) {
        log.info("Kafka: measurement created -> id={}, pallet={}, profiles={}",
                event.id(), event.techPalletId(), event.profilesCount());
    }
}
