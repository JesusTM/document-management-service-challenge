package com.clara.ops.challenge.document_management_service_challenge.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DocumentTagId implements Serializable {

  private UUID documentId;

  private Long tagId;
}
