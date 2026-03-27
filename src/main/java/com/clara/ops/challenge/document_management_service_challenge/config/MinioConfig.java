package com.clara.ops.challenge.document_management_service_challenge.config;

import io.minio.MinioClient;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MinioConfig {
  private final MinioProperties properties;

  @Bean
  public MinioClient minioClient() {
    OkHttpClient httpClient =
        new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .connectionPool(new ConnectionPool(5, 5, TimeUnit.MINUTES))
            .build();

    return MinioClient.builder()
        .endpoint(properties.getUrl())
        .credentials(properties.getAccessKey(), properties.getSecretKey())
        .httpClient(httpClient)
        .build();
  }
}
