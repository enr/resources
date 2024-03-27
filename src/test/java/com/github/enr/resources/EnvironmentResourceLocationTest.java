package com.github.enr.resources;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.github.enr.system.EnvironmentSource;

class EnvironmentResourceLocationTest {

  static Stream<ResourceSupportsTestCase> testSupportsInput() {
    return Stream.of(
        // empty
        new ResourceSupportsTestCase(null, false), new ResourceSupportsTestCase("", false),
        new ResourceSupportsTestCase(" ", false), new ResourceSupportsTestCase("classpath:", false),
        new ResourceSupportsTestCase("env:TEST_VAR", true), new ResourceSupportsTestCase(" env:FOO ", true));
  }

  @ParameterizedTest
  @MethodSource("testSupportsInput")
  void testSupports(ResourceSupportsTestCase t) {
    EnvironmentLocation sut = new EnvironmentLocation();
    assertThat(sut.supports(t.uri())).as("supports (" + t.uri() + ")").isEqualTo(t.supported());
  }

  static Stream<ResourceContentsTestCase> testContentsInput() {
    return Stream.of(new ResourceContentsTestCase("env:FOO", "BAR"));
  }

  @ParameterizedTest
  @MethodSource("testContentsInput")
  void testRead(ResourceContentsTestCase t) {
    EnvironmentSource env = new MapBackedEnvironmentSource(Map.of("FOO", "BAR"));
    EnvironmentLocation sut = new EnvironmentLocation(env);
    Resource resource = sut.get(t.uri());
    assertThat(resource.exists()).as("exists (" + t.uri() + ")").isTrue();
    assertThat(resource.getAsString()).as("contents (" + t.uri() + ")").isEqualTo(t.contents());
  }

  static class MapBackedEnvironmentSource implements EnvironmentSource {
    private final Map<String, String> env;

    public MapBackedEnvironmentSource(Map<String, String> env) {
      this.env = env;
    }

    @Override
    public Map<String, String> getEnv() {
      return env;
    }

    @Override
    public String getEnvironmentVar(String key) {
      return env.get(key);
    }
  }

}
