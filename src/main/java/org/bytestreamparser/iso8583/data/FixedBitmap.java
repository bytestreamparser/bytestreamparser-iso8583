package org.bytestreamparser.iso8583.data;

import static org.bytestreamparser.scalar.util.Preconditions.check;

import java.util.BitSet;
import java.util.stream.IntStream;

/**
 * FixedBitmap is a {@link Bitmap} backed by a {@link BitSet}. It has a fixed capacity and cannot be
 * extended.
 */
public class FixedBitmap implements Bitmap {
  private final int bytes;
  private final BitSet bitSet;

  /**
   * Creates a new FixedBitmap.
   *
   * @param bytes the number of bytes.
   */
  public FixedBitmap(int bytes) {
    Bitmap.check(bytes);
    this.bytes = bytes;
    this.bitSet = new BitSet(bytes * Byte.SIZE);
  }

  /**
   * Creates a new FixedBitmap.
   *
   * @param bytes the byte array representation of the bitmap.
   * @return the FixedBitmap.
   */
  public static FixedBitmap valueOf(byte[] bytes) {
    FixedBitmap bitmap = new FixedBitmap(bytes.length);
    BitSet.valueOf(bytes).stream()
        .forEach(bit -> bitmap.set(bit / Byte.SIZE * Byte.SIZE + Byte.SIZE - (bit % Byte.SIZE)));
    return bitmap;
  }

  /**
   * @return the total number of bits in the {@link Bitmap}.
   */
  @Override
  public int capacity() {
    return bytes * Byte.SIZE;
  }

  /**
   * Checks if the given bit is set.
   *
   * @param bit the bit to check. NOTE: The bit index starts from {@code 1}.
   * @return {@code true} if the bit is set, {@code false} otherwise.
   */
  @Override
  public boolean get(int bit) {
    checkBit(bit);
    return bitSet.get(bit - 1);
  }

  /**
   * Sets the given bit.
   *
   * @param bit the bit to set. NOTE: The bit index starts from {@code 1}.
   */
  @Override
  public void set(int bit) {
    checkBit(bit);
    bitSet.set(bit - 1);
  }

  /**
   * Clears the given bit.
   *
   * @param bit the bit to clear. NOTE: The bit index starts from {@code 1}.
   */
  @Override
  public void clear(int bit) {
    checkBit(bit);
    bitSet.clear(bit - 1);
  }

  /**
   * @return the number of bits set in the {@link Bitmap}.
   */
  @Override
  public int cardinality() {
    return bitSet.cardinality();
  }

  /**
   * @return a stream of the bits set in the {@link Bitmap}.
   */
  @Override
  public IntStream stream() {
    return bitSet.stream().map(bit -> bit + 1);
  }

  /**
   * Converts the {@link Bitmap} to a byte array.
   *
   * @return the byte array representation of the {@link Bitmap}.
   */
  @Override
  public byte[] toByteArray() {
    byte[] packed = new byte[bytes];
    bitSet.stream().forEach(bit -> packed[bit / Byte.SIZE] |= (byte) (1 << (7 - bit % Byte.SIZE)));
    return packed;
  }

  private void checkBit(int bit) {
    check(bit > 0, BIT_ERROR, capacity(), bit);
    check(bit <= capacity(), BIT_ERROR, capacity(), bit);
  }
}
