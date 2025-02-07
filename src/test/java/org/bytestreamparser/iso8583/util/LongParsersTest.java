package org.bytestreamparser.iso8583.util;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.lyang.randomparamsresolver.RandomParametersExtension;
import io.github.lyang.randomparamsresolver.RandomParametersExtension.Randomize;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HexFormat;
import org.bytestreamparser.api.parser.DataParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@ExtendWith(RandomParametersExtension.class)
class LongParsersTest {
  @ParameterizedTest
  @ValueSource(
      strings = {"US-ASCII", "IBM1047", "ISO-8859-1", "UTF-8", "UTF-16", "UTF-16BE", "UTF-16LE"})
  void text(
      String charsetName,
      @Randomize long value,
      @Randomize(intMin = Character.MIN_RADIX, intMax = Character.MAX_RADIX + 1) int radix)
      throws IOException {
    Charset charset = Charset.forName(charsetName);
    String string = Long.toString(value, radix);
    InputStream input = new ByteArrayInputStream(string.getBytes(charset));
    DataParser<Long> parser = LongParsers.text(charsetName, string.length(), radix, charset);
    assertThat(parser.parse(input)).isEqualTo(value);
  }

  @Test
  void text_with_default_radix(@Randomize long value) throws IOException {
    Charset charset = Charset.defaultCharset();
    String string = Long.toString(value);
    InputStream input = new ByteArrayInputStream(string.getBytes(charset));
    DataParser<Long> parser = LongParsers.text(charset.name(), string.length());
    assertThat(parser.parse(input)).isEqualTo(value);
  }

  @Test
  void bcd(@Randomize(longMin = 0, longMax = 100) long value) throws IOException {
    DataParser<Long> parser = LongParsers.bcd("bcd", 2);
    ByteArrayInputStream input =
        new ByteArrayInputStream(HexFormat.of().parseHex(String.format("%02d", value)));
    assertThat(parser.parse(input)).isEqualTo(value);
  }
}
