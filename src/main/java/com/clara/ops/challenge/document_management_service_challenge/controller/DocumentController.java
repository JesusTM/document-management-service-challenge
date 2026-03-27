package com.clara.ops.challenge.document_management_service_challenge.controller;

import com.clara.ops.challenge.document_management_service_challenge.dto.DocumentDownloadUrl;
import com.clara.ops.challenge.document_management_service_challenge.dto.DocumentSearchFilters;
import com.clara.ops.challenge.document_management_service_challenge.dto.PaginatedDocumentSearch;
import com.clara.ops.challenge.document_management_service_challenge.dto.UploadDocument;
import com.clara.ops.challenge.document_management_service_challenge.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/document-management")
@RequiredArgsConstructor
@Tag(name = "Document Management", description = "API for document operations")
public class DocumentController {

  private final DocumentService documentService;

  @Operation(
      summary = "Upload a document",
      description = "Uploads a PDF document along with its metadata")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "Document uploaded successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
      })
  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<String> upload(
      @Parameter(description = "Document metadata as JSON", required = true)
          @Valid
          @RequestPart("metadata")
          UploadDocument request,
      @Parameter(description = "PDF file to upload", required = true) @RequestPart("file")
          MultipartFile file) {
    UUID id = documentService.upload(request, file);
    return ResponseEntity.status(HttpStatus.CREATED).body(id.toString());
  }

  @Operation(
      summary = "Search documents",
      description =
          "Search for documents using filters and pagination. Returns a paginated list of matching"
              + " documents.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Documents retrieved successfully",
            content = @Content(schema = @Schema(implementation = PaginatedDocumentSearch.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid search filters",
            content = @Content)
      })
  @PostMapping("/search")
  public ResponseEntity<PaginatedDocumentSearch> search(
      @Parameter(description = "Page number (0-based)", example = "0")
          @RequestParam(defaultValue = "0")
          int page,
      @Parameter(description = "Page size", example = "20") @RequestParam(defaultValue = "20")
          int size,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Filters to apply for document search",
              required = true,
              content = @Content(schema = @Schema(implementation = DocumentSearchFilters.class)))
          @Valid
          @RequestBody
          DocumentSearchFilters filters) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    return ResponseEntity.ok(documentService.search(filters, pageable));
  }

  @Operation(
      summary = "Download document",
      description = "Generates a download URL for the given document ID")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Download URL retrieved successfully",
            content = @Content(schema = @Schema(implementation = DocumentDownloadUrl.class))),
        @ApiResponse(responseCode = "404", description = "Document not found", content = @Content)
      })
  @GetMapping("/download/{documentId}")
  public ResponseEntity<DocumentDownloadUrl> download(
      @Parameter(description = "UUID of the document to download", required = true) @PathVariable
          UUID documentId) {
    return ResponseEntity.ok(documentService.download(documentId));
  }
}
