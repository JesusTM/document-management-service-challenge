package com.clara.ops.challenge.document_management_service_challenge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.util.List;

@Schema(description = "Filtros opcionales para búsqueda de documentos")
public record DocumentSearchFilters(
    @Schema(description = "Usuario para filtrar", example = "user1")
        @Size(max = 100, message = "user must be less than 100 characters")
        String user,
    @Schema(description = "Nombre del documento para filtrar", example = "documento.pdf")
        @Size(max = 255, message = "name must be less than 255 characters")
        String name,
    @Schema(description = "Lista de tags para filtrar", example = "[\"finanzas\",\"2026\"]")
        @Size(max = 10, message = "maximum 10 tags allowed")
        List<@Size(max = 50, message = "tag must be less than 50 characters") String> tags) {}
