package com.github.enr.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

class CompositeResourceLoader implements ResourceLoader {

  private final List<PrioritizableResourceLocation> locations;

  CompositeResourceLoader(PrioritizableResourceLocation... locations) {
    // stable sort: locations with the same priority keep their declaration order
    List<PrioritizableResourceLocation> sorted = new ArrayList<>(Arrays.asList(locations));
    sorted.sort(new PrioritizableComparator());
    this.locations = List.copyOf(sorted);
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
