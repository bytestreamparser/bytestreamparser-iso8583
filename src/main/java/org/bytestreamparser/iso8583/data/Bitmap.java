package org.bytestreamparser.iso8583.data;

import static org.bytestreamparser.scalar.util.Preconditions.check;

import java.util.BitSet;
import java.util.stream.IntStream;

public class Bitmap {
  private static final String BIT_ERROR = "bit should be between 1 and %s, but got [%d]";
  private static final String BYTES_ERROR = "bytes should be greater than 0, but got [%d]";
  private static final String EXTENSIONS_ERROR =
      "extensions should be greater than or equal to 0, but got [%d]";
  private static final String MAXIMUM_CAPACITY_ERROR = "Maximum capacity exceeded";
  private static final String VALUE_OF_ERROR =
      "byte array length should be multiple of %s, but got [%d]";
  private final int bytes;
  private final int extensions;
  private final BitSet bitSet;

  public Bitmap(int bytes, int extensions) {
    check(bytes > 0, BYTES_ERROR, bytes);
    check(extensions >= 0, EXTENSIONS_ERROR, extensions);
    check(Integer.MAX_VALUE / Byte.SIZE / (1 + extensions) >= bytes, MAXIMUM_CAPACITY_ERROR);
    this.bytes = bytes;
    this.extensions = extensions;
    bitSet = new BitSet(bytes * Byte.SIZE * (1 + extensions));
  }

  public static Bitmap valueOf(int bytes, byte[] bits) {
    check(bits.length % bytes == 0, VALUE_OF_ERROR, bytes, bits.length);
    Bitmap bitmap = new Bitmap(bytes, bits.length / bytes - 1);
    BitSet.valueOf(bits).stream()
        .forEach(bit -> bitmap.set(bit / Byte.SIZE * Byte.SIZE + Byte.SIZE - (bit % Byte.SIZE)));
    return bitmap;
  }

  public int capacity() {
    return bytes * Byte.SIZE * (1 + extensions);
  }

  public boolean get(int bit) {
    checkBit(bit);
    return bitSet.get(bit - 1);
  }

  public void set(int bit) {
    checkBit(bit);
    bitSet.set(bit - 1);
    recalibrate();
  }

  public void clear(int bit) {
    checkBit(bit);
    bitSet.clear(bit - 1);
    recalibrate();
  }

  public int cardinality() {
    return bitSet.cardinality();
  }

  public IntStream stream() {
    return bitSet.stream().map(bit -> bit + 1);
  }

  public byte[] toByteArray() {
    byte[] packed = new byte[bytes * (1 + usedExtensions())];
    bitSet.stream().forEach(bit -> packed[bit / Byte.SIZE] |= (byte) (1 << (7 - bit % Byte.SIZE)));
    return packed;
  }

  private void checkBit(int bit) {
    check(bit > 0, BIT_ERROR, capacity(), bit);
    check(bit <= capacity(), BIT_ERROR, capacity(), bit);
  }

  private void recalibrate() {
    if (extensions > 0) {
      for (int index = extensions; index >= 0; index--) {
        int highestBit = bitSet.length();
        if (highestBit > bytes * Byte.SIZE * (1 + index) + 1) {
          bitSet.set(bytes * Byte.SIZE * index);
        } else {
          bitSet.clear(bytes * Byte.SIZE * index);
        }
      }
    }
  }

  private int usedExtensions() {
    for (int index = 0; index < extensions; index++) {
      if (bytes * Byte.SIZE * (index + 1) >= bitSet.length()) {
        return index;
      }
    }
    return extensions;
  }
}
