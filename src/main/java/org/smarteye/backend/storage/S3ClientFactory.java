package org.smarteye.backend.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * Простой фасад для работы с S3/MinIO клиентом.
 * Сейчас лишь отдаёт бин S3Client и имя бакета.
 * При необходимости можно расширить (пресайн-ссылки, ACL и т.п.).
 */
@Component
@RequiredArgsConstructor
public class S3ClientFactory {

    private final S3Client s3Client;
    private final String s3BucketName;

    public S3Client client() {
        return s3Client;
    }

    public String bucket() {
        return s3BucketName;
    }
}
