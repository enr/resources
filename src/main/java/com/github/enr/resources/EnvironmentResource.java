package com.github.enr.resources;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

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

  @Override
  public String getAsString(Charset charset) {
    return getAsString();
  }

  @Override
  public Path getAsPath(PathConversionStrategy strategy) {
    try {
      switch (strategy) {
        case STRICT:
          throw new ResourceLoadingException(
              "Cannot convert environment variable to Path with STRICT strategy: %s".formatted(key));

        case LENIENT:
        case FORCE_TEMPORARY:
          // Create a temporary file with the environment variable content
          Path tempFile = Files.createTempFile("env-" + key + "-", ".tmp");
          try (InputStream is = getAsInputStream()) {
            Files.copy(is, tempFile, StandardCopyOption.REPLACE_EXISTING);
          }
          return tempFile;

        default:
          throw new ResourceLoadingException("Unknown conversion strategy: %s".formatted(strategy));
      }
    } catch (IOException e) {
      throw new ResourceLoadingException("Error creating temporary file for environment variable: %s".formatted(key),
          e);
    }
  }

}
