package com.clara.ops.challenge.document_management_service_challenge.model;

import lombok.*;
import java.time.Instant;
import java.util.*;

@Data
@Builder
public class Document {
    private UUID id;
    private String userName;
    private String documentName;
    private String minioPath;
    private long fileSize;
    private String fileType;
    private Instant createdAt;
}