package org.bytestreamparser.iso8583.data;

import static org.bytestreamparser.scalar.util.Preconditions.check;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class ExtendableBitmap implements Bitmap {
  public static final String EXTENSION_ERROR_MESSAGE =
      "extension capacity should be %s, but got [%s]";
  private final int bytes;
  private final List<FixedBitmap> bitmaps;

  public ExtendableBitmap(int bytes) {
    this.bytes = bytes;
    this.bitmaps = new ArrayList<>();
    this.bitmaps.add(new FixedBitmap(bytes));
  }

  public ExtendableBitmap addExtension(FixedBitmap bitmap) {
    check(
        bitmap.capacity() == bytes * Byte.SIZE,
        EXTENSION_ERROR_MESSAGE,
        bytes * Byte.SIZE,
        bitmap.capacity());
    bitmaps.add(bitmap);
    return this;
  }

  public int capacity() {
    return bitmaps.stream().mapToInt(FixedBitmap::capacity).sum();
  }

  public boolean get(int bit) {
    checkBit(bit);
    return bitmaps.get(getBitmapIndex(bit)).get(getBitIndex(bit));
  }

  public void set(int bit) {
    checkBit(bit);
    bitmaps.get(getBitmapIndex(bit)).set(getBitIndex(bit));
    recalibrate();
  }

  public void clear(int bit) {
    checkBit(bit);
    bitmaps.get(getBitmapIndex(bit)).clear(getBitIndex(bit));
    recalibrate();
  }

  public int cardinality() {
    return bitmaps.stream().mapToInt(FixedBitmap::cardinality).sum();
  }

  public IntStream stream() {
    return IntStream.range(0, bitmaps.size())
        .mapToObj(index -> bitmaps.get(index).stream().map(bit -> index * bytes * Byte.SIZE + bit))
        .reduce(IntStream::concat)
        .orElseGet(IntStream::empty);
  }

  public byte[] toByteArray() {
    byte[] byteArray = new byte[bytes * bitmaps.size()];
    int index = 0;
    do {
      System.arraycopy(bitmaps.get(index).toByteArray(), 0, byteArray, index * bytes, bytes);
      index++;
    } while (index < bitmaps.size() && bitmaps.get(index).cardinality() > 0);
    return Arrays.copyOfRange(byteArray, 0, index * bytes);
  }

  private void checkBit(int bit) {
    check(bit > 0, BIT_ERROR, capacity(), bit);
    check(bit <= capacity(), BIT_ERROR, capacity(), bit);
  }

  private void recalibrate() {
    for (int index = bitmaps.size() - 1; index > 0; index--) {
      if (bitmaps.get(index).cardinality() > 0) {
        bitmaps.get(index - 1).set(1);
      } else {
        bitmaps.get(index - 1).clear(1);
      }
    }
  }

  private int getBitmapIndex(int bit) {
    return (bit - 1) / (bytes * Byte.SIZE);
  }

  private int getBitIndex(int bit) {
    return (bit - 1) % (bytes * Byte.SIZE) + 1;
  }
}
