package com.clara.ops.challenge.document_management_service_challenge.storage;

import java.io.InputStream;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

  String upload(String path, InputStream stream, MultipartFile file);

  String generateDownloadUrl(String path);
}
