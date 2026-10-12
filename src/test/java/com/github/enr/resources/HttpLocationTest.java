package com.github.enr.resources;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class HttpLocationTest {

  static Stream<ResourceSupportsTestCase> supportsInput() {
    return Stream.of(new ResourceSupportsTestCase(null, false), new ResourceSupportsTestCase("", false),
        new ResourceSupportsTestCase("/tmp/foo", false), new ResourceSupportsTestCase("relative/path", false),
        new ResourceSupportsTestCase("classpath:foo.txt", false), new ResourceSupportsTestCase("env:FOO", false),
        new ResourceSupportsTestCase("file:/tmp/foo", false), new ResourceSupportsTestCase("ftp://host/foo", false),
        new ResourceSupportsTestCase("http://localhost/foo", true),
        new ResourceSupportsTestCase("  https://example.com/foo  ", true),
        new ResourceSupportsTestCase("HTTPS://example.com/foo", true));
  }

  @ParameterizedTest
  @MethodSource("supportsInput")
  void testSupports(ResourceSupportsTestCase t) {
    assertThat(new HttpLocation().supports(t.uri())).as("supports (" + t.uri() + ")").isEqualTo(t.supported());
  }
}
