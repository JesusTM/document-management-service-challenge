package com.clara.ops.challenge.document_management_service_challenge.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.Semaphore;
import org.junit.jupiter.api.Test;

class UploadLimiterTest {

  @Test
  void acquire_and_release_shouldWorkNormally() {
    UploadLimiter limiter = new UploadLimiter();

    assertDoesNotThrow(limiter::acquire);
    assertDoesNotThrow(limiter::release);
  }

  @Test
  void acquire_shouldThrowRuntimeException_whenInterrupted()
      throws InterruptedException, NoSuchFieldException, IllegalAccessException {
    Semaphore semaphoreMock = mock(Semaphore.class);
    doThrow(new InterruptedException()).when(semaphoreMock).acquire();

    UploadLimiter limiter = new UploadLimiter();
    var field = UploadLimiter.class.getDeclaredField("semaphore");
    field.setAccessible(true);
    field.set(limiter, semaphoreMock);

    RuntimeException exception = assertThrows(RuntimeException.class, limiter::acquire);
    assertTrue(exception.getCause() instanceof InterruptedException);
  }
}
