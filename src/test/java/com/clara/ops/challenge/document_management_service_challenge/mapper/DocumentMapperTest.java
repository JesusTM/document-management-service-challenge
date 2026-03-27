package com.clara.ops.challenge.document_management_service_challenge.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.clara.ops.challenge.document_management_service_challenge.dto.DocumentDTO;
import com.clara.ops.challenge.document_management_service_challenge.entity.Document;
import com.clara.ops.challenge.document_management_service_challenge.entity.DocumentTag;
import com.clara.ops.challenge.document_management_service_challenge.entity.Tag;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DocumentMapperTest {

  private DocumentMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new DocumentMapper();
  }

  @Test
  void toDto_shouldMapDocumentToDtoCorrectly() {
    Tag tag1 = new Tag();
    tag1.setName("finance");
    Tag tag2 = new Tag();
    tag2.setName("report");

    DocumentTag docTag1 = new DocumentTag();
    docTag1.setTag(tag1);
    DocumentTag docTag2 = new DocumentTag();
    docTag2.setTag(tag2);

    Document document = new Document();
    document.setId(UUID.randomUUID());
    document.setUserName("john.doe");
    document.setDocumentName("report.pdf");
    document.setFileSize(1024L);
    document.setFileType("application/pdf");
    document.setCreatedAt(Instant.now());
    document.setTags(Set.of(docTag1, docTag2));

    DocumentDTO dto = mapper.toDto(document);

    assertEquals(document.getId().toString(), dto.id());
    assertEquals(document.getUserName(), dto.user());
    assertEquals(document.getDocumentName(), dto.name());
    assertEquals(document.getFileType(), dto.type());
    assertEquals((int) document.getFileSize(), dto.size());
    assertEquals(document.getCreatedAt().toString(), dto.createdAt());

    assertTrue(dto.tags().contains("finance"));
    assertTrue(dto.tags().contains("report"));
    assertEquals(2, dto.tags().size());
  }
}
