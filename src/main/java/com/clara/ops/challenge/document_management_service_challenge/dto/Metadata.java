package com.clara.ops.challenge.document_management_service_challenge.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Metadata de paginación")
public record Metadata(
    @Schema(description = "Página actual", example = "1") int currentPage,
    @Schema(description = "Número de items por página", example = "10") int itemsPerPage,
    @Schema(description = "Número de items en la página actual", example = "10") int currentItems,
    @Schema(description = "Número total de páginas", example = "5") int totalPages,
    @Schema(description = "Número total de items", example = "50") long totalItems) {}
