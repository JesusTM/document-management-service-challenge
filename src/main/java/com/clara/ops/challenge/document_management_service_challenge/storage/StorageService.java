package com.clara.ops.challenge.document_management_service_challenge.storage;

import java.io.InputStream;

public interface StorageService {

    String upload(String path, InputStream stream, long size, String contentType);

    String generateDownloadUrl(String path);
}