package com.clara.ops.challenge.document_management_service_challenge.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MinioInitializer {

  private final MinioClient minioClient;

  @Value("${minio.bucket}")
  private String bucket;

  @PostConstruct
  public void initBucket() {
    try {

      boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());

      log.info("Bucket {} exists: {}", bucket, exists);

      if (!exists) {
        minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
      }

    } catch (Exception e) {
      throw new RuntimeException("Error initializing MinIO bucket", e);
    }
  }
}
