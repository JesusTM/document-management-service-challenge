package com.clara.ops.challenge.document_management_service_challenge.service;

import com.clara.ops.challenge.document_management_service_challenge.dto.*;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface DocumentService {

    void upload(UploadDocument request, MultipartFile file);

    PaginatedDocumentSearch search(
            DocumentSearchFilters filters,
            Pageable pageable);

    DocumentDownloadUrl download(UUID id);
}