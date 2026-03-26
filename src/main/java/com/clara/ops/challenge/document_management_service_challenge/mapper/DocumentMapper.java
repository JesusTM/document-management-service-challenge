package com.clara.ops.challenge.document_management_service_challenge.mapper;

import com.clara.ops.challenge.document_management_service_challenge.dto.DocumentDTO;
import com.clara.ops.challenge.document_management_service_challenge.entity.Document;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DocumentMapper {

    public DocumentDTO toDto(Document doc) {

        List<String> tags = doc.getTags().stream()
                .map(t -> t.getTag().getName()).toList();

        return new DocumentDTO(
                doc.getId().toString(),
                doc.getUserName(),
                doc.getDocumentName(),
                tags,
                (int) doc.getFileSize(),
                doc.getFileType(),
                doc.getCreatedAt().toString()
        );
    }
}