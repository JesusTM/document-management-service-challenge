package com.clara.ops.challenge.document_management_service_challenge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Resultado de búsqueda paginada")
public record PaginatedDocumentSearch(Metadata metadata, List<DocumentDTO> documents) {}
