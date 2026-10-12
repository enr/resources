package com.github.enr.resources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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
    try {
      Optional<URL> url = url(location);
      return url.isPresent();
    } catch (Exception e) {
      // NOOP
    }
    return false;
  }

  @Override
  public byte[] getAsBytes() {
    try (InputStream is = getAsInputStream()) {
      if (is == null) {
        return new byte[0];
      }
      return is.readAllBytes();
    } catch (Exception e) {
      throw new ResourceLoadingException("error loading resource %s".formatted(location), e);
    }
  }

  @Override
  public InputStream getAsInputStream() {
    try {
      return openStream(urlOrFail(location));
    } catch (Exception e) {
      throw new ResourceLoadingException("error loading resource %s".formatted(location), e);
    }
  }

  @Override
  public String getAsString() {
    return getAsString(StandardCharsets.UTF_8);
  }

  @Override
  public String getAsString(Charset charset) {
    return new String(getAsBytes(), charset);
  }

  @Override
  public Path getAsPath(PathConversionStrategy strategy) {
    Objects.requireNonNull(strategy, "strategy must not be null");
    URL url = urlOrFail(location);
    if (strategy == PathConversionStrategy.FORCE_TEMPORARY) {
      return TempFiles.copyToTempFile("resource-", this::getAsInputStream);
    }
    Optional<Path> direct = directPath(url);
    if (direct.isPresent()) {
      return direct.get();
    }
    if (strategy == PathConversionStrategy.STRICT) {
      throw new ResourceLoadingException(
          "Cannot convert URL resource to Path with STRICT strategy: %s".formatted(location));
    }
    return TempFiles.copyToTempFile("resource-", this::getAsInputStream);
  }

  private static Optional<Path> directPath(URL url) {
    if (!"file".equalsIgnoreCase(url.getProtocol())) {
      return Optional.empty();
    }
    try {
      Path filePath = Path.of(url.toURI());
      return Files.exists(filePath) ? Optional.of(filePath) : Optional.empty();
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  /**
   * Opens the given URL with connection and read timeouts; HTTP(S) URLs must answer with a 2xx
   * status.
   */
  protected InputStream openStream(URL url) throws IOException {
    if (HttpConnections.isHttp(url)) {
      return HttpConnections.openStream(url, HttpConnections.CONNECT_TIMEOUT_MILLIS,
          HttpConnections.READ_TIMEOUT_MILLIS);
    }
    URLConnection connection = url.openConnection();
    connection.setConnectTimeout(HttpConnections.CONNECT_TIMEOUT_MILLIS);
    connection.setReadTimeout(HttpConnections.READ_TIMEOUT_MILLIS);
    return connection.getInputStream();
  }

  protected String location() {
    return location;
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
