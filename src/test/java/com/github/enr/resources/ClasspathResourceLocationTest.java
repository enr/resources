package com.github.enr.resources;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class ClasspathResourceLocationTest {

  static Stream<ResourceSupportsTestCase> testSupportsInput() {
    return Stream.of(
        // empty
        new ResourceSupportsTestCase(null, false), new ResourceSupportsTestCase("", false),
        new ResourceSupportsTestCase(" ", false), new ResourceSupportsTestCase("classpath:", false),
        new ResourceSupportsTestCase("classpath:relative-resource", true),
        new ResourceSupportsTestCase(" classpath:/abs/resource ", true));
  }

  @ParameterizedTest
  @MethodSource("testSupportsInput")
  void testSupports(ResourceSupportsTestCase t) {
    ClasspathLocation sut = new ClasspathLocation();
    assertThat(sut.supports(t.uri())).as("supports (" + t.uri() + ")").isEqualTo(t.supported());
  }
}
