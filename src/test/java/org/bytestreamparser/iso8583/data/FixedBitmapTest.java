package org.bytestreamparser.iso8583.data;

import static org.assertj.core.api.Assertions.assertThat;

import org.bytestreamparser.api.testing.extension.RandomParametersExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FixedBitmapTest extends BitmapTestBase {
  @BeforeEach
  void setUp(@RandomParametersExtension.Randomize(intMin = 1, intMax = 8) int bytes) {
    super.setUp(bytes, 0);
  }

  @Test
  void value_of() {
    Bitmap parsed = Bitmap.valueOf(1, new byte[] {0b01111111});

    assertThat(parsed.capacity()).isEqualTo(8);
    assertThat(parsed.cardinality()).isEqualTo(7);
    assertThat(parsed.get(1)).isFalse();
  }

  @Test
  void set_first_bit() {
    assertThat(bitmap.get(1)).isFalse();
    bitmap.set(1);
    assertThat(bitmap.get(1)).isTrue();
  }

  @Override
  @Test
  void clear_first_bit() {
    assertThat(bitmap.get(1)).isFalse();
    bitmap.clear(1);
    assertThat(bitmap.get(1)).isFalse();

    bitmap.set(1);
    assertThat(bitmap.get(1)).isTrue();
    bitmap.clear(1);
    assertThat(bitmap.get(1)).isFalse();
  }

  @Override
  protected int validBit(int bit) {
    return bit;
  }
}
