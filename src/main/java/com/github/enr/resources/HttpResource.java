package com.github.enr.resources;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class HttpResource implements Resource {

  public HttpResource(String location) {
    // TODO Auto-generated constructor stub
  }

  @Override
  public boolean exists() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public byte[] getAsBytes() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public InputStream getAsInputStream() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getAsString() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Path toPath(PathConversionStrategy strategy) {
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
