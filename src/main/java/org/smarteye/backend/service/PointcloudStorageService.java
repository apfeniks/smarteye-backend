package org.smarteye.backend.service;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;              // <— ВАЖНО: этот импорт
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PointcloudStorageService {

    private final MinioClient minio;

    @Value("${smarteye.s3.bucket}")
    private String bucket;

    public String presignPut(String objectKey, int minutes) throws Exception {
        return minio.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket(bucket)
                        .object(objectKey)
                        .method(Method.PUT)
                        .expiry(minutes, TimeUnit.MINUTES)
                        .build()
        );
    }

    public String presignGet(String objectKey, int minutes) throws Exception {
        return minio.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket(bucket)
                        .object(objectKey)
                        .method(Method.GET)
                        .expiry(minutes, TimeUnit.MINUTES)
                        .build()
        );
    }
}
