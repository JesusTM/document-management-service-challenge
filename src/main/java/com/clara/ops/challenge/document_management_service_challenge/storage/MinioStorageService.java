package com.clara.ops.challenge.document_management_service_challenge.storage;

import com.clara.ops.challenge.document_management_service_challenge.exception.StorageException;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MinioStorageService implements StorageService {

  private final MinioClient minioClient;

  @Value("${minio.bucket}")
  private String bucket;

  @Override
  public String upload(String path, InputStream stream, MultipartFile file) {
    try {
      long minPartSize = 5 * 1024 * 1024;
      long optimizedPartSize = (file.getSize() < minPartSize) ? -1 : minPartSize;

      minioClient.putObject(
          PutObjectArgs.builder().bucket(bucket).object(path).stream(
                  stream, file.getSize(), optimizedPartSize)
              .contentType(file.getContentType())
              .build());

      return path;

    } catch (Exception e) {
      throw new StorageException("Error uploading file", e);
    }
  }

  @Override
  public String generateDownloadUrl(String path) {
    try {

      return minioClient.getPresignedObjectUrl(
          GetPresignedObjectUrlArgs.builder()
              .bucket(bucket)
              .object(path)
              .method(Method.GET)
              .expiry(3600)
              .build());
    } catch (Exception e) {
      throw new StorageException("Error generating URL", e);
    }
  }
}
