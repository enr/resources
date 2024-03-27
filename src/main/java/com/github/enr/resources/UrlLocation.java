package com.github.enr.resources;

import java.net.URI;

public class UrlLocation implements ResourceLocation {

  @Override
  public Resource get(String location) {
    return new UrlResource(location);
  }

  @Override
  public boolean supports(String path) {
    try {
      URI uri = new URI(path);
      return uri.toURL() != null;
    } catch (Exception e) {
      // NOOP
    }
    return false;
  }

}
