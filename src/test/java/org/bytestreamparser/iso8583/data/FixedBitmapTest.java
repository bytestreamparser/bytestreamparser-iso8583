package org.bytestreamparser.iso8583.data;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.random.RandomGenerator;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension.Randomize;
import org.bytestreamparser.iso8583.helper.TestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FixedBitmapTest extends BitmapTestBase<FixedBitmap> {
  private int bytes;

  @BeforeEach
  void setUp(@Randomize(intMin = 1, intMax = 8) int bytes) {
    this.bytes = bytes;
    bitmap = new FixedBitmap(bytes);
  }

  @Test
  void value_of() {
    FixedBitmap bitmap = FixedBitmap.valueOf(new byte[] {0b01111111});
    assertThat(bitmap.capacity()).isEqualTo(8);
    assertThat(bitmap.cardinality()).isEqualTo(7);
    assertThat(bitmap.get(1)).isFalse();
  }

  @Override
  @Test
  void to_byte_array(@Randomize RandomGenerator generator) {
    int bit = generator.nextInt(1, bitmap.capacity());
    bitmap.set(bit);
    String binaryString = TestHelper.toBinaryString(bitmap.toByteArray());
    assertThat(binaryString.charAt(bit - 1)).isEqualTo('1');
  }

  @Override
  protected void createBitmap(int bytes) {
    new FixedBitmap(bytes);
  }

  @Override
  protected int expectedCapacity() {
    return this.bytes * Byte.SIZE;
  }

  @Override
  protected int randomDataBit(RandomGenerator generator, Bitmap bitmap) {
    return generator.nextInt(1, bitmap.capacity() + 1);
  }
}
