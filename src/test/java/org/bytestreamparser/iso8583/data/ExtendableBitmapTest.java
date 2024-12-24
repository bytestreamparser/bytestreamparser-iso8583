package org.bytestreamparser.iso8583.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.bytestreamparser.api.testing.extension.RandomParametersExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExtendableBitmapTest extends BitmapTestBase {

  @Override
  @BeforeEach
  void setUp(
      @RandomParametersExtension.Randomize(intMin = 2, intMax = 8) int bytes,
      @RandomParametersExtension.Randomize(intMin = 1, intMax = 3) int extensions) {
    super.setUp(bytes, extensions);
  }

  @Test
  void set_first_bit() {
    assertThat(bitmap.get(1)).isFalse();
    bitmap.set(1);
    assertThat(bitmap.get(1)).isFalse();

    bitmap.set(bitmap.capacity());
    assertThat(bitmap.get(1)).isTrue();
    assertThat(bitmap.get(bitmap.capacity())).isTrue();
  }

  @Test
  void clear_first_bit() {
    bitmap.set(bitmap.capacity());
    assertThat(bitmap.get(1)).isTrue();

    bitmap.clear(1);
    assertThat(bitmap.get(1)).isTrue();

    bitmap.clear(bitmap.capacity());
    assertThat(bitmap.get(1)).isFalse();
    assertThat(bitmap.get(bitmap.capacity())).isFalse();
  }

  @Test
  void to_byte_array_without_using_extensions() {
    byte[] expected = new byte[bytes];
    assertThat(bitmap.toByteArray()).isEqualTo(expected);
    bitmap.set(2);
    expected[0] = 0b01000000;
    assertThat(bitmap.toByteArray()).isEqualTo(expected);
  }

  @Test
  void to_byte_array_with_used_extensions() {
    bitmap.set(bitmap.capacity());
    byte[] expected = new byte[bytes * (1 + extensions)];
    expected[expected.length - 1] = 1;
    for (int index = 0; index < extensions; index++) {
      expected[bytes * index] = (byte) 0b10000000;
    }
    assertThat(bitmap.toByteArray()).isEqualTo(expected);
  }

  @Test
  void validates_byte_array() {
    assertThatThrownBy(() -> Bitmap.valueOf(2, new byte[3]))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("byte array length should be multiple of %d, but got [%d]", 2, 3);
  }

  @Test
  void value_of_with_used_extension() {
    Bitmap parsed = Bitmap.valueOf(1, new byte[] {(byte) 0b10000001, 0b00000001});

    assertThat(parsed.capacity()).isEqualTo(16);
    assertThat(parsed.cardinality()).isEqualTo(3);
    assertThat(parsed.get(1)).isTrue();
    assertThat(parsed.get(8)).isTrue();
    assertThat(parsed.get(16)).isTrue();
  }

  @Test
  void value_of_with_empty_extension() {
    Bitmap parsed = Bitmap.valueOf(1, new byte[] {(byte) 0b10000001, 0b00000000});

    assertThat(parsed.capacity()).isEqualTo(16);
    assertThat(parsed.cardinality()).isEqualTo(1);
    assertThat(parsed.get(1)).isFalse();
    assertThat(parsed.get(8)).isTrue();
  }

  @Override
  protected int validBit(int bit) {
    if (bit % Byte.SIZE == 1) {
      return bit + 1;
    } else {
      return bit;
    }
  }
}
