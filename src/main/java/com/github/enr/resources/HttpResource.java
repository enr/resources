package com.github.enr.resources;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Objects;

public class HttpResource implements Resource {

  private static final int EXISTS_TIMEOUT_MILLIS = 5000;

  private final String location;
  private final URL url;

  public HttpResource(String location) {
    this.location = location;
    URL parsed;
    try {
      parsed = new URI(location).toURL();
    } catch (Exception e) {
      throw new ResourceLoadingException("Invalid URL: " + location, e);
    }
    if (!HttpConnections.isHttp(parsed)) {
      throw new ResourceLoadingException("Not an HTTP URL: " + location);
    }
    this.url = parsed;
  }

  @Override
  public boolean exists() {
    try {
      HttpURLConnection connection = HttpConnections.open(url, "HEAD", EXISTS_TIMEOUT_MILLIS, EXISTS_TIMEOUT_MILLIS);
      connection.disconnect();
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public byte[] getAsBytes() {
    try (InputStream is = getAsInputStream()) {
      return is.readAllBytes();
    } catch (IOException e) {
      throw new ResourceLoadingException("Error reading HTTP resource as bytes: " + location, e);
    }
  }

  @Override
  public InputStream getAsInputStream() {
    try {
      return HttpConnections.openStream(url, HttpConnections.CONNECT_TIMEOUT_MILLIS,
          HttpConnections.READ_TIMEOUT_MILLIS);
    } catch (IOException e) {
      throw new ResourceLoadingException("Error opening HTTP connection: " + location, e);
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
    if (strategy == PathConversionStrategy.STRICT) {
      throw new ResourceLoadingException("Cannot convert HTTP resource to Path with STRICT strategy");
    }
    return TempFiles.copyToTempFile("http-resource-", this::getAsInputStream);
  }

}
