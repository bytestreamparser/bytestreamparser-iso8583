package org.bytestreamparser.iso8583.data;

import java.util.stream.IntStream;
import org.bytestreamparser.scalar.util.Preconditions;

/**
 * A bitmap is a data structure that represents a set of bits. It is used to indicate the presence
 * or absence of data elements in a message.
 */
public interface Bitmap {
  int MAXIMUM_BYTES = Integer.MAX_VALUE / Byte.SIZE;
  String BYTES_ERROR = "bytes should be greater than 0, but got [%d]";
  String MAXIMUM_CAPACITY_ERROR = "maximum capacity %d bytes exceeded: [%d]";
  String BIT_ERROR = "bit should be between 1 and %s, but got [%d]";

  /**
   * Checks if the given number of bytes is valid.
   *
   * @param bytes the number of bytes.
   */
  static void check(int bytes) {
    Preconditions.check(bytes > 0, BYTES_ERROR, bytes);
    Preconditions.check(bytes <= MAXIMUM_BYTES, MAXIMUM_CAPACITY_ERROR, MAXIMUM_BYTES, bytes);
  }

  /**
   * @return the total number of bits in the {@link Bitmap}.
   */
  int capacity();

  /**
   * Checks if the given bit is set.
   *
   * @param bit the bit to check. NOTE: The bit index starts from {@code 1}.
   * @return {@code true} if the bit is set, {@code false} otherwise.
   */
  boolean get(int bit);

  /**
   * Sets the given bit.
   *
   * @param bit the bit to set. NOTE: The bit index starts from {@code 1}.
   */
  void set(int bit);

  /**
   * Clears the given bit.
   *
   * @param bit the bit to clear. NOTE: The bit index starts from {@code 1}.
   */
  void clear(int bit);

  /**
   * Returns the number of bits set in the {@link Bitmap}.
   *
   * @return the number of bits set.
   */
  int cardinality();

  /**
   * Returns a stream of the bits set in the {@link Bitmap}.
   *
   * @return a stream of the bits set.
   */
  IntStream stream();

  /**
   * Converts the {@link Bitmap} to a byte array.
   *
   * @return the byte array representation of the {@link Bitmap}.
   */
  byte[] toByteArray();
}
