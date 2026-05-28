package com.github.enr.resources;

import java.io.FilterInputStream;
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
    HttpURLConnection connection = null;
    try {
      connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("HEAD");
      connection.setConnectTimeout(5000);
      connection.setReadTimeout(5000);
      int responseCode = connection.getResponseCode();
      return responseCode >= 200 && responseCode < 400;
    } catch (Exception e) {
      return false;
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
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
    HttpURLConnection connection = null;
    try {
      connection = (HttpURLConnection) url.openConnection();
      connection.setConnectTimeout(10000);
      connection.setReadTimeout(30000);
      connection.setRequestProperty("User-Agent", "HttpResource/1.0");

      int responseCode = connection.getResponseCode();
      if (responseCode >= 200 && responseCode < 400) {
        final HttpURLConnection conn = connection;
        return new FilterInputStream(connection.getInputStream()) {
          @Override
          public void close() throws IOException {
            try {
              super.close();
            } finally {
              conn.disconnect();
            }
          }
        };
      } else {
        connection.disconnect();
        throw new ResourceLoadingException("HTTP error " + responseCode + " for URL: " + location);
      }
    } catch (ResourceLoadingException e) {
      throw e;
    } catch (IOException e) {
      if (connection != null) {
        connection.disconnect();
      }
      throw new ResourceLoadingException("Error opening HTTP connection: " + location, e);
    }
  }

  @Override
  public String getAsString() {
    return getAsString(StandardCharsets.UTF_8);
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
          Path tempFile = Files.createTempFile("http-resource-", ".tmp");
          boolean success = false;
          try (InputStream is = getAsInputStream()) {
            Files.copy(is, tempFile, StandardCopyOption.REPLACE_EXISTING);
            success = true;
          } finally {
            if (!success) {
              try {
                Files.deleteIfExists(tempFile);
              } catch (IOException ignored) {
              }
            }
          }
          return tempFile;

        default:
          throw new ResourceLoadingException("Unknown conversion strategy: %s".formatted(strategy));
      }
    } catch (ResourceLoadingException e) {
      throw e;
    } catch (IOException e) {
      throw new ResourceLoadingException("Error creating temporary file for HTTP resource", e);
    }
  }

}
