package com.clara.ops.challenge.document_management_service_challenge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Representa un documento PDF con metadata")
public record DocumentDTO(
    @Schema(
            description = "ID único del documento",
            example = "123e4567-e89b-12d3-a456-426614174000")
        String id,
    @Schema(description = "Usuario propietario del documento", example = "user1") String user,
    @Schema(description = "Nombre del documento", example = "documento.pdf") String name,
    @Schema(
            description = "Lista de tags asociados al documento",
            example = "[\"finanzas\",\"2026\"]")
        List<String> tags,
    @Schema(description = "Tamaño del archivo en bytes", example = "1048576") int size,
    @Schema(description = "Tipo de archivo", example = "application/pdf") String type,
    @Schema(description = "Fecha de creación en formato ISO-8601", example = "2026-03-27T10:00:00Z")
        String createdAt) {}
