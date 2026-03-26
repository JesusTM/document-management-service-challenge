package com.clara.ops.challenge.document_management_service_challenge.exception;

public class DocumentNotFoundException extends RuntimeException {

    public DocumentNotFoundException(String id) {
        super("Document not found: " + id);
    }
}