package com.github.enr.resources;

import java.net.URI;

public class UrlLocation implements ResourceLocation {

  @Override
  public Resource get(String location) {
    return new UrlResource(location);
  }

  @Override
  public boolean supports(String path) {
    if (path == null) {
      return false;
    }
    try {
      URI uri = new URI(path.trim());
      String scheme = uri.getScheme();
      // Exclude file: URIs — those belong to FileSystemLocation
      if (scheme == null || scheme.equalsIgnoreCase("file")) {
        return false;
      }
      return uri.toURL() != null;
    } catch (Exception e) {
      // NOOP
    }
    return false;
  }

}
