package com.clara.ops.challenge.document_management_service_challenge.exception;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler handler;

  @BeforeEach
  void setUp() {
    handler = new GlobalExceptionHandler();
  }

  @Test
  void handleDocumentNotFound_shouldReturnNotFound() {
    DocumentNotFoundException ex = new DocumentNotFoundException("1");

    ResponseEntity<ApiError> response = handler.handleDocumentNotFound(ex);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Not Found", response.getBody().error());
    assertEquals("Document not found: 1", response.getBody().message());
    assertNotNull(response.getBody().timestamp());
  }

  @Test
  void handleInvalidDocument_shouldReturnBadRequest() {
    InvalidDocumentException ex = new InvalidDocumentException("Invalid PDF");

    ResponseEntity<ApiError> response = handler.handleInvalidDocument(ex);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Bad Request", response.getBody().error());
    assertEquals("Invalid PDF", response.getBody().message());
    assertNotNull(response.getBody().timestamp());
  }

  @Test
  void handleStorageError_shouldReturnInternalServerError() {
    StorageException ex = new StorageException("Minio error");

    ResponseEntity<ApiError> response = handler.handleStorageError(ex);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Storage Error", response.getBody().error());
    assertEquals("Minio error", response.getBody().message());
    assertNotNull(response.getBody().timestamp());
  }

  @Test
  void handleValidation_shouldReturnBadRequest() {
    FieldError fieldError = new FieldError("object", "field", "must not be null");
    BindingResult bindingResult = mock(BindingResult.class);
    when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

    MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
    when(ex.getBindingResult()).thenReturn(bindingResult);

    ResponseEntity<ApiError> response = handler.handleValidation(ex);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Validation Error", response.getBody().error());
    assertEquals("field must not be null", response.getBody().message());
    assertNotNull(response.getBody().timestamp());
  }

  @Test
  void handleGenericException_shouldReturnInternalServerError() {
    Exception ex = new Exception("Something went wrong");

    ResponseEntity<ApiError> response = handler.handleGenericException(ex);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Internal Server Error", response.getBody().error());
    assertEquals("Something went wrong", response.getBody().message());
    assertNotNull(response.getBody().timestamp());
  }
}
