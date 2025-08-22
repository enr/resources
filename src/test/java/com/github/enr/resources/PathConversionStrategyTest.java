package com.github.enr.resources;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class PathConversionStrategyTest {

  @ParameterizedTest
  @EnumSource(PathConversionStrategy.class)
  void enumValues_shouldBeAccessible(PathConversionStrategy strategy) {
    assertThat(strategy).as("PathConversionStrategy enum value").isNotNull();
  }

  @Test
  void enum_shouldHaveThreeValues() {
    PathConversionStrategy[] values = PathConversionStrategy.values();
    assertThat(values).as("PathConversionStrategy enum values").hasSize(3);
  }

  @Test
  void enum_shouldContainStrict() {
    assertThat(PathConversionStrategy.STRICT).as("STRICT enum value").isNotNull();
  }

  @Test
  void enum_shouldContainLenient() {
    assertThat(PathConversionStrategy.LENIENT).as("LENIENT enum value").isNotNull();
  }

  @Test
  void enum_shouldContainForceTemporary() {
    assertThat(PathConversionStrategy.FORCE_TEMPORARY).as("FORCE_TEMPORARY enum value").isNotNull();
  }

  @Test
  void valueOf_shouldReturnCorrectEnum() {
    PathConversionStrategy strict = PathConversionStrategy.valueOf("STRICT");
    assertThat(strict).as("valueOf('STRICT')").isEqualTo(PathConversionStrategy.STRICT);
  }

  @Test
  void enumValues_shouldBeDistinct() {
    PathConversionStrategy[] values = PathConversionStrategy.values();
    assertThat(values).as("PathConversionStrategy enum values should be distinct").doesNotHaveDuplicates();
  }

  @Test
  void enum_shouldBeComparable() {
    PathConversionStrategy strict = PathConversionStrategy.STRICT;
    PathConversionStrategy lenient = PathConversionStrategy.LENIENT;
    PathConversionStrategy forceTemporary = PathConversionStrategy.FORCE_TEMPORARY;

    assertThat(strict).as("STRICT should equal itself").isEqualTo(strict);
    assertThat(lenient).as("LENIENT should equal itself").isEqualTo(lenient);
    assertThat(forceTemporary).as("FORCE_TEMPORARY should equal itself").isEqualTo(forceTemporary);

    assertThat(strict).as("STRICT should not equal LENIENT").isNotEqualTo(lenient);
    assertThat(strict).as("STRICT should not equal FORCE_TEMPORARY").isNotEqualTo(forceTemporary);
    assertThat(lenient).as("LENIENT should not equal FORCE_TEMPORARY").isNotEqualTo(forceTemporary);
  }
}
