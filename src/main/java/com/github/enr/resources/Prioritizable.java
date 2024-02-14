package com.github.enr.resources;

public interface Prioritizable {

  /*
   * Highest priority is evaluated first and so on.
   */
  int priority();
}
