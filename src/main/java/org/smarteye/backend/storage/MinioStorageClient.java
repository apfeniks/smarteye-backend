package org.smarteye.backend.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Утилита для формирования публичных URL к объектам MinIO/S3.
 * В нашем docker-compose бакет делает anonymous download, поэтому достаточно path-style URL.
 */
@Component
@RequiredArgsConstructor
public class MinioStorageClient {

    private final S3ClientFactory s3;

    @Value("${S3_ENDPOINT:http://localhost:9000}")
    private String endpoint;

    /**
     * Публичный URL вида: {endpoint}/{bucket}/{objectKey}
     * Пример: http://localhost:9000/smarteye-clouds/clouds/2025-09-11/m123.parquet
     */
    public String publicUrl(String objectKey) {
        String base = endpoint.endsWith("/") ? endpoint.substring(0, endpoint.length() - 1) : endpoint;
        String bucket = s3.bucket();
        String key = objectKey.startsWith("/") ? objectKey.substring(1) : objectKey;
        return base + "/" + bucket + "/" + key;
    }
}
