package org.bytestreamparser.iso8583.data;

import static org.bytestreamparser.scalar.util.Preconditions.check;

import java.util.BitSet;
import java.util.stream.IntStream;

public class FixedBitmap implements Bitmap {
  private static final int MAXIMUM_BYTES = Integer.MAX_VALUE / Byte.SIZE;
  private static final String BYTES_ERROR = "bytes should be greater than 0, but got [%d]";
  private static final String MAXIMUM_CAPACITY_ERROR = "maximum capacity %d bytes exceeded: [%d]";
  private final int bytes;
  private final BitSet bitSet;

  public FixedBitmap(int bytes) {
    check(bytes > 0, BYTES_ERROR, bytes);
    check(bytes <= MAXIMUM_BYTES, MAXIMUM_CAPACITY_ERROR, MAXIMUM_BYTES, bytes);
    this.bytes = bytes;
    this.bitSet = new BitSet(bytes * Byte.SIZE);
  }

  public static FixedBitmap valueOf(byte[] bytes) {
    FixedBitmap bitmap = new FixedBitmap(bytes.length);
    BitSet.valueOf(bytes).stream()
        .forEach(bit -> bitmap.set(bit / Byte.SIZE * Byte.SIZE + Byte.SIZE - (bit % Byte.SIZE)));
    return bitmap;
  }

  public int capacity() {
    return bytes * Byte.SIZE;
  }

  public boolean get(int bit) {
    checkBit(bit);
    return bitSet.get(bit - 1);
  }

  public void set(int bit) {
    checkBit(bit);
    bitSet.set(bit - 1);
  }

  public void clear(int bit) {
    checkBit(bit);
    bitSet.clear(bit - 1);
  }

  public int cardinality() {
    return bitSet.cardinality();
  }

  public IntStream stream() {
    return bitSet.stream().map(bit -> bit + 1);
  }

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
