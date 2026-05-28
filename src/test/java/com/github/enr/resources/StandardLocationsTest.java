package com.github.enr.resources;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StandardLocationsTest {

  @Test
  void classpath_shouldBeInstanceOfClasspathLocation() {
    assertThat(StandardLocations.CLASSPATH).isInstanceOf(ClasspathLocation.class);
  }

  @Test
  void withConventionalPriority_shouldReturnFourLocationsInPriorityOrder() {
    PrioritizableResourceLocation[] locations = StandardLocations.withConventionalPriority();

    assertThat(locations).hasSize(4);
    assertThat(locations[0].priority()).isEqualTo(90);
    assertThat(locations[1].priority()).isEqualTo(80);
    assertThat(locations[2].priority()).isEqualTo(10);
    assertThat(locations[3].priority()).isEqualTo(-100);

    assertThat(locations[0].location()).isInstanceOf(ClasspathLocation.class);
    assertThat(locations[1].location()).isInstanceOf(EnvironmentLocation.class);
    assertThat(locations[2].location()).isInstanceOf(UrlLocation.class);
    assertThat(locations[3].location()).isInstanceOf(FileSystemLocation.class);
  }
}
