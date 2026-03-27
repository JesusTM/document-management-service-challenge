package com.clara.ops.challenge.document_management_service_challenge.service;

import java.util.concurrent.Semaphore;
import org.springframework.stereotype.Component;

@Component
public class UploadLimiter {

  private final Semaphore semaphore = new Semaphore(10);

  public void acquire() {
    try {
      semaphore.acquire();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public void release() {
    semaphore.release();
  }
}
