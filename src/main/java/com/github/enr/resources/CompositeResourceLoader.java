package com.github.enr.resources;

import java.util.Arrays;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

class CompositeResourceLoader implements ResourceLoader {

  private final SortedSet<PrioritizableResourceLocation> locations;

  CompositeResourceLoader(PrioritizableResourceLocation... locations) {
    SortedSet<PrioritizableResourceLocation> sorted = new TreeSet<>(new PrioritizableComparator());
    sorted.addAll(Arrays.asList(locations));
    this.locations = sorted;
  }

  @Override
  public Resource get(String uri) {
    String path = Objects.requireNonNull(uri);
    for (PrioritizableResourceLocation prl : locations) {
      ResourceLocation location = prl.location();
      if (location.supports(path)) {
        return location.get(path);
      }
    }
    return new UnreadableResource(path);
  }

  @Override
  public boolean supports(String uri) {
    if (uri == null) {
      return false;
    }
    for (PrioritizableResourceLocation prl : locations) {
      ResourceLocation location = prl.location();
      if (location.supports(uri)) {
        return true;
      }
    }
    return false;
  }

}
