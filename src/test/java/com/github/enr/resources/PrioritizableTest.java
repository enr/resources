package com.github.enr.resources;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PrioritizableTest {

  @ParameterizedTest
  @ValueSource(ints = {-100, -10, 0, 10, 100})
  void prioritizable_shouldReturnCorrectPriority(int priority) {
    TestPrioritizable prioritizable = new TestPrioritizable(priority);

    int result = prioritizable.priority();

    assertThat(result).as("priority() should return correct value").isEqualTo(priority);
  }

  @Test
  void prioritizable_shouldReturnZeroPriority() {
    TestPrioritizable prioritizable = new TestPrioritizable(0);

    int result = prioritizable.priority();

    assertThat(result).as("priority() should return zero").isZero();
  }

  @Test
  void prioritizable_shouldReturnNegativePriority() {
    TestPrioritizable prioritizable = new TestPrioritizable(-5);

    int result = prioritizable.priority();

    assertThat(result).as("priority() should return negative value").isNegative();
  }

  @Test
  void prioritizable_shouldReturnPositivePriority() {
    TestPrioritizable prioritizable = new TestPrioritizable(5);

    int result = prioritizable.priority();

    assertThat(result).as("priority() should return positive value").isPositive();
  }

  @Test
  void prioritizable_shouldReturnMaxIntegerPriority() {
    TestPrioritizable prioritizable = new TestPrioritizable(Integer.MAX_VALUE);

    int result = prioritizable.priority();

    assertThat(result).as("priority() should return max integer value").isEqualTo(Integer.MAX_VALUE);
  }

  @Test
  void prioritizable_shouldReturnMinIntegerPriority() {
    TestPrioritizable prioritizable = new TestPrioritizable(Integer.MIN_VALUE);

    int result = prioritizable.priority();

    assertThat(result).as("priority() should return min integer value").isEqualTo(Integer.MIN_VALUE);
  }

  // Test helper class
  private static class TestPrioritizable implements Prioritizable {
    private final int priority;

    TestPrioritizable(int priority) {
      this.priority = priority;
    }

    @Override
    public int priority() {
      return priority;
    }
  }
}
