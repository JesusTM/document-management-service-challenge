package com.clara.ops.challenge.document_management_service_challenge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

@Schema(description = "DTO para subir un documento PDF")
public record UploadDocument(
    @NotBlank(message = "user is required")
        @Size(max = 100, message = "user must be less than 100 characters")
        @Schema(description = "Usuario que sube el documento", example = "user1")
        String user,
    @NotBlank(message = "name is required")
        @Size(max = 255, message = "name must be less than 255 characters")
        @Schema(description = "Nombre del documento", example = "documento.pdf")
        String name,
    @NotEmpty(message = "tags cannot be empty")
        @Size(max = 255, message = "tags must be less than 255 characters")
        @Schema(description = "Lista de tags del documento", example = "[\"finanzas\",\"2026\"]")
        List<@NotBlank @Size(max = 50, message = "tag must be less than 50 characters") String>
            tags) {}
