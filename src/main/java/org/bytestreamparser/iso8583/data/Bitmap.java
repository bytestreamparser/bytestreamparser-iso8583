package org.bytestreamparser.iso8583.data;

import java.util.stream.IntStream;

public interface Bitmap {
  String BIT_ERROR = "bit should be between 1 and %s, but got [%d]";

  int capacity();

  boolean get(int bit);

  void set(int bit);

  void clear(int bit);

  int cardinality();

  IntStream stream();

  byte[] toByteArray();
}
