package com.github.enr.resources;

import java.io.InputStream;

public class UnreadableResource implements Resource {

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
    throw new ResourceLoadingException("resource not found %s".formatted(location));
  }

  @Override
  public InputStream getAsInputStream() {
    throw new ResourceLoadingException("resource not found %s".formatted(location));
  }

  @Override
  public String getAsString() {
    throw new ResourceLoadingException("resource not found %s".formatted(location));
  }

}
