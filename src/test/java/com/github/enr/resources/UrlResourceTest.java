package com.github.enr.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

class UrlResourceTest {

  @Test
  void exists_shouldReturnTrue_forReachableHttpUrl() throws IOException {
    try (MockWebServer server = new MockWebServer()) {
      server.enqueue(new MockResponse().setResponseCode(200));
      server.start();

      assertThat(new UrlResource(server.url("/").toString()).exists()).isTrue();
    }
  }

  @Test
  void exists_shouldReturnFalse_forHttp404() throws IOException {
    try (MockWebServer server = new MockWebServer()) {
      server.enqueue(new MockResponse().setResponseCode(404));
      server.start();

      assertThat(new UrlResource(server.url("/missing").toString()).exists()).isFalse();
    }
  }

  @Test
  void exists_shouldReturnFalse_forUnreachableHost() {
    assertThat(new UrlResource("http://nonexistent.invalid/foo").exists()).isFalse();
  }

  @Test
  void exists_shouldCheckFileUrls(@TempDir Path dir) throws IOException {
    Path file = dir.resolve("present.txt");
    Files.writeString(file, "x");

    assertThat(new UrlResource(file.toUri().toString()).exists()).isTrue();
    assertThat(new UrlResource(dir.resolve("absent.txt").toUri().toString()).exists()).isFalse();
  }

  @Test
  void getAsString_shouldFollowRedirect_andRejectRedirectPage() throws IOException {
    try (MockWebServer server = new MockWebServer()) {
      server.enqueue(new MockResponse().setResponseCode(302).addHeader("Location", "/target").setBody("redirect page"));
      server.enqueue(new MockResponse().setBody("final"));
      server.start();

      assertThat(new UrlResource(server.url("/start").toString()).getAsString()).isEqualTo("final");
    }
  }

  @Test
  void getAsString_shouldThrow_forHttpError() throws IOException {
    try (MockWebServer server = new MockWebServer()) {
      server.enqueue(new MockResponse().setResponseCode(500).setBody("error page"));
      server.start();

      UrlResource resource = new UrlResource(server.url("/").toString());
      assertThatThrownBy(resource::getAsString).isInstanceOf(ResourceLoadingException.class);
    }
  }
}
