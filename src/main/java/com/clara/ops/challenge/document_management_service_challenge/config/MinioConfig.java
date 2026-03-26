package com.clara.ops.challenge.document_management_service_challenge.config;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MinioConfig {
    private final MinioProperties properties;

    @Bean
    public MinioClient minioClient() {

        return MinioClient.builder()
                .endpoint(properties.getUrl())
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .build();
    }
}