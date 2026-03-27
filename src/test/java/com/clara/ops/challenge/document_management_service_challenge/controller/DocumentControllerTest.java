package com.clara.ops.challenge.document_management_service_challenge.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.clara.ops.challenge.document_management_service_challenge.dto.*;
import com.clara.ops.challenge.document_management_service_challenge.service.DocumentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DocumentController.class)
class DocumentControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private DocumentService documentService;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void uploadDocument_shouldReturnCreated() throws Exception {
    // Arrange
    UploadDocument uploadRequest = new UploadDocument("user1", "test.pdf", List.of("tag1", "tag2"));
    MockMultipartFile file =
        new MockMultipartFile(
            "file", "test.pdf", MediaType.APPLICATION_PDF_VALUE, "dummy content".getBytes());
    MockMultipartFile metadata =
        new MockMultipartFile(
            "metadata",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(uploadRequest));

    UUID generatedId = UUID.randomUUID();
    Mockito.when(documentService.upload(any(UploadDocument.class), any(MockMultipartFile.class)))
        .thenReturn(generatedId);

    // Act & Assert
    mockMvc
        .perform(multipart("/document-management/upload").file(file).file(metadata))
        .andExpect(status().isCreated())
        .andExpect(content().string(generatedId.toString()));
  }

  @Test
  void searchDocuments_shouldReturnOk() throws Exception {
    // Arrange
    DocumentSearchFilters filters = new DocumentSearchFilters("user1", "doc", List.of("tag1"));
    DocumentDTO doc =
        new DocumentDTO("id1", "user1", "doc1", List.of("tag1"), 100, "pdf", "2026-03-27T12:00:00");
    Metadata metadata = new Metadata(0, 20, 1, 1, 1);
    PaginatedDocumentSearch result = new PaginatedDocumentSearch(metadata, List.of(doc));

    Mockito.when(documentService.search(any(DocumentSearchFilters.class), any()))
        .thenReturn(result);

    // Act & Assert
    mockMvc
        .perform(
            post("/document-management/search")
                .param("page", "0")
                .param("size", "20")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filters)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.documents[0].id").value("id1"))
        .andExpect(jsonPath("$.metadata.totalItems").value(1));
  }

  @Test
  void downloadDocument_shouldReturnUrl() throws Exception {
    // Arrange
    UUID documentId = UUID.randomUUID();
    DocumentDownloadUrl downloadUrl =
        new DocumentDownloadUrl("http://example.com/download/" + documentId);
    Mockito.when(documentService.download(documentId)).thenReturn(downloadUrl);

    // Act & Assert
    mockMvc
        .perform(get("/document-management/download/{documentId}", documentId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.url").value(downloadUrl.url()));
  }
}
