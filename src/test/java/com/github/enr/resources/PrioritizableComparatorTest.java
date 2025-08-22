package com.github.enr.resources;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PrioritizableComparatorTest {

  private final PrioritizableComparator comparator = new PrioritizableComparator();

  @ParameterizedTest
  @CsvSource({"10, 5, -5", // o1.priority() > o2.priority() -> positive
      "5, 10, 5", // o1.priority() < o2.priority() -> negative
      "7, 7, 0" // o1.priority() == o2.priority() -> zero
  })
  void compare_shouldReturnCorrectOrder(int priority1, int priority2, int expectedResult) {
    Prioritizable o1 = createMockPrioritizable(priority1);
    Prioritizable o2 = createMockPrioritizable(priority2);

    int result = comparator.compare(o1, o2);
    assertThat(result).as("compare(%d, %d)".formatted(priority1, priority2)).isEqualTo(expectedResult);
  }

  @Test
  void compare_shouldReturnNegative_whenFirstHasHigherPriority() {
    Prioritizable highPriority = createMockPrioritizable(10);
    Prioritizable lowPriority = createMockPrioritizable(1);

    int result = comparator.compare(highPriority, lowPriority);
    assertThat(result).as("compare high priority vs low priority").isNegative();
  }

  @Test
  void compare_shouldReturnPositive_whenFirstHasLowerPriority() {
    Prioritizable lowPriority = createMockPrioritizable(1);
    Prioritizable highPriority = createMockPrioritizable(10);

    int result = comparator.compare(lowPriority, highPriority);
    assertThat(result).as("compare low priority vs high priority").isPositive();
  }

  @Test
  void compare_shouldReturnZero_whenPrioritiesAreEqual() {
    Prioritizable samePriority1 = createMockPrioritizable(5);
    Prioritizable samePriority2 = createMockPrioritizable(5);

    int result = comparator.compare(samePriority1, samePriority2);
    assertThat(result).as("compare equal priorities").isZero();
  }

  // @Test
  // void compare_shouldHandleZeroPriority() {
  // Prioritizable zeroPriority = createMockPrioritizable(0);
  // Prioritizable positivePriority = createMockPrioritizable(5);

  // int result = comparator.compare(zeroPriority, positivePriority);
  // assertThat(result).as("compare zero priority vs positive priority").isNegative();
  // }

  // @Test
  // void compare_shouldHandleNegativePriority() {
  // Prioritizable negativePriority = createMockPrioritizable(-5);
  // Prioritizable positivePriority = createMockPrioritizable(5);

  // int result = comparator.compare(negativePriority, positivePriority);
  // assertThat(result).as("compare negative priority vs positive priority").isNegative();
  // }

  private Prioritizable createMockPrioritizable(int priority) {
    return new Prioritizable() {
      @Override
      public int priority() {
        return priority;
      }
    };
  }
}
