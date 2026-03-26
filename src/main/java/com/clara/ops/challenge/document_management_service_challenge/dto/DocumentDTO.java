package com.clara.ops.challenge.document_management_service_challenge.dto;

import java.util.List;

public record DocumentDTO(
        String id,
        String user,
        String name,
        List<String> tags,
        int size,
        String type,
        String createdAt
) {}
