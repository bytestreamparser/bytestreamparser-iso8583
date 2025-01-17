package org.bytestreamparser.iso8583.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.bytestreamparser.api.parser.DataParser;
import org.bytestreamparser.api.testing.data.TestData;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension.Randomize;
import org.bytestreamparser.iso8583.data.ExtendableBitmap;
import org.bytestreamparser.iso8583.data.FixedBitmap;
import org.bytestreamparser.iso8583.helper.TestHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(RandomParametersExtension.class)
class BinaryParsersTest {

  @Test
  void BIN(@Randomize byte[] value) throws IOException {
    DataParser<TestData, byte[]> parser = BinaryParsers.BIN("id", value.length);
    ByteArrayInputStream input = new ByteArrayInputStream(value);
    assertThat(parser.parse(input)).isEqualTo(value);
  }

  @Test
  void BMP(@Randomize byte[] value) throws IOException {
    DataParser<TestData, FixedBitmap> parser = BinaryParsers.BMP("bitmap", value.length);
    ByteArrayInputStream input = new ByteArrayInputStream(value);
    FixedBitmap parsed = parser.parse(input);
    FixedBitmap expected = FixedBitmap.valueOf(value);
    assertThat(parsed.capacity()).isEqualTo(expected.capacity());
    for (int index = 1; index <= parsed.capacity(); index++) {
      assertThat(parsed.get(index)).isEqualTo(expected.get(index));
    }
  }

  @Test
  void EBMP(@Randomize(length = 10) byte[] value) throws IOException {
    value[0] = (byte) (0b10000000 | value[0]);
    value[value.length / 2] = (byte) (0b01111111 & value[value.length / 2]);
    value[value.length / 2] = (byte) (0b00000001 | value[value.length / 2]);
    ByteArrayInputStream input = new ByteArrayInputStream(value);
    DataParser<TestData, ExtendableBitmap> parser = BinaryParsers.EBMP("bitmap", value.length / 2);
    ExtendableBitmap parsed = parser.parse(input);
    char[] chars = TestHelper.toBinaryString(value).toCharArray();
    for (int index = 0; index < chars.length; index++) {
      assertThat(parsed.get(index + 1)).isEqualTo(chars[index] == '1');
    }
  }
}
