package com.clara.ops.challenge.document_management_service_challenge.entity;

import lombok.*;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String documentName;

    @Column(nullable = false)
    private String minioPath;

    private long fileSize;

    private String fileType;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DocumentTag> tags = new HashSet<>();
}