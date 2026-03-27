package com.clara.ops.challenge.document_management_service_challenge.service;

import com.clara.ops.challenge.document_management_service_challenge.dto.*;
import com.clara.ops.challenge.document_management_service_challenge.model.Document;
import com.clara.ops.challenge.document_management_service_challenge.exception.DocumentNotFoundException;
import com.clara.ops.challenge.document_management_service_challenge.exception.InvalidDocumentException;
import com.clara.ops.challenge.document_management_service_challenge.repository.DocumentRepository;
import com.clara.ops.challenge.document_management_service_challenge.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final StorageService storageService;
    private final UploadLimiter limiter;

    @Override
    public UUID upload(UploadDocument request, MultipartFile file) {

        limiter.acquire();

        try (InputStream stream = file.getInputStream()) {
            validateFile(file);

            UUID documentId = UUID.randomUUID();
            String path = request.user() + "/" + request.name();

            storageService.upload(
                    path,
                    stream,
                    file.getSize(),
                    file.getContentType()
            );


            Document document =
                    Document.builder()
                            .id(documentId)
                            .userName(request.user())
                            .documentName(request.name())
                            .minioPath(path)
                            .fileSize(file.getSize())
                            .fileType(file.getContentType())
                            .createdAt(Instant.now())
                            .build();



            // Save metadata
            documentRepository.saveDocument(document);
            documentRepository.saveTags(request.tags());
            documentRepository.saveDocumentTags(documentId, request.tags());

            return documentId;

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            limiter.release();
        }
    }

    @Override
    public PaginatedDocumentSearch search(DocumentSearchFilters filters, int page, int size) {
        int offset = page * size;
        List<DocumentDTO> documents = documentRepository.searchDocuments(
                filters.user(), filters.name(), filters.tags(), offset, size
        );
        long totalItems = documentRepository.countDocuments(filters.user(), filters.name(), filters.tags());
        int totalPages = (int) Math.ceil((double) totalItems / size);

        Metadata metadata = new Metadata(page, size, documents.size(), totalPages, totalItems);

        return new PaginatedDocumentSearch(metadata, documents);
    }

    @Override
    public DocumentDownloadUrl download(UUID id) {

        String minioPath = documentRepository.findMinioPathById(id)
                .orElseThrow(() -> new DocumentNotFoundException(id.toString()));

        String url = storageService.generateDownloadUrl(minioPath);

        return new DocumentDownloadUrl(url);
    }

    private void validateFile(MultipartFile file) {

        if (file.isEmpty()) {
            throw new InvalidDocumentException("File is empty");
        }

        if (!"application/pdf".equals(file.getContentType())) {
            throw new InvalidDocumentException("Only PDF files are allowed");
        }

        long maxSize = 500 * 1024 * 1024;

        if (file.getSize() > maxSize) {
            throw new InvalidDocumentException("File exceeds 500MB limit");
        }

        validatePdfSignature(file);
    }

    private void validatePdfSignature(MultipartFile file) {

        try (InputStream is = file.getInputStream()) {

            byte[] header = new byte[4];
            is.read(header);

            String signature = new String(header);

            if (!signature.startsWith("%PDF")) {
                throw new InvalidDocumentException("Invalid PDF file");
            }

        } catch (IOException e) {
            throw new InvalidDocumentException("Unable to validate file");
        }
    }
}