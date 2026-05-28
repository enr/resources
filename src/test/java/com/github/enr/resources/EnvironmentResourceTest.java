package com.github.enr.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.github.enr.system.EnvironmentSource;

class EnvironmentResourceTest {

  private static EnvironmentResource resource(String key, String value) {
    EnvironmentSource env = new MapEnvironmentSource(Map.of(key, value));
    return new EnvironmentResource(key, env);
  }

  @Test
  void getAsString_withCharset_usesCharsetToDecodeBytesNotIgnoresIt() {
    EnvironmentResource r = resource("KEY", "hello");
    // After the fix: getAsString(charset) == new String(getAsBytes(), charset)
    byte[] bytes = r.getAsBytes();
    assertThat(r.getAsString(StandardCharsets.UTF_8)).isEqualTo(new String(bytes, StandardCharsets.UTF_8));
  }

  @Test
  void getAsString_withUtf8_returnsCorrectValue() {
    EnvironmentResource r = resource("KEY", "hello");
    assertThat(r.getAsString(StandardCharsets.UTF_8)).isEqualTo("hello");
  }

  @Test
  void getAsBytes_andGetAsString_areConsistent() {
    EnvironmentResource r = resource("MSG", "test-value");
    byte[] bytes = r.getAsBytes();
    // round-trip: bytes encoded as UTF-8, decoded back as UTF-8 must equal original
    assertThat(new String(bytes, StandardCharsets.UTF_8)).isEqualTo("test-value");
    assertThat(r.getAsString(StandardCharsets.UTF_8)).isEqualTo(new String(bytes, StandardCharsets.UTF_8));
  }

  @Test
  void exists_returnsFalse_forMissingKey() {
    EnvironmentSource env = new MapEnvironmentSource(Map.of());
    EnvironmentResource r = new EnvironmentResource("MISSING_KEY", env);
    assertThat(r.exists()).isFalse();
  }

  @Test
  void getAsString_throwsResourceLoadingException_forMissingKey() {
    EnvironmentSource env = new MapEnvironmentSource(Map.of());
    EnvironmentResource r = new EnvironmentResource("MISSING_KEY", env);
    assertThatThrownBy(r::getAsString).isInstanceOf(ResourceLoadingException.class).hasMessageContaining("MISSING_KEY");
  }

  @Test
  void getAsString_returnsEmptyString_forEmptyValue() {
    EnvironmentSource env = new MapEnvironmentSource(Map.of("EMPTY_KEY", ""));
    EnvironmentResource r = new EnvironmentResource("EMPTY_KEY", env);
    assertThat(r.exists()).isTrue();
    assertThat(r.getAsString()).isEmpty();
    assertThat(r.getAsBytes()).isEmpty();
  }

  private static class MapEnvironmentSource implements EnvironmentSource {
    private final Map<String, String> map;

    MapEnvironmentSource(Map<String, String> map) {
      this.map = map;
    }

    @Override
    public Map<String, String> getEnv() {
      return map;
    }

    @Override
    public String getEnvironmentVar(String key) {
      return map.get(key);
    }
  }
}
