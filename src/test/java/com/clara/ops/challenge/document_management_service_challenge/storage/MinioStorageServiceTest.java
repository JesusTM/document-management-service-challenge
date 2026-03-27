package com.clara.ops.challenge.document_management_service_challenge.storage;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.clara.ops.challenge.document_management_service_challenge.exception.StorageException;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import java.io.InputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class MinioStorageServiceTest {

  @Mock private MinioClient minioClient;

  @Mock private MultipartFile multipartFile;

  @Mock private InputStream inputStream;

  @InjectMocks private MinioStorageService minioStorageService;

  private final String BUCKET_NAME = "test-bucket";
  private final String TEST_PATH = "documents/test.pdf";

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(minioStorageService, "bucket", BUCKET_NAME);
  }

  @Test
  void upload_ShouldReturnPath_WhenSuccessful() throws Exception {
    when(multipartFile.getSize()).thenReturn(1024L);
    when(multipartFile.getContentType()).thenReturn("application/pdf");

    String result = minioStorageService.upload(TEST_PATH, inputStream, multipartFile);

    assertEquals(TEST_PATH, result);
    verify(minioClient, times(1)).putObject(any(PutObjectArgs.class));
  }

  @Test
  void upload_ShouldThrowStorageException_WhenMinioFails() {
    when(multipartFile.getSize()).thenReturn(1024L);

    StorageException exception =
        assertThrows(
            StorageException.class,
            () -> minioStorageService.upload(TEST_PATH, inputStream, multipartFile));

    assertTrue(exception.getMessage().contains("Error uploading file"));
  }

  @Test
  void generateDownloadUrl_ShouldReturnUrl_WhenSuccessful() throws Exception {
    String expectedUrl = "https://s3.amazonaws.com/test-bucket/file?token=123";
    when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
        .thenReturn(expectedUrl);

    String result = minioStorageService.generateDownloadUrl(TEST_PATH);

    assertEquals(expectedUrl, result);
    verify(minioClient).getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class));
  }

  @Test
  void generateDownloadUrl_ShouldThrowStorageException_WhenMinioFails() throws Exception {
    when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
        .thenThrow(new RuntimeException("Connection failed"));

    assertThrows(StorageException.class, () -> minioStorageService.generateDownloadUrl(TEST_PATH));
  }
}
