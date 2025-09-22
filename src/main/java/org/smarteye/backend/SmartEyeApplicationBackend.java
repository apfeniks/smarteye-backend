package org.smarteye.backend;

import org.smarteye.backend.config.ConveyorProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.time.ZoneOffset;
import java.util.TimeZone;

@SpringBootApplication
@EnableConfigurationProperties(ConveyorProperties.class)
public class SmartEyeApplicationBackend {

    public static void main(String[] args) {
        // Гарантируем работу в UTC
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.UTC));
        SpringApplication.run(SmartEyeApplicationBackend.class, args);
    }
}
