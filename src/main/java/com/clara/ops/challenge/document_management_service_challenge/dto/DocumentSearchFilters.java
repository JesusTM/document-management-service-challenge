package com.clara.ops.challenge.document_management_service_challenge.dto;

import jakarta.validation.constraints.Size;

import java.util.List;

public record DocumentSearchFilters(
        @Size(max = 100, message = "user must be less than 100 characters")
        String user,

        @Size(max = 255, message = "name must be less than 255 characters")
        String name,

        @Size(max = 10, message = "maximum 10 tags allowed")
        List<
            @Size(max = 50, message = "tag must be less than 50 characters")
        String> tags
) {}