package com.github.enr.resources;

import java.lang.System.Logger.Level;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class FileSystemLocation implements ResourceLocation {

  private static final System.Logger LOG = System.getLogger(MethodHandles.lookup().lookupClass().getName());

  private static final String URI_SCHEME = "file";
  private static final String RESOURCE_PREFIX = URI_SCHEME + ":";

  @Override
  public Resource get(String location) {
    String path = Objects.requireNonNull(location, "uri must not be null").trim();
    path = Strings.removeStart(path, RESOURCE_PREFIX);
    path = FilePath.absoluteNormalized(path);
    return new FileSystemResource(path);
  }

  @Override
  public boolean supports(String u) {
    if (u == null || u.isBlank()) {
      return false;
    }
    String path = clean(u);
    try {

      URI uri = new URI(path);
      if (uri.getScheme() == null || URI_SCHEME.equals(uri.getScheme()) ||
      // could be Windows drive
          uri.getScheme().length() == 1) {
        return true;
      }
    } catch (URISyntaxException e) {
      LOG.log(Level.WARNING, "Error parsing resource path '{0}': {1}", path, e.getMessage());
    }
    return false;
  }

  private String clean(String location) {
    return FilePath.toSlash(location.trim()).replace(" ", "%20");
  }

}
