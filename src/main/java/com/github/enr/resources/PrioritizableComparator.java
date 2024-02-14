package com.github.enr.resources;

import java.util.Comparator;

class PrioritizableComparator implements Comparator<Prioritizable> {

  @Override
  public int compare(Prioritizable o1, Prioritizable o2) {
    return o2.priority() - o1.priority();
  }

}
