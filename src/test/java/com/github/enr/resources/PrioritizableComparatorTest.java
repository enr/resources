package com.github.enr.resources;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PrioritizableComparatorTest {

  private final PrioritizableComparator comparator = new PrioritizableComparator();

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

  @Test
  void compare_shouldHandleZeroPriority() {
    Prioritizable zeroPriority = createMockPrioritizable(0);
    Prioritizable positivePriority = createMockPrioritizable(5);

    int result = comparator.compare(zeroPriority, positivePriority);
    assertThat(result).as("compare zero priority vs positive priority").isPositive();
  }

  @Test
  void compare_shouldHandleNegativePriority() {
    Prioritizable negativePriority = createMockPrioritizable(-5);
    Prioritizable positivePriority = createMockPrioritizable(5);

    int result = comparator.compare(negativePriority, positivePriority);
    assertThat(result).as("compare negative priority vs positive priority").isPositive();
  }

  @Test
  void compare_shouldNotOverflow_withExtremeValues() {
    Prioritizable maxPriority = createMockPrioritizable(Integer.MAX_VALUE);
    Prioritizable minPriority = createMockPrioritizable(Integer.MIN_VALUE);

    // Integer.MAX_VALUE - Integer.MIN_VALUE overflows to negative; Integer.compare does not
    assertThat(comparator.compare(maxPriority, minPriority))
        .as("MAX_VALUE should sort before MIN_VALUE (result must be negative)").isNegative();
    assertThat(comparator.compare(minPriority, maxPriority))
        .as("MIN_VALUE should sort after MAX_VALUE (result must be positive)").isPositive();
  }

  private Prioritizable createMockPrioritizable(int priority) {
    return new Prioritizable() {
      @Override
      public int priority() {
        return priority;
      }
    };
  }
}
