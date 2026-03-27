package com.clara.ops.challenge.document_management_service_challenge.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "document_tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentTag {

  @EmbeddedId private DocumentTagId id;

  @ManyToOne
  @MapsId("documentId")
  private Document document;

  @ManyToOne
  @MapsId("tagId")
  private Tag tag;
}
