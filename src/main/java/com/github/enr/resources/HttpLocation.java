package com.github.enr.resources;

import java.net.URI;
import java.net.URISyntaxException;

public class HttpLocation implements ResourceLocation {

  @Override
  public Resource get(String location) {
    return new HttpResource(location.trim());
  }

  @Override
  public boolean supports(String path) {
    if (path == null) {
      return false;
    }
    try {
      String scheme = new URI(path.trim()).getScheme();
      return "http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme);
    } catch (URISyntaxException e) {
      return false;
    }
  }

}
