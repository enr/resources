package com.github.enr.resources;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;

public class UrlResource extends AbstractUrlResource {

  public UrlResource(String location) {
    super(location);
  }

  @Override
  protected Optional<URL> url(String location) {
    try {
      URI uri = new URI(location);
      return Optional.ofNullable(uri.toURL());
    } catch (URISyntaxException | MalformedURLException e) {
      // NOOP
    }
    return Optional.empty();
  }

}
