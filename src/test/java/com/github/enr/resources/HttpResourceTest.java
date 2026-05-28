package com.github.enr.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

class HttpResourceTest {

  @Test
  void getAsString_shouldReturnBody() throws IOException {
    try (MockWebServer server = new MockWebServer()) {
      server.enqueue(new MockResponse().addHeader("Content-Type", "text/plain; charset=utf-8").setBody("foo"));
      server.start();

      HttpResource resource = new HttpResource(server.url("/").toString());
      assertThat(resource.getAsString()).isEqualTo("foo");
    }
  }

  @Test
  void getAsString_withCharset_shouldReturnBody() throws IOException {
    try (MockWebServer server = new MockWebServer()) {
      server.enqueue(new MockResponse().setBody("hello"));
      server.start();

      HttpResource resource = new HttpResource(server.url("/").toString());
      assertThat(resource.getAsString(StandardCharsets.UTF_8)).isEqualTo("hello");
    }
  }

  @Test
  void exists_shouldReturnTrue_for200() throws IOException {
    try (MockWebServer server = new MockWebServer()) {
      server.enqueue(new MockResponse().setResponseCode(200));
      server.start();

      HttpResource resource = new HttpResource(server.url("/").toString());
      assertThat(resource.exists()).isTrue();
    }
  }

  @Test
  void exists_shouldReturnFalse_for404() throws IOException {
    try (MockWebServer server = new MockWebServer()) {
      server.enqueue(new MockResponse().setResponseCode(404));
      server.start();

      HttpResource resource = new HttpResource(server.url("/").toString());
      assertThat(resource.exists()).isFalse();
    }
  }

  @Test
  void getAsInputStream_shouldThrow_forErrorResponse() throws IOException {
    try (MockWebServer server = new MockWebServer()) {
      server.enqueue(new MockResponse().setResponseCode(500));
      server.start();

      HttpResource resource = new HttpResource(server.url("/").toString());
      assertThatThrownBy(resource::getAsInputStream).isInstanceOf(ResourceLoadingException.class)
          .hasMessageContaining("500");
    }
  }

  @Test
  void getAsPath_lenient_shouldCreateTempFile() throws IOException {
    try (MockWebServer server = new MockWebServer()) {
      server.enqueue(new MockResponse().setBody("content"));
      server.start();

      HttpResource resource = new HttpResource(server.url("/").toString());
      var path = resource.getAsPath(PathConversionStrategy.LENIENT);
      assertThat(Files.exists(path)).isTrue();
      assertThat(Files.readString(path)).isEqualTo("content");
      Files.deleteIfExists(path);
    }
  }

  @Test
  void getAsPath_strict_shouldThrowWithStrictMessage() throws IOException {
    try (MockWebServer server = new MockWebServer()) {
      server.start();

      HttpResource resource = new HttpResource(server.url("/").toString());
      assertThatThrownBy(() -> resource.getAsPath(PathConversionStrategy.STRICT))
          .isInstanceOf(ResourceLoadingException.class).hasMessageContaining("STRICT");
    }
  }

  @Test
  void getAsPath_shouldNotLeaveTempFile_whenServerReturnsError() throws IOException {
    try (MockWebServer server = new MockWebServer()) {
      server.enqueue(new MockResponse().setResponseCode(503));
      server.start();

      HttpResource resource = new HttpResource(server.url("/").toString());
      assertThatThrownBy(() -> resource.getAsPath(PathConversionStrategy.LENIENT))
          .isInstanceOf(ResourceLoadingException.class);

      java.io.File[] orphans = new java.io.File(System.getProperty("java.io.tmpdir"))
          .listFiles((d, n) -> n.startsWith("http-resource-") && n.endsWith(".tmp"));
      assertThat(orphans).as("no orphaned http-resource temp files").isEmpty();
    }
  }
}
