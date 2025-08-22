package com.github.enr.resources;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StandardLocationsTest {

  @Test
  void classpath_shouldBeInstanceOfClasspathLocation() {
    ResourceLocation classpath = StandardLocations.CLASSPATH;
    assertThat(classpath).as("CLASSPATH should be instance of ClasspathLocation")
        .isInstanceOf(ClasspathLocation.class);
  }

  @Test
  void withConventionalPriority_shouldReturnArrayWithFourElements() {
    PrioritizableResourceLocation[] locations = StandardLocations.withConventionalPriority();
    assertThat(locations).as("withConventionalPriority() should return array with 4 elements").hasSize(4);
  }

  @Test
  void withConventionalPriority_shouldReturnLocationsInPriorityOrder() {
    PrioritizableResourceLocation[] locations = StandardLocations.withConventionalPriority();
    
    // Should be sorted by priority (highest first due to PrioritizableComparator)
    assertThat(locations[0].priority()).as("First location priority").isEqualTo(90);
    assertThat(locations[1].priority()).as("Second location priority").isEqualTo(80);
    assertThat(locations[2].priority()).as("Third location priority").isEqualTo(10);
    assertThat(locations[3].priority()).as("Fourth location priority").isEqualTo(-100);
  }

  @Test
  void withConventionalPriority_shouldReturnCorrectLocationTypes() {
    PrioritizableResourceLocation[] locations = StandardLocations.withConventionalPriority();
    
    assertThat(locations[0].location()).as("First location should be ClasspathLocation")
        .isInstanceOf(ClasspathLocation.class);
    assertThat(locations[1].location()).as("Second location should be EnvironmentLocation")
        .isInstanceOf(EnvironmentLocation.class);
    assertThat(locations[2].location()).as("Third location should be UrlLocation")
        .isInstanceOf(UrlLocation.class);
    assertThat(locations[3].location()).as("Fourth location should be FileSystemLocation")
        .isInstanceOf(FileSystemLocation.class);
  }

  @Test
  void withConventionalPriority_shouldReturnNewArrayEachTime() {
    PrioritizableResourceLocation[] locations1 = StandardLocations.withConventionalPriority();
    PrioritizableResourceLocation[] locations2 = StandardLocations.withConventionalPriority();
    
    assertThat(locations1).as("First call should return new array").isNotSameAs(locations2);
  }

  @Test
  void withConventionalPriority_shouldReturnLocationsWithCorrectPriorities() {
    PrioritizableResourceLocation[] locations = StandardLocations.withConventionalPriority();
    
    // Verify priorities match expected values
    assertThat(locations).as("All locations should have correct priorities")
        .anySatisfy(location -> assertThat(location.priority()).as("ClasspathLocation priority").isEqualTo(90))
        .anySatisfy(location -> assertThat(location.priority()).as("EnvironmentLocation priority").isEqualTo(80))
        .anySatisfy(location -> assertThat(location.priority()).as("UrlLocation priority").isEqualTo(10))
        .anySatisfy(location -> assertThat(location.priority()).as("FileSystemLocation priority").isEqualTo(-100));
  }

  @Test
  void withConventionalPriority_shouldReturnLocationsThatAreNotNull() {
    PrioritizableResourceLocation[] locations = StandardLocations.withConventionalPriority();
    
    for (int i = 0; i < locations.length; i++) {
      assertThat(locations[i]).as("Location at index " + i).isNotNull();
      assertThat(locations[i].location()).as("Location.location() at index " + i).isNotNull();
    }
  }
}
