package com.github.enr.resources;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class DefaultResourceLoaderTest {

  static Stream<ResourceContentsTestCase> testContentsInput() {
    return Stream.of(new ResourceContentsTestCase("classpath:com/github/enr/resources/cp-01.txt", "test cp-01 txt"),
        new ResourceContentsTestCase("classpath:/com/github/enr/resources/cp-01.txt", "test cp-01 txt"),
        new ResourceContentsTestCase("classpath:cp-02.txt", "test cp-02 txt"),
        new ResourceContentsTestCase("classpath:/cp-02.txt", "test cp-02 txt"));
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
