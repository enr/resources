package com.github.enr.resources;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class EnvironmentResource implements Resource {

  private final String key;

  public EnvironmentResource(String key) {
    super();
    this.key = key;
  }

  @Override
  public boolean exists() {
    return System.getenv(key) != null;
  }

  @Override
  public byte[] getAsBytes() {
    return getAsString().getBytes(StandardCharsets.UTF_8);
  }

  @Override
  public InputStream getAsInputStream() {
    return new ByteArrayInputStream(getAsBytes());
  }

  @Override
  public String getAsString() {
    String value = System.getenv(key);
    if (value == null) {
      throw new ResourceLoadingException("no environment variable %s".formatted(key));
    }
    return value;
  }

}
