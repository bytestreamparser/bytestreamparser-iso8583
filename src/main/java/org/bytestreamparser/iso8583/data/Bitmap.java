package org.bytestreamparser.iso8583.data;

import java.util.stream.IntStream;
import org.bytestreamparser.scalar.util.Preconditions;

public interface Bitmap {
  int MAXIMUM_BYTES = Integer.MAX_VALUE / Byte.SIZE;
  String BYTES_ERROR = "bytes should be greater than 0, but got [%d]";
  String MAXIMUM_CAPACITY_ERROR = "maximum capacity %d bytes exceeded: [%d]";
  String BIT_ERROR = "bit should be between 1 and %s, but got [%d]";

  static void check(int bytes) {
    Preconditions.check(bytes > 0, BYTES_ERROR, bytes);
    Preconditions.check(bytes <= MAXIMUM_BYTES, MAXIMUM_CAPACITY_ERROR, MAXIMUM_BYTES, bytes);
  }

  int capacity();

  boolean get(int bit);

  void set(int bit);

  void clear(int bit);

  int cardinality();

  IntStream stream();

  byte[] toByteArray();
}
