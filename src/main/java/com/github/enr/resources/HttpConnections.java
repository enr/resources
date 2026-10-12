package com.github.enr.resources;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Opens HTTP(S) connections with timeouts, accepting only 2xx responses and following redirects
 * (also across http -> https, which {@link HttpURLConnection} does not do on its own).
 */
final class HttpConnections {

  static final int CONNECT_TIMEOUT_MILLIS = 10_000;
  static final int READ_TIMEOUT_MILLIS = 30_000;

  private static final int MAX_REDIRECTS = 5;
  private static final String USER_AGENT = "HttpResource/1.0";

  private HttpConnections() {}

  static boolean isHttp(URL url) {
    String protocol = url.getProtocol();
    return "http".equalsIgnoreCase(protocol) || "https".equalsIgnoreCase(protocol);
  }

  /**
   * Returns a connection whose response code is 2xx; the caller must disconnect it.
   *
   * @throws ResourceLoadingException for non 2xx responses, invalid or too many redirects
   */
  static HttpURLConnection open(URL url, String method, int connectTimeout, int readTimeout) throws IOException {
    URL current = url;
    for (int redirects = 0;; redirects++) {
      if (!isHttp(current)) {
        throw new ResourceLoadingException("Not an HTTP URL: %s".formatted(current));
      }
      HttpURLConnection connection = (HttpURLConnection) current.openConnection();
      String location;
      try {
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod(method);
        connection.setConnectTimeout(connectTimeout);
        connection.setReadTimeout(readTimeout);
        connection.setRequestProperty("User-Agent", USER_AGENT);
        int code = connection.getResponseCode();
        if (code >= 200 && code < 300) {
          return connection;
        }
        if (!isRedirect(code)) {
          throw new ResourceLoadingException("HTTP error %d for URL: %s".formatted(code, current));
        }
        location = connection.getHeaderField("Location");
      } catch (IOException | RuntimeException e) {
        connection.disconnect();
        throw e;
      }
      connection.disconnect();
      if (redirects >= MAX_REDIRECTS) {
        throw new ResourceLoadingException("Too many redirects for URL: %s".formatted(url));
      }
      current = resolveRedirect(current, location);
    }
  }

  /**
   * Opens a GET connection and returns its body; closing the stream disconnects the connection.
   */
  static InputStream openStream(URL url, int connectTimeout, int readTimeout) throws IOException {
    HttpURLConnection connection = open(url, "GET", connectTimeout, readTimeout);
    try {
      return new FilterInputStream(connection.getInputStream()) {
        @Override
        public void close() throws IOException {
          try {
            super.close();
          } finally {
            connection.disconnect();
          }
        }
      };
    } catch (IOException | RuntimeException e) {
      connection.disconnect();
      throw e;
    }
  }

  private static boolean isRedirect(int code) {
    return code == 301 || code == 302 || code == 303 || code == 307 || code == 308;
  }

  private static URL resolveRedirect(URL current, String location) throws IOException {
    if (location == null || location.isBlank()) {
      throw new ResourceLoadingException("Redirect without Location header for URL: %s".formatted(current));
    }
    URL next;
    try {
      next = current.toURI().resolve(location.trim()).toURL();
    } catch (URISyntaxException | IllegalArgumentException e) {
      throw new ResourceLoadingException("Invalid redirect location '%s' for URL: %s".formatted(location, current), e);
    }
    if (!isHttp(next)) {
      throw new ResourceLoadingException("Redirect to non HTTP URL %s from %s".formatted(next, current));
    }
    if ("https".equalsIgnoreCase(current.getProtocol()) && "http".equalsIgnoreCase(next.getProtocol())) {
      throw new ResourceLoadingException("Refusing redirect from HTTPS to HTTP: %s -> %s".formatted(current, next));
    }
    return next;
  }
}
