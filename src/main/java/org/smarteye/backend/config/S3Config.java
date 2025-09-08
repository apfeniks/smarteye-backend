package org.smarteye.backend.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    @Bean
    MinioClient minioClient(
            @Value("${smarteye.s3.endpoint}") String endpoint,
            @Value("${smarteye.s3.access-key}") String accessKey,
            @Value("${smarteye.s3.secret-key}") String secretKey
    ) {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}
