package com.github.enr.resources;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class FileSystemResourceLocationTest {

  static Stream<ResourceSupportsTestCase> osAgnosticSupportsInput() {
    return Stream.of(
        // empty
        new ResourceSupportsTestCase(null, false), new ResourceSupportsTestCase("", false),
        new ResourceSupportsTestCase(" ", false), new ResourceSupportsTestCase("file:", false),
        // win
        new ResourceSupportsTestCase("file:C:/with/scheme", true),
        new ResourceSupportsTestCase("C:/win/with/slash", true),
        // new ResourceSupportsTestCase("C:\\with\\backslash", true),
        // happy paths
        new ResourceSupportsTestCase("file:/with/scheme", true),
        new ResourceSupportsTestCase("file:/path/with spaces", true),
        new ResourceSupportsTestCase("  file:/with/scheme", true), new ResourceSupportsTestCase("relative/path", true),
        new ResourceSupportsTestCase("./relative/path", true),
        new ResourceSupportsTestCase("file://./relative/path", true));
  }

  static Stream<ResourceSupportsTestCase> windowsSupportsInput() {
    return Stream.of(new ResourceSupportsTestCase("C:\\with\\backslash", true));
  }

  @ParameterizedTest
  @MethodSource("osAgnosticSupportsInput")
  void testSupports(ResourceSupportsTestCase t) {
    FileSystemLocation sut = new FileSystemLocation();
    assertThat(sut.supports(t.uri())).as("supports (" + t.uri() + ")").isEqualTo(t.supported());
  }

  @EnabledOnOs({OS.WINDOWS})
  @ParameterizedTest
  @MethodSource("windowsSupportsInput")
  void testSupportsOnWin(ResourceSupportsTestCase t) {
    FileSystemLocation sut = new FileSystemLocation();
    assertThat(sut.supports(t.uri())).as("supports (" + t.uri() + ")").isEqualTo(t.supported());
  }

}
