package com.clara.ops.challenge.document_management_service_challenge.controller;

import com.clara.ops.challenge.document_management_service_challenge.dto.DocumentDownloadUrl;
import com.clara.ops.challenge.document_management_service_challenge.dto.DocumentSearchFilters;
import com.clara.ops.challenge.document_management_service_challenge.dto.PaginatedDocumentSearch;
import com.clara.ops.challenge.document_management_service_challenge.dto.UploadDocument;
import com.clara.ops.challenge.document_management_service_challenge.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/document-management")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> upload(
            @Valid @RequestPart("metadata") UploadDocument request,
            @RequestPart("file") MultipartFile file
    ) {

        documentService.upload(request, file);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/search")
    public PaginatedDocumentSearch search(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @Valid @RequestBody DocumentSearchFilters filters
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return documentService.search(filters, pageable);
    }

    @GetMapping("/download/{documentId}")
    public DocumentDownloadUrl download(@PathVariable UUID documentId) {
        return documentService.download(documentId);
    }
}