package org.bytestreamparser.iso8583.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.random.RandomGenerator;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension.Randomize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExtendableBitmapTest extends BitmapTestBase<ExtendableBitmap> {

  private int bytes;
  private int extensions;

  @BeforeEach
  void setUp(
      @Randomize(intMin = 1, intMax = 9) int bytes,
      @Randomize(intMin = 1, intMax = 4) int extensions) {
    this.bytes = bytes;
    this.extensions = extensions;
    this.bitmap = new ExtendableBitmap(bytes);
    for (int index = 0; index < extensions; index++) {
      bitmap.addExtension(new FixedBitmap(bytes));
    }
  }

  @Test
  void add_extension_with_invalid_capacity() {
    FixedBitmap invalidBitmap = new FixedBitmap(bytes + 1);
    assertThatThrownBy(() -> bitmap.addExtension(invalidBitmap))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "extension capacity should be %s, but got [%s]",
            bytes * Byte.SIZE, (bytes + 1) * Byte.SIZE);
  }

  @Test
  void set_extension_indicator(@Randomize RandomGenerator generator) {
    int extensionBit = randomExtensionBit(generator, bitmap);
    assertThat(bitmap.get(extensionBit)).isFalse();
    bitmap.set(extensionBit);
    assertThat(bitmap.get(extensionBit)).isFalse();

    int dataBit = extensionBit + bytes * Byte.SIZE + 1;
    bitmap.set(dataBit);
    assertThat(bitmap.get(extensionBit)).isTrue();
  }

  @Test
  void clear_extension_bit(@Randomize RandomGenerator generator) {
    int extensionBit = randomExtensionBit(generator, bitmap);

    int dataBit = extensionBit + bytes * Byte.SIZE + 1;
    bitmap.set(dataBit);
    assertThat(bitmap.get(extensionBit)).isTrue();

    bitmap.clear(extensionBit);
    assertThat(bitmap.get(extensionBit)).isTrue();

    bitmap.clear(dataBit);
    assertThat(bitmap.get(dataBit)).isFalse();
    assertThat(bitmap.get(extensionBit)).isFalse();
  }

  @Override
  @Test
  void to_byte_array(@Randomize RandomGenerator generator) {
    int bit = generator.nextInt(1, bitmap.capacity());
    bitmap.set(bit);
    String binaryString = toBinaryString(bitmap.toByteArray());
    assertThat(binaryString.charAt(bit - 1)).isEqualTo('1');
  }

  @Test
  void to_byte_array_without_using_extensions(@Randomize RandomGenerator generator) {
    assertThat(bitmap.toByteArray()).isEqualTo(new byte[bytes]);

    int baseDataBit = generator.nextInt(1, bytes * Byte.SIZE);
    bitmap.set(baseDataBit);
    String binaryString = toBinaryString(bitmap.toByteArray());
    assertThat(binaryString.charAt(baseDataBit - 1)).isEqualTo('1');
  }

  @Test
  void to_byte_array_with_used_extensions(@Randomize RandomGenerator generator) {
    int extensionBit = randomExtensionBit(generator, bitmap);
    int dataBit = extensionBit + bytes * Byte.SIZE + 1;

    bitmap.set(dataBit);
    String binaryString = toBinaryString(bitmap.toByteArray());

    assertThat(binaryString.charAt(extensionBit - 1)).isEqualTo('1');
    assertThat(binaryString.charAt(dataBit - 1)).isEqualTo('1');
  }

  @Override
  protected void createBitmap(int bytes) {
    new ExtendableBitmap(bytes);
  }

  @Override
  protected int expectedCapacity() {
    return bytes * Byte.SIZE * (1 + extensions);
  }

  @Override
  protected int randomDataBit(RandomGenerator generator, Bitmap bitmap) {
    return generator
        .ints(1, bitmap.capacity() + 1)
        .filter(b -> b % (bytes * Byte.SIZE) != 1)
        .findFirst()
        .orElseThrow();
  }

  private int randomExtensionBit(RandomGenerator generator, Bitmap bitmap) {
    return generator
        .ints(1, bitmap.capacity() + 1)
        .filter(b -> b % (bytes * Byte.SIZE) == 1)
        .filter(b -> b < (extensions * bytes * Byte.SIZE))
        .findFirst()
        .orElseThrow();
  }
}
