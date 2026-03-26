package com.clara.ops.challenge.document_management_service_challenge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UploadDocument(
        @NotBlank(message = "user is required")
        @Size(max = 100, message = "user must be less than 100 characters")
        String user,

        @NotBlank(message = "name is required")
        @Size(max = 255, message = "name must be less than 255 characters")
        String name,

        @NotBlank(message = "name is required")
        @Size(max = 255, message = "name must be less than 255 characters")
        List<
                @NotBlank(message = "tag cannot be blank")
                @Size(max = 50, message = "tag must be less than 50 characters")
        String> tags
) {}