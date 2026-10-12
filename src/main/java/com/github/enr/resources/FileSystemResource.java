package com.github.enr.resources;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class FileSystemResource implements Resource {

  private static final String ERROR_READING_RESOURCE_TPL = "Error reading resource %s";
  private final Path path;

  public FileSystemResource(String location) {
    super();
    this.path = Paths.get(location).toAbsolutePath().normalize();
  }

  @Override
  public boolean exists() {
    return Files.exists(path);
  }

  @Override
  public byte[] getAsBytes() {
    try {
      return Files.readAllBytes(path);
    } catch (IOException e) {
      throw new ResourceLoadingException(ERROR_READING_RESOURCE_TPL.formatted(path), e);
    }
  }

  @Override
  public InputStream getAsInputStream() {
    try {
      return Files.newInputStream(path);
    } catch (IOException e) {
      throw new ResourceLoadingException(ERROR_READING_RESOURCE_TPL.formatted(path), e);
    }
  }

  @Override
  public String getAsString() {
    return getAsString(StandardCharsets.UTF_8);
  }

  @Override
  public String getAsString(Charset charset) {
    try {
      return Files.readString(path, charset);
    } catch (IOException e) {
      throw new ResourceLoadingException(ERROR_READING_RESOURCE_TPL.formatted(path), e);
    }
  }

  @Override
  public Path getAsPath(PathConversionStrategy strategy) {
    Objects.requireNonNull(strategy, "strategy must not be null");
    if (!Files.exists(path)) {
      throw new ResourceLoadingException("Resource not found %s".formatted(path));
    }
    if (strategy == PathConversionStrategy.FORCE_TEMPORARY) {
      return TempFiles.copyToTempFile("resource-", this::getAsInputStream);
    }
    return path;
  }

}
