package com.clara.ops.challenge.document_management_service_challenge.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "URL temporal para descargar un documento")
public record DocumentDownloadUrl(
    @Schema(
            description = "URL de descarga del documento",
            example = "https://minio.example.com/doc1.pdf")
        String url) {}
