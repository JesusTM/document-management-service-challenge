package com.clara.ops.challenge.document_management_service_challenge.service;

import com.clara.ops.challenge.document_management_service_challenge.dto.*;
import com.clara.ops.challenge.document_management_service_challenge.entity.Document;
import com.clara.ops.challenge.document_management_service_challenge.entity.DocumentTag;
import com.clara.ops.challenge.document_management_service_challenge.entity.DocumentTagId;
import com.clara.ops.challenge.document_management_service_challenge.entity.Tag;
import com.clara.ops.challenge.document_management_service_challenge.exception.DocumentNotFoundException;
import com.clara.ops.challenge.document_management_service_challenge.exception.InvalidDocumentException;
import com.clara.ops.challenge.document_management_service_challenge.exception.StorageException;
import com.clara.ops.challenge.document_management_service_challenge.mapper.DocumentMapper;
import com.clara.ops.challenge.document_management_service_challenge.repository.DocumentRepository;
import com.clara.ops.challenge.document_management_service_challenge.repository.TagRepository;
import com.clara.ops.challenge.document_management_service_challenge.repository.specification.DocumentSpecification;
import com.clara.ops.challenge.document_management_service_challenge.storage.StorageService;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

  private final DocumentRepository documentRepository;
  private final TagRepository tagRepository;
  private final StorageService storageService;
  private final DocumentMapper mapper;
  private final UploadLimiter limiter;

  @Override
  public UUID upload(UploadDocument request, MultipartFile file) {

    limiter.acquire();

    try (InputStream stream = file.getInputStream()) {
      validateFile(file);

      String path = request.user() + "/" + request.name();

      storageService.upload(path, stream, file);

      Document document =
          Document.builder()
              .id(UUID.randomUUID())
              .userName(request.user())
              .documentName(request.name())
              .minioPath(path)
              .fileSize(file.getSize())
              .fileType(file.getContentType())
              .createdAt(Instant.now())
              .build();

      Set<DocumentTag> documentTags = new HashSet<>();

      for (String tagName : request.tags()) {
        Tag tag =
            tagRepository
                .findByName(tagName)
                .orElseGet(() -> tagRepository.save(Tag.builder().name(tagName).build()));

        documentTags.add(
            DocumentTag.builder()
                .id(new DocumentTagId(document.getId(), tag.getId()))
                .document(document)
                .tag(tag)
                .build());
      }

      document.setTags(documentTags);
      documentRepository.save(document);

      return document.getId();
    } catch (InvalidDocumentException | StorageException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      limiter.release();
    }
  }

  @Transactional(readOnly = true)
  @Override
  public PaginatedDocumentSearch search(DocumentSearchFilters filters, Pageable pageable) {
    Specification<Document> spec =
        Specification.where(DocumentSpecification.hasUser(filters.user()))
            .and(DocumentSpecification.hasName(filters.name()))
            .and(DocumentSpecification.hasTags(filters.tags()));

    Page<Document> page = documentRepository.findAll(spec, pageable);

    List<DocumentDTO> docs = page.getContent().stream().map(mapper::toDto).toList();

    Metadata metadata =
        new Metadata(
            page.getNumber(),
            page.getSize(),
            page.getNumberOfElements(),
            page.getTotalPages(),
            page.getTotalElements());

    return new PaginatedDocumentSearch(metadata, docs);
  }

  @Override
  public DocumentDownloadUrl download(UUID id) {

    Document document =
        documentRepository
            .findById(id)
            .orElseThrow(() -> new DocumentNotFoundException(id.toString()));

    String url = storageService.generateDownloadUrl(document.getMinioPath());

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
