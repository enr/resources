package com.github.enr.resources;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

class HttpResourceTest {

  @Test
  void test() throws IOException {
    MockWebServer server = new MockWebServer();
    MockResponse response = new MockResponse().addHeader("Content-Type", "text/plain; charset=utf-8").setBody("foo");

    server.enqueue(response);
    server.start();

    HttpResource resource = new HttpResource(server.url("/").toString());
    assertThat(resource.getAsString()).isEqualTo("foo");

    server.shutdown();
    server.close();
  }
}
