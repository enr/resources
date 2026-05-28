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

  static Stream<ResourceContentsTestCase> testContentsInput() {
    return Stream.of(new ResourceContentsTestCase("classpath:com/github/enr/resources/cp-01.txt", "test cp-01 txt"),
        new ResourceContentsTestCase("classpath:/com/github/enr/resources/cp-01.txt", "test cp-01 txt"),
        new ResourceContentsTestCase("classpath:cp-02.txt", "test cp-02 txt"),
        new ResourceContentsTestCase("classpath:/cp-02.txt", "test cp-02 txt"),
        // leading/trailing whitespace must be trimmed before prefix removal
        new ResourceContentsTestCase(" classpath:cp-02.txt", "test cp-02 txt"),
        new ResourceContentsTestCase(" classpath:/com/github/enr/resources/cp-01.txt ", "test cp-01 txt"));
  }

  @ParameterizedTest
  @MethodSource("testContentsInput")
  void testRead(ResourceContentsTestCase t) {
    ClasspathLocation sut = new ClasspathLocation();
    assertThat(sut.supports(t.uri())).as("supports (" + t.uri() + ")").isTrue();
    Resource resource = sut.get(t.uri());
    assertThat(resource.exists()).as("exists (" + t.uri() + ")").isTrue();
    assertThat(resource.getAsString()).as("contents (" + t.uri() + ")").isEqualTo(t.contents());
  }
}
