package org.smarteye.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.utils.StringUtils;

import java.net.URI;

@Configuration
public class MinioConfig {

    @Value("${S3_ENDPOINT:http://localhost:9000}")
    private String endpoint;

    @Value("${S3_REGION:us-east-1}")
    private String region;

    @Value("${S3_ACCESS_KEY_ID:smarteye}")
    private String accessKey;

    @Value("${S3_SECRET_ACCESS_KEY:smarteye_secret}")
    private String secretKey;

    @Value("${S3_FORCE_PATH_STYLE:true}")
    private boolean forcePathStyle;

    @Value("${S3_BUCKET:smarteye-clouds}")
    private String bucket;

    @Bean
    public S3Client s3Client(
            @Value("${s3.region:us-east-1}") String region,
            @Value("${s3.endpoint:}") String endpoint,
            @Value("${s3.accessKey}") String accessKey,
            @Value("${s3.secretKey}") String secretKey) {

        var creds = AwsBasicCredentials.create(accessKey, secretKey);
        var s3cfg = S3Configuration.builder().pathStyleAccessEnabled(true).build();

        var builder = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(creds))
                .serviceConfiguration(s3cfg)
                .region(Region.of(region));

        if (software.amazon.awssdk.utils.StringUtils.isNotBlank(endpoint)) {
            builder = builder.endpointOverride(java.net.URI.create(endpoint));
        }
        return builder.build();
    }


    /** Имя бакета как бин, чтобы инжектить в сервисы. */
    @Bean(name = "s3BucketName")
    public String s3BucketName() {
        return bucket;
    }
}
