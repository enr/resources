package com.github.enr.resources;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    try {
      return Files.readString(path, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new ResourceLoadingException(ERROR_READING_RESOURCE_TPL.formatted(path), e);
    }
  }

}
