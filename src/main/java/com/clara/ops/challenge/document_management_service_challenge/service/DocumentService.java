package com.clara.ops.challenge.document_management_service_challenge.service;

import com.clara.ops.challenge.document_management_service_challenge.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface DocumentService {

    UUID upload(UploadDocument request, MultipartFile file);

    PaginatedDocumentSearch search(DocumentSearchFilters filters, int page, int size);

    DocumentDownloadUrl download(UUID id);
}