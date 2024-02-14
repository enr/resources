package com.github.enr.resources;

class Strings {

  private static boolean isEmpty(String str) {
    return str == null || str.isEmpty();
  }

  public static String removeStart(final String str, final String remove) {
    if (isEmpty(str) || isEmpty(remove)) {
      return str;
    }
    if (str.startsWith(remove)) {
      return str.substring(remove.length());
    }
    return str;
  }

}
