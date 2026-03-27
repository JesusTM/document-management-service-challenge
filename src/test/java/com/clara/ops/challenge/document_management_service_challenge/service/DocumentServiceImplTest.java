package com.clara.ops.challenge.document_management_service_challenge.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.clara.ops.challenge.document_management_service_challenge.dto.*;
import com.clara.ops.challenge.document_management_service_challenge.entity.Document;
import com.clara.ops.challenge.document_management_service_challenge.entity.Tag;
import com.clara.ops.challenge.document_management_service_challenge.exception.DocumentNotFoundException;
import com.clara.ops.challenge.document_management_service_challenge.exception.InvalidDocumentException;
import com.clara.ops.challenge.document_management_service_challenge.mapper.DocumentMapper;
import com.clara.ops.challenge.document_management_service_challenge.repository.DocumentRepository;
import com.clara.ops.challenge.document_management_service_challenge.repository.TagRepository;
import com.clara.ops.challenge.document_management_service_challenge.storage.StorageService;
import java.io.ByteArrayInputStream;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class DocumentServiceImplTest {

  @InjectMocks private DocumentServiceImpl documentService;

  @Mock private DocumentRepository documentRepository;

  @Mock private TagRepository tagRepository;

  @Mock private StorageService storageService;

  @Mock private DocumentMapper mapper;

  @Mock private UploadLimiter limiter;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void upload_shouldReturnDocumentId() throws Exception {
    byte[] pdfHeader = "%PDF".getBytes();
    MockMultipartFile file =
        new MockMultipartFile("file", "test.pdf", "application/pdf", pdfHeader);

    UploadDocument request = new UploadDocument("user1", "doc1", List.of("tag1"));

    Tag tag = Tag.builder().id(Long.MIN_VALUE).name("tag1").build();
    when(tagRepository.findByName("tag1")).thenReturn(Optional.of(tag));

    when(documentRepository.save(any(Document.class))).thenAnswer(i -> i.getArguments()[0]);

    UUID result = documentService.upload(request, file);

    assertNotNull(result);
    verify(storageService).upload(eq("user1/doc1"), any(ByteArrayInputStream.class), eq(file));
    verify(documentRepository).save(any(Document.class));
    verify(limiter).acquire();
    verify(limiter).release();
  }

  @Test
  void upload_shouldThrowRuntimeException_forError() {
    byte[] pdfHeader = "%PDF".getBytes();
    MockMultipartFile file =
        new MockMultipartFile("file", "test.pdf", "application/pdf", pdfHeader);

    UploadDocument request = new UploadDocument("user1", "doc1", List.of());

    doThrow(new RuntimeException()).when(storageService).upload(anyString(), any(), any());

    assertThrows(RuntimeException.class, () -> documentService.upload(request, file));
  }

  @Test
  void upload_shouldThrow_whenFileExceeds500MB() throws Exception {
    MultipartFile file = mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getContentType()).thenReturn("application/pdf");
    when(file.getSize()).thenReturn(501L * 1024 * 1024); // 501 MB
    when(file.getInputStream()).thenReturn(new ByteArrayInputStream("%PDF".getBytes()));

    UploadDocument request = new UploadDocument("user1", "documento", java.util.List.of("tag1"));

    assertThrows(InvalidDocumentException.class, () -> documentService.upload(request, file));
  }

  @Test
  void upload_shouldThrowInvalidDocumentException_forEmptyFile() {
    MockMultipartFile file =
        new MockMultipartFile("file", "empty.pdf", "application/pdf", new byte[0]);

    UploadDocument request = new UploadDocument("user1", "doc1", List.of());

    assertThrows(InvalidDocumentException.class, () -> documentService.upload(request, file));
  }

  @Test
  void upload_shouldThrowInvalidDocumentException_forContentTypeWrong() {
    byte[] pdfHeader = "%PDF".getBytes();
    MockMultipartFile file =
        new MockMultipartFile("file", "test.pdf", "application/json", pdfHeader);

    UploadDocument request = new UploadDocument("user1", "doc1", List.of());

    assertThrows(InvalidDocumentException.class, () -> documentService.upload(request, file));
  }

  @Test
  void search_shouldReturnPaginatedResult() {
    DocumentSearchFilters filters = new DocumentSearchFilters("user1", "doc", List.of("tag1"));

    Document doc = Document.builder().id(UUID.randomUUID()).build();
    DocumentDTO dto =
        new DocumentDTO("id1", "user1", "doc1", List.of("tag1"), 100, "pdf", "2026-03-27T12:00:00");
    Page<Document> page = new PageImpl<>(List.of(doc), PageRequest.of(0, 10), 1);

    when(documentRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(page);
    when(mapper.toDto(doc)).thenReturn(dto);

    PaginatedDocumentSearch result = documentService.search(filters, PageRequest.of(0, 10));

    assertNotNull(result);
    assertEquals(1, result.metadata().totalItems());
    assertEquals(dto, result.documents().get(0));
  }

  @Test
  void download_shouldReturnDownloadUrl() {
    UUID docId = UUID.randomUUID();
    Document doc = Document.builder().id(docId).minioPath("user1/doc1").build();

    when(documentRepository.findById(docId)).thenReturn(Optional.of(doc));
    when(storageService.generateDownloadUrl("user1/doc1")).thenReturn("http://download-url");

    DocumentDownloadUrl result = documentService.download(docId);

    assertNotNull(result);
    assertEquals("http://download-url", result.url());
  }

  @Test
  void download_shouldThrowDocumentNotFoundException() {
    UUID docId = UUID.randomUUID();
    when(documentRepository.findById(docId)).thenReturn(Optional.empty());

    assertThrows(DocumentNotFoundException.class, () -> documentService.download(docId));
  }
}
