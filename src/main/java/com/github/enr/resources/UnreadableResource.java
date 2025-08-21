package com.github.enr.resources;

import java.io.InputStream;
import java.nio.file.Path;

public class UnreadableResource implements Resource {

  private static final String UNREADABLE_RESOURCE_TPL = "Unreadable resource %s";
  private final String location;

  public UnreadableResource(String location) {
    super();
    this.location = location;
  }

  @Override
  public boolean exists() {
    return false;
  }

  @Override
  public byte[] getAsBytes() {
    throw new ResourceLoadingException(UNREADABLE_RESOURCE_TPL.formatted(location));
  }

  @Override
  public InputStream getAsInputStream() {
    throw new ResourceLoadingException(UNREADABLE_RESOURCE_TPL.formatted(location));
  }

  @Override
  public String getAsString() {
    throw new ResourceLoadingException(UNREADABLE_RESOURCE_TPL.formatted(location));
  }

  @Override
  public Path getAsPath(PathConversionStrategy strategy) {
    throw new ResourceLoadingException(UNREADABLE_RESOURCE_TPL.formatted(location));
  }

}
