package org.bytestreamparser.iso8583.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Random;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension.Randomize;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(RandomParametersExtension.class)
abstract class BitmapTestBase {
  protected static Random RANDOM = new Random();
  protected Bitmap bitmap;
  protected int bytes;
  protected int extensions;

  void setUp(int bytes, int extensions) {
    this.bytes = bytes;
    this.extensions = extensions;
    bitmap = new Bitmap(bytes, extensions);
  }

  @Test
  void validates_bytes(@Randomize(intMax = 1) int negative) {
    assertThatThrownBy(() -> new Bitmap(negative, extensions))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("bytes should be greater than 0, but got [%d]", negative);
  }

  @Test
  void validates_extensions(@Randomize(intMax = 0) int negative) {
    assertThatThrownBy(() -> new Bitmap(bytes, negative))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("extensions should be greater than or equal to 0, but got [%d]", negative);
  }

  @Test
  void validates_maximum_capacity() {
    assertThatThrownBy(() -> new Bitmap(Integer.MAX_VALUE, 2))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Maximum capacity exceeded");
  }

  @Test
  void capacity() {
    assertThat(bitmap.capacity()).isEqualTo(bytes * Byte.SIZE * (1 + extensions));
  }

  @Test
  void get() {
    assertThat(bitmap.get(RANDOM.nextInt(1, bitmap.capacity()))).isFalse();
  }

  @Test
  void get_out_of_bounds() {
    int negative = RANDOM.nextInt(Integer.MIN_VALUE, 1);
    assertThatThrownBy(() -> bitmap.get(negative))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("bit should be between 1 and %d, but got [%d]", bitmap.capacity(), negative);

    int overCapacity = RANDOM.nextInt(bitmap.capacity() + 1, Integer.MAX_VALUE);
    assertThatThrownBy(() -> bitmap.get(overCapacity))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "bit should be between 1 and %d, but got [%d]", bitmap.capacity(), overCapacity);
  }

  @Test
  void set() {
    int bit = validBit(RANDOM.nextInt(1, bitmap.capacity()));
    assertThat(bitmap.get(bit)).isFalse();
    bitmap.set(bit);
    assertThat(bitmap.get(bit)).isTrue();
  }

  @Test
  void set_out_of_bounds() {
    int negative = RANDOM.nextInt(Integer.MIN_VALUE, 1);
    assertThatThrownBy(() -> bitmap.set(negative))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("bit should be between 1 and %d, but got [%d]", bitmap.capacity(), negative);

    int overCapacity = RANDOM.nextInt(bitmap.capacity() + 1, Integer.MAX_VALUE);
    assertThatThrownBy(() -> bitmap.set(overCapacity))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "bit should be between 1 and %d, but got [%d]", bitmap.capacity(), overCapacity);
  }

  @Test
  void clear() {
    int bit = validBit(RANDOM.nextInt(1, bitmap.capacity()));

    assertThat(bitmap.get(bit)).isFalse();
    bitmap.clear(bit);
    assertThat(bitmap.get(bit)).isFalse();

    bitmap.set(bit);
    assertThat(bitmap.get(bit)).isTrue();
    bitmap.clear(bit);
    assertThat(bitmap.get(bit)).isFalse();
  }

  @Test
  void clear_out_of_bounds() {
    int negative = RANDOM.nextInt(Integer.MIN_VALUE, 1);
    assertThatThrownBy(() -> bitmap.clear(negative))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("bit should be between 1 and %d, but got [%d]", bitmap.capacity(), negative);

    int overCapacity = RANDOM.nextInt(bitmap.capacity() + 1, Integer.MAX_VALUE);
    assertThatThrownBy(() -> bitmap.clear(overCapacity))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "bit should be between 1 and %d, but got [%d]", bitmap.capacity(), overCapacity);
  }

  @Test
  abstract void set_first_bit();

  @Test
  abstract void clear_first_bit();

  @Test
  void cardinality() {
    int bit = validBit(RANDOM.nextInt(1, bitmap.capacity()));

    assertThat(bitmap.cardinality()).isEqualTo(0);
    bitmap.set(bit);
    assertThat(bitmap.cardinality()).isGreaterThan(0);

    bitmap.clear(bit);
    assertThat(bitmap.cardinality()).isEqualTo(0);
  }

  @Test
  void int_stream() {
    assertThat(bitmap.stream().boxed().toList()).isEqualTo(List.of());

    bitmap.set(2);
    assertThat(bitmap.stream().boxed().toList()).isEqualTo(List.of(2));
  }

  protected abstract int validBit(int bit);
}
