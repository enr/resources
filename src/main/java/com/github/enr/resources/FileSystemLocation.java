package com.github.enr.resources;

import java.lang.System.Logger.Level;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Path;
import java.util.Objects;

public class FileSystemLocation implements ResourceLocation {

  private static final System.Logger LOG = System.getLogger(MethodHandles.lookup().lookupClass().getName());

  private static final String URI_SCHEME = "file";
  private static final String RESOURCE_PREFIX = URI_SCHEME + ":";

  @Override
  public Resource get(String location) {
    String path = Objects.requireNonNull(location, "uri must not be null").trim();
    if (hasFileScheme(path)) {
      path = fileUriToPath(path);
    }
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
      if (uri.getScheme() == null || URI_SCHEME.equalsIgnoreCase(uri.getScheme()) ||
      // could be Windows drive
          uri.getScheme().length() == 1) {
        return true;
      }
    } catch (URISyntaxException e) {
      LOG.log(Level.WARNING, "Error parsing resource path '{0}': {1}", path, e.getMessage());
    }
    return false;
  }

  private static boolean hasFileScheme(String location) {
    return location.regionMatches(true, 0, RESOURCE_PREFIX, 0, RESOURCE_PREFIX.length());
  }

  /**
   * Converts a {@code file:} URI to a path, decoding percent-encoded characters and handling forms
   * like {@code file:///C:/dir}. Falls back to stripping the scheme for inputs that are not valid
   * hierarchical URIs (e.g. {@code file:relative/path} or paths with unencoded spaces).
   */
  private static String fileUriToPath(String location) {
    try {
      URI uri = new URI(location);
      if (!uri.isOpaque() && uri.getAuthority() == null) {
        return Path.of(uri).toString();
      }
    } catch (URISyntaxException | IllegalArgumentException | FileSystemNotFoundException e) {
      // not a valid file URI: use the raw path
    }
    return location.substring(RESOURCE_PREFIX.length());
  }

  private String clean(String location) {
    return FilePath.toSlash(location.trim()).replace(" ", "%20");
  }

}
