package org.bytestreamparser.iso8583.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import org.bytestreamparser.api.parser.DataParser;
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
  void fixedLength(@Randomize byte[] value) throws IOException {
    DataParser<byte[]> parser = BinaryParsers.fixedLength("fixed", value.length);
    ByteArrayInputStream input = new ByteArrayInputStream(value);
    assertThat(parser.parse(input)).isEqualTo(value);
  }

  @Test
  void binaryLVar(@Randomize(length = 10) byte[] value) throws IOException {
    value[0] = 4;
    DataParser<byte[]> parser = BinaryParsers.binaryLVar("binaryLVar");
    ByteArrayInputStream input = new ByteArrayInputStream(value);

    assertThat(parser.parse(input)).isEqualTo(Arrays.copyOfRange(value, 1, 5));
    assertThat(input.available()).isEqualTo(5);

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    parser.pack(input.readAllBytes(), output);

    byte[] packed = output.toByteArray();
    assertThat(packed[0]).isEqualTo((byte) 5);
    assertThat(Arrays.copyOfRange(packed, 1, 6)).isEqualTo(Arrays.copyOfRange(value, 5, 10));
  }

  @Test
  void binaryLLVar(@Randomize(length = 10) byte[] value) throws IOException {
    value[0] = 0;
    value[1] = 3;
    DataParser<byte[]> parser = BinaryParsers.binaryLLVar("binaryLLVar");
    ByteArrayInputStream input = new ByteArrayInputStream(value);
    assertThat(parser.parse(input)).isEqualTo(Arrays.copyOfRange(value, 2, 5));
    assertThat(input.available()).isEqualTo(5);
  }

  @Test
  void textLLVar(
      @Randomize(intMin = 1, intMax = 10) int length, @Randomize(length = 10) byte[] value)
      throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    output.write(String.format("%02d", length).getBytes());
    output.write(value);
    DataParser<byte[]> parser = BinaryParsers.textLLVar("plainLLVar");
    ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
    assertThat(parser.parse(input)).isEqualTo(Arrays.copyOfRange(value, 0, length));
    assertThat(input.available()).isEqualTo(value.length - length);
  }

  @Test
  void textLLLVar(
      @Randomize(intMin = 1, intMax = 10) int length, @Randomize(length = 10) byte[] value)
      throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    output.write(String.format("%03d", length).getBytes());
    output.write(value);
    DataParser<byte[]> parser = BinaryParsers.textLLLVar("plainLLLVar");
    ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
    assertThat(parser.parse(input)).isEqualTo(Arrays.copyOfRange(value, 0, length));
    assertThat(input.available()).isEqualTo(value.length - length);
  }

  @Test
  void fixedBitmap(@Randomize byte[] value) throws IOException {
    DataParser<FixedBitmap> parser = BinaryParsers.fixedBitmap("fixedBitmap", value.length);
    ByteArrayInputStream input = new ByteArrayInputStream(value);
    FixedBitmap parsed = parser.parse(input);
    FixedBitmap expected = FixedBitmap.valueOf(value);
    assertThat(parsed.capacity()).isEqualTo(expected.capacity());
    for (int index = 1; index <= parsed.capacity(); index++) {
      assertThat(parsed.get(index)).isEqualTo(expected.get(index));
    }
  }

  @Test
  void extendableBitmap(@Randomize(length = 10) byte[] value) throws IOException {
    value[0] = (byte) (0b10000000 | value[0]);
    value[value.length / 2] = (byte) (0b01111111 & value[value.length / 2]);
    value[value.length / 2] = (byte) (0b00000001 | value[value.length / 2]);
    ByteArrayInputStream input = new ByteArrayInputStream(value);
    DataParser<ExtendableBitmap> parser =
        BinaryParsers.extendableBitmap("extendableBitmap", value.length / 2);
    ExtendableBitmap parsed = parser.parse(input);
    char[] chars = TestHelper.toBinaryString(value).toCharArray();
    for (int index = 0; index < chars.length; index++) {
      assertThat(parsed.get(index + 1)).isEqualTo(chars[index] == '1');
    }
  }
}
