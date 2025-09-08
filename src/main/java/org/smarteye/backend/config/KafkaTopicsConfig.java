package org.smarteye.backend.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {

    public static final String TOPIC_MEASUREMENT_CREATED = "scanner.measurements.created";
    public static final String TOPIC_QA_REQUESTED        = "qa.analysis.requested";
    public static final String TOPIC_QA_FINISHED         = "qa.analysis.finished";
    public static final String TOPIC_RFID_READ           = "rfid.read";
    public static final String TOPIC_WEIGHT_CAPTURED     = "logistic.weight.captured";

    // создаём только то, что нужно сейчас
    @Bean NewTopic measurementCreatedTopic() {
        return TopicBuilder.name(TOPIC_MEASUREMENT_CREATED).partitions(1).replicas(1).build();
    }
}
