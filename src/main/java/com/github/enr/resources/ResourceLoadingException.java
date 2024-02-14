package com.github.enr.resources;

public class ResourceLoadingException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ResourceLoadingException() {
    super();
  }

  public ResourceLoadingException(String message, Throwable cause) {
    super(message, cause);
  }

  public ResourceLoadingException(String message) {
    super(message);
  }

  public ResourceLoadingException(Throwable cause) {
    super(cause);
  }

}
