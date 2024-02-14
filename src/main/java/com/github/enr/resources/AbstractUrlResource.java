package com.github.enr.resources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

abstract class AbstractUrlResource implements Resource {

  private final String location;

  AbstractUrlResource(String location) {
    super();
    this.location = location;
  }

  @Override
  public boolean exists() {
    return url(location).isPresent();
  }

  @Override
  public byte[] getAsBytes() {
    try (InputStream is = getAsInputStream()) {
      if (is == null) {
        return new byte[0];
      }
      return is.readAllBytes();
    } catch (IOException e) {
      throw new ResourceLoadingException("error loading resource %s".formatted(location), e);
    }
  }

  @Override
  public InputStream getAsInputStream() {
    try {
      return urlOrFail(location).openStream();
    } catch (Exception e) {
      throw new ResourceLoadingException("error loading resource %s".formatted(location), e);
    }
  }

  @Override
  public String getAsString() {
    return new String(getAsBytes(), StandardCharsets.UTF_8);
  }

  private URL urlOrFail(String location) {
    Optional<URL> url = url(Objects.requireNonNull(location, "location must not be null"));
    if (url.isPresent()) {
      return url.get();
    }
    throw new ResourceLoadingException("resource not found %s".formatted(location));
  }

  protected abstract Optional<URL> url(String location);

}
