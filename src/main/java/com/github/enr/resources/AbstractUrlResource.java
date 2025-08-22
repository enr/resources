package com.github.enr.resources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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
    return getAsString(StandardCharsets.UTF_8);
  }

  @Override
  public String getAsString(Charset charset) {
    return new String(getAsBytes(), charset);
  }

  @Override
  public Path getAsPath(PathConversionStrategy strategy) {
    try {
      URL url = urlOrFail(location);

      // Try to convert URL to file path directly
      if ("file".equals(url.getProtocol())) {
        try {
          Path filePath = Path.of(url.toURI());
          if (Files.exists(filePath)) {
            return filePath;
          }
        } catch (Exception e) {
          // Fall through to other strategies
        }
      }

      // Handle different strategies
      switch (strategy) {
        case STRICT:
          throw new ResourceLoadingException(
              "Cannot convert URL resource to Path with STRICT strategy: %s".formatted(location));

        case LENIENT:
          // Try to create a temporary file if direct conversion is not possible
          return createTemporaryFile();

        case FORCE_TEMPORARY:
          // Always create a temporary file
          return createTemporaryFile();

        default:
          throw new ResourceLoadingException("Unknown conversion strategy: %s".formatted(strategy));
      }
    } catch (Exception e) {
      throw new ResourceLoadingException("Error converting resource to Path: %s".formatted(location), e);
    }
  }

  private Path createTemporaryFile() throws IOException {
    Path tempFile = Files.createTempFile("resource-", ".tmp");
    try (InputStream is = getAsInputStream()) {
      Files.copy(is, tempFile, StandardCopyOption.REPLACE_EXISTING);
    }
    return tempFile;
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
