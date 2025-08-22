package com.github.enr.resources;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class HttpResource implements Resource {

  private final String location;
  private final URL url;

  public HttpResource(String location) {
    this.location = location;
    try {
      URI uri = new URI(location);
      this.url = uri.toURL();
    } catch (Exception e) {
      throw new ResourceLoadingException("Invalid URL: " + location, e);
    }
  }

  @Override
  public boolean exists() {
    try {
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("HEAD");
      connection.setConnectTimeout(5000);
      connection.setReadTimeout(5000);

      int responseCode = connection.getResponseCode();
      return responseCode >= 200 && responseCode < 400;
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public byte[] getAsBytes() {
    try (InputStream is = getAsInputStream()) {
      if (is == null) {
        return new byte[0];
      }
      return is.readAllBytes();
    } catch (IOException e) {
      throw new ResourceLoadingException("Error reading HTTP resource as bytes: " + location, e);
    }
  }

  @Override
  public InputStream getAsInputStream() {
    try {
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setConnectTimeout(10000);
      connection.setReadTimeout(30000);
      connection.setRequestProperty("User-Agent", "HttpResource/1.0");

      int responseCode = connection.getResponseCode();
      if (responseCode >= 200 && responseCode < 400) {
        return connection.getInputStream();
      } else {
        throw new ResourceLoadingException("HTTP error " + responseCode + " for URL: " + location);
      }
    } catch (IOException e) {
      throw new ResourceLoadingException("Error opening HTTP connection: " + location, e);
    }
  }

  @Override
  public String getAsString() {
    try {
      byte[] bytes = getAsBytes();
      return new String(bytes, StandardCharsets.UTF_8);
    } catch (Exception e) {
      throw new ResourceLoadingException("Error reading HTTP resource as string: " + location, e);
    }
  }

  @Override
  public byte[] getAsBytes(Charset charset) {
    try {
      byte[] bytes = getAsBytes();
      return new String(bytes, StandardCharsets.UTF_8).getBytes(charset);
    } catch (Exception e) {
      throw new ResourceLoadingException("Error reading HTTP resource as bytes with charset: " + location, e);
    }
  }

  @Override
  public String getAsString(Charset charset) {
    try {
      byte[] bytes = getAsBytes();
      return new String(bytes, charset);
    } catch (Exception e) {
      throw new ResourceLoadingException("Error reading HTTP resource as string with charset: " + location, e);
    }
  }

  @Override
  public Path getAsPath(PathConversionStrategy strategy) {
    try {
      switch (strategy) {
        case STRICT:
          throw new ResourceLoadingException("Cannot convert HTTP resource to Path with STRICT strategy");

        case LENIENT:
        case FORCE_TEMPORARY:
          // Create a temporary file with the HTTP resource content
          Path tempFile = Files.createTempFile("http-resource-", ".tmp");
          try (InputStream is = getAsInputStream()) {
            if (is != null) {
              Files.copy(is, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }
          }
          return tempFile;

        default:
          throw new ResourceLoadingException("Unknown conversion strategy: %s".formatted(strategy));
      }
    } catch (IOException e) {
      throw new ResourceLoadingException("Error creating temporary file for HTTP resource", e);
    }
  }

}
