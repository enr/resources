package com.github.enr.resources;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Optional;

public class UrlResource extends AbstractUrlResource {

  public UrlResource(String location) {
    super(location);
  }

  /**
   * Checks that the resource can actually be reached: a HEAD request answering 2xx for HTTP(S) URLs,
   * a successful open for the other protocols.
   */
  @Override
  public boolean exists() {
    Optional<URL> url = url(location());
    if (url.isEmpty()) {
      return false;
    }
    try {
      if (HttpConnections.isHttp(url.get())) {
        HttpURLConnection connection = HttpConnections.open(url.get(), "HEAD", HttpConnections.CONNECT_TIMEOUT_MILLIS,
            HttpConnections.READ_TIMEOUT_MILLIS);
        connection.disconnect();
        return true;
      }
      try (InputStream is = openStream(url.get())) {
        return true;
      }
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  protected Optional<URL> url(String location) {
    try {
      URI uri = new URI(location);
      return Optional.ofNullable(uri.toURL());
    } catch (Exception e) {
      // NOOP
    }
    return Optional.empty();
  }

}
