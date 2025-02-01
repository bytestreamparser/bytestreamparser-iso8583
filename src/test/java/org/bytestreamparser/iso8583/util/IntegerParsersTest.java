package org.bytestreamparser.iso8583.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HexFormat;
import org.bytestreamparser.api.parser.DataParser;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension.Randomize;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@ExtendWith(RandomParametersExtension.class)
class IntegerParsersTest {

  @Test
  void ubyte(@Randomize byte[] value) throws IOException {
    DataParser<Integer> parser = IntegerParsers.ubyte("ubyte");
    ByteArrayInputStream input = new ByteArrayInputStream(value);
    assertThat(parser.parse(input)).isEqualTo(value[0] & 0xFF);
  }

  @Test
  void ushort(@Randomize byte[] value) throws IOException {
    DataParser<Integer> parser = IntegerParsers.ushort("ushort");
    ByteArrayInputStream input = new ByteArrayInputStream(value);
    int expected = ((int) value[0] & 0xFF) << 8 | value[1] & 0xFF;
    assertThat(parser.parse(input)).isEqualTo(expected);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"US-ASCII", "IBM1047", "ISO-8859-1", "UTF-8", "UTF-16", "UTF-16BE", "UTF-16LE"})
  void text(
      String charsetName,
      @Randomize Integer value,
      @Randomize(intMin = Character.MIN_RADIX, intMax = Character.MAX_RADIX + 1) int radix)
      throws IOException {
    Charset charset = Charset.forName(charsetName);
    String string = Integer.toString(value, radix);
    InputStream input = new ByteArrayInputStream(string.getBytes(charset));
    DataParser<Integer> parser = IntegerParsers.text(charsetName, string.length(), radix, charset);
    assertThat(parser.parse(input)).isEqualTo(value);
  }

  @Test
  void text_with_default_radix(@Randomize Integer value) throws IOException {
    Charset charset = Charset.defaultCharset();
    String string = Integer.toString(value);
    InputStream input = new ByteArrayInputStream(string.getBytes(charset));
    DataParser<Integer> parser = IntegerParsers.text(charset.name(), string.length());
    assertThat(parser.parse(input)).isEqualTo(value);
  }

  @Test
  void bcd(@Randomize(intMin = 0, intMax = 100) int value) throws IOException {
    DataParser<Integer> parser = IntegerParsers.bcd("bcd", 2);
    ByteArrayInputStream input =
        new ByteArrayInputStream(HexFormat.of().parseHex(String.format("%02d", value)));
    assertThat(parser.parse(input)).isEqualTo(value);
  }
}
