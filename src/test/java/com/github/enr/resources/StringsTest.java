package com.github.enr.resources;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class StringsTest {

  @ParameterizedTest
  @CsvSource({"test, te, st", "hello, he, llo", "world, wo, rld", "abc, ab, c", "xyz, x, yz"})
  void removeStart_shouldRemovePrefix_whenStringStartsWithRemove(String input, String remove, String expected) {
    String result = Strings.removeStart(input, remove);
    assertThat(result).as("removeStart('%s', '%s')").isEqualTo(expected);
  }

  @ParameterizedTest
  @CsvSource({"test, hello, test", "world, abc, world", "xyz, def, xyz"})
  void removeStart_shouldReturnOriginalString_whenStringDoesNotStartWithRemove(String input, String remove,
      String expected) {
    String result = Strings.removeStart(input, remove);
    assertThat(result).as("removeStart('%s', '%s') should return original string").isEqualTo(expected);
  }

  @ParameterizedTest
  @NullAndEmptySource
  void removeStart_shouldReturnOriginalString_whenRemoveIsNullOrEmpty(String remove) {
    String input = "test";
    String result = Strings.removeStart(input, remove);
    assertThat(result).as("removeStart with null/empty remove").isEqualTo(input);
  }

  @ParameterizedTest
  @NullAndEmptySource
  void removeStart_shouldReturnOriginalString_whenInputIsNullOrEmpty(String input) {
    String remove = "test";
    String result = Strings.removeStart(input, remove);
    assertThat(result).as("removeStart with null/empty input").isEqualTo(input);
  }

  @ParameterizedTest
  @CsvSource({"test, test, ''", "hello, hello, ''", "world, world, ''"})
  void removeStart_shouldReturnEmptyString_whenRemoveEqualsInput(String input, String remove, String expected) {
    String result = Strings.removeStart(input, remove);
    assertThat(result).as("removeStart when remove equals input").isEqualTo(expected);
  }

  @ParameterizedTest
  @CsvSource({"test, t, est", "hello, h, ello", "world, w, orld"})
  void removeStart_shouldRemoveSingleCharacter(String input, String remove, String expected) {
    String result = Strings.removeStart(input, remove);
    assertThat(result).as("removeStart with single character").isEqualTo(expected);
  }
}
