package com.clara.ops.challenge.document_management_service_challenge.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DocumentNotFoundException.class)
    public ResponseEntity<ApiError> handleDocumentNotFound(
            DocumentNotFoundException ex) {

        ApiError error = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                Instant.now()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(InvalidDocumentException.class)
    public ResponseEntity<ApiError> handleInvalidDocument(
            InvalidDocumentException ex) {

        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                Instant.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<ApiError> handleStorageError(
            StorageException ex) {

        ApiError error = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Storage Error",
                ex.getMessage(),
                Instant.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex) {

        String message =
                ex.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .findFirst()
                        .map(e -> e.getField() + " " + e.getDefaultMessage())
                        .orElse("Validation error");

        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                message,
                Instant.now()
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex) {

        ApiError error = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage(),
                Instant.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}