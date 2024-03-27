package com.github.enr.resources;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.github.enr.system.EnvironmentSource;

public class EnvironmentResource implements Resource {

  private final String key;

  private final EnvironmentSource env;

  public EnvironmentResource(String key) {
    this(key, new EnvironmentSource() {});
  }

  public EnvironmentResource(String key, EnvironmentSource env) {
    this.key = key;
    this.env = env;
  }

  @Override
  public boolean exists() {
    return env.getEnvironmentVar(key) != null;
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
    String value = env.getEnvironmentVar(key);
    if (value == null) {
      throw new ResourceLoadingException("no environment variable %s".formatted(key));
    }
    return value;
  }

}
