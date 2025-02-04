package org.bytestreamparser.iso8583.data;

import static org.bytestreamparser.scalar.util.Preconditions.check;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * ExtendableBitmap is a {@link Bitmap} backed by a lit of {@link FixedBitmap}s. It can be extended
 * by adding more {@link FixedBitmap} extensions.
 */
public class ExtendableBitmap implements Bitmap {
  private static final String EXTENSION_ERROR_MESSAGE =
      "extension capacity should be %s, but got [%s]";
  private static final FixedBitmap EMPTY = new FixedBitmap(1);
  private final int bytes;
  private final List<FixedBitmap> bitmaps;

  /**
   * Creates a new ExtendableBitmap.
   *
   * @param bytes the number of bytes per bitmap.
   */
  public ExtendableBitmap(int bytes) {
    Bitmap.check(bytes);
    this.bytes = bytes;
    this.bitmaps = new ArrayList<>();
  }

  private static FixedBitmap recalibrate(FixedBitmap next, FixedBitmap prev) {
    if (next.cardinality() > 0) {
      prev.set(1);
    } else {
      prev.clear(1);
    }
    return prev;
  }

  /**
   * Adds the given extensions to the {@link ExtendableBitmap}.
   *
   * @param extensions the extensions to add.
   * @return the {@link ExtendableBitmap} with the added extensions.
   */
  public ExtendableBitmap addExtensions(List<FixedBitmap> extensions) {
    for (FixedBitmap extension : extensions) {
      check(
          extension.capacity() == bytes * Byte.SIZE,
          EXTENSION_ERROR_MESSAGE,
          bytes * Byte.SIZE,
          extension.capacity());
    }
    bitmaps.addAll(extensions);
    recalibrate();
    return this;
  }

  /**
   * @return the total number of bits in the {@link Bitmap}.
   */
  @Override
  public int capacity() {
    return bitmaps.stream().mapToInt(FixedBitmap::capacity).sum();
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
    return bitmaps.get(getBitmapIndex(bit)).get(getBitIndex(bit));
  }

  /**
   * Sets the given bit.
   *
   * @param bit the bit to set. NOTE: The bit index starts from {@code 1}. The first bit of each
   *     {@link FixedBitmap} indicates if the next {@link FixedBitmap} is empty or not, and will be
   *     automatically recalibrated.
   */
  @Override
  public void set(int bit) {
    checkBit(bit);
    bitmaps.get(getBitmapIndex(bit)).set(getBitIndex(bit));
    recalibrate();
  }

  /**
   * Clears the given bit.
   *
   * @param bit the bit to clear. NOTE: The bit index starts from {@code 1}. The first bit of each
   *     {@link FixedBitmap} indicates if the next {@link FixedBitmap} is empty or not, and will be
   *     automatically recalibrated.
   */
  @Override
  public void clear(int bit) {
    checkBit(bit);
    bitmaps.get(getBitmapIndex(bit)).clear(getBitIndex(bit));
    recalibrate();
  }

  /**
   * @return the number of bits set in the {@link Bitmap}.
   */
  @Override
  public int cardinality() {
    return bitmaps.stream().mapToInt(FixedBitmap::cardinality).sum();
  }

  /**
   * @return a stream of the bits set in the {@link Bitmap}.
   */
  @Override
  public IntStream stream() {
    return IntStream.range(0, bitmaps.size())
        .mapToObj(index -> bitmaps.get(index).stream().map(bit -> index * bytes * Byte.SIZE + bit))
        .reduce(IntStream::concat)
        .orElseGet(IntStream::empty);
  }

  /**
   * Converts the {@link Bitmap} to a byte array.
   *
   * @return the byte array representation of the {@link Bitmap}. Trailing empty {@link
   *     FixedBitmap}s will be ignored.
   */
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
    FixedBitmap next = EMPTY;
    for (int index = bitmaps.size() - 1; index >= 0; index--) {
      next = recalibrate(next, bitmaps.get(index));
    }
  }

  private int getBitmapIndex(int bit) {
    return (bit - 1) / (bytes * Byte.SIZE);
  }

  private int getBitIndex(int bit) {
    return (bit - 1) % (bytes * Byte.SIZE) + 1;
  }
}
