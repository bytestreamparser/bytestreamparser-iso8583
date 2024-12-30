package org.bytestreamparser.iso8583.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.random.RandomGenerator;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension.Randomize;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(RandomParametersExtension.class)
abstract class BitmapTestBase<T extends Bitmap> {
  protected T bitmap;

  protected static String toBinaryString(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) {
      sb.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
    }
    return sb.toString();
  }

  @Test
  void validates_bytes(@Randomize(intMax = 1) int negative) {
    assertThatThrownBy(() -> createBitmap(negative))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("bytes should be greater than 0, but got [%d]", negative);
  }

  @Test
  void validates_maximum_capacity() {
    assertThatThrownBy(() -> createBitmap(Integer.MAX_VALUE))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("maximum capacity 268435455 bytes exceeded: [%d]", Integer.MAX_VALUE);
  }

  @Test
  void capacity() {
    assertThat(bitmap.capacity()).isEqualTo(expectedCapacity());
  }

  @Test
  void get(@Randomize RandomGenerator generator) {
    assertThat(bitmap.get(generator.nextInt(1, bitmap.capacity()))).isFalse();
  }

  @Test
  void get_out_of_bounds(@Randomize RandomGenerator generator) {
    int negative = generator.nextInt(Integer.MIN_VALUE, 1);
    assertThatThrownBy(() -> bitmap.get(negative))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("bit should be between 1 and %d, but got [%d]", bitmap.capacity(), negative);

    int overCapacity = generator.nextInt(bitmap.capacity() + 1, Integer.MAX_VALUE);
    assertThatThrownBy(() -> bitmap.get(overCapacity))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "bit should be between 1 and %d, but got [%d]", bitmap.capacity(), overCapacity);
  }

  @Test
  void set(@Randomize RandomGenerator generator) {
    int bit = randomDataBit(generator, bitmap);
    assertThat(bitmap.get(bit)).isFalse();
    bitmap.set(bit);
    assertThat(bitmap.get(bit)).isTrue();
  }

  @Test
  void set_out_of_bounds(@Randomize RandomGenerator generator) {
    int negative = generator.nextInt(Integer.MIN_VALUE, 1);
    assertThatThrownBy(() -> bitmap.set(negative))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("bit should be between 1 and %d, but got [%d]", bitmap.capacity(), negative);

    int overCapacity = generator.nextInt(bitmap.capacity() + 1, Integer.MAX_VALUE);
    assertThatThrownBy(() -> bitmap.set(overCapacity))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "bit should be between 1 and %d, but got [%d]", bitmap.capacity(), overCapacity);
  }

  @Test
  void clear(@Randomize RandomGenerator generator) {
    int bit = randomDataBit(generator, bitmap);

    assertThat(bitmap.get(bit)).isFalse();
    bitmap.clear(bit);
    assertThat(bitmap.get(bit)).isFalse();

    bitmap.set(bit);
    assertThat(bitmap.get(bit)).isTrue();
    bitmap.clear(bit);
    assertThat(bitmap.get(bit)).isFalse();
  }

  @Test
  void clear_out_of_bounds(@Randomize RandomGenerator generator) {
    int negative = generator.nextInt(Integer.MIN_VALUE, 1);
    assertThatThrownBy(() -> bitmap.clear(negative))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("bit should be between 1 and %d, but got [%d]", bitmap.capacity(), negative);

    int overCapacity = generator.nextInt(bitmap.capacity() + 1, Integer.MAX_VALUE);
    assertThatThrownBy(() -> bitmap.clear(overCapacity))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "bit should be between 1 and %d, but got [%d]", bitmap.capacity(), overCapacity);
  }

  @Test
  void cardinality(@Randomize RandomGenerator generator) {
    int bit = randomDataBit(generator, bitmap);

    assertThat(bitmap.cardinality()).isZero();
    bitmap.set(bit);
    assertThat(bitmap.cardinality()).isPositive();

    bitmap.clear(bit);
    assertThat(bitmap.cardinality()).isZero();
  }

  @Test
  void int_stream(@Randomize RandomGenerator generator) {
    int bit = randomDataBit(generator, bitmap);
    assertThat(bitmap.stream().boxed().toList()).isEmpty();

    bitmap.set(bit);
    assertThat(bitmap.stream().boxed().toList()).contains(bit);
  }

  @Test
  abstract void to_byte_array(@Randomize RandomGenerator generator);

  protected abstract void createBitmap(int bytes);

  protected abstract int expectedCapacity();

  protected abstract int randomDataBit(RandomGenerator generator, Bitmap bitmap);
}
