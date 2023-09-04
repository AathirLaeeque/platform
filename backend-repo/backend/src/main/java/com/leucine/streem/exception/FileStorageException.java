package com.leucine.streem.exception;

public class FileStorageException extends RuntimeException {
  public FileStorageException(final String message) {
    super(message);
  }

  public FileStorageException(final String message, Throwable cause) {
    super(message, cause);
  }
}
