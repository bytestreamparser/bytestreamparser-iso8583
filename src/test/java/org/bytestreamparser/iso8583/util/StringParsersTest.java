package org.bytestreamparser.iso8583.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HexFormat;
import org.bytestreamparser.api.parser.DataParser;
import org.bytestreamparser.api.testing.data.TestData;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension.Randomize;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@ExtendWith(RandomParametersExtension.class)
class StringParsersTest {
  private static final HexFormat HEX_FORMAT = HexFormat.of();

  @ParameterizedTest
  @ValueSource(strings = {"US-ASCII", "IBM1047", "ISO-8859-1"})
  void plain(String charset, @Randomize String value) throws IOException {
    ByteArrayInputStream input = new ByteArrayInputStream(value.getBytes(charset));
    DataParser<TestData, String> parser =
        StringParsers.plain("plain", value.length(), Charset.forName(charset));
    assertThat(parser.parse(input)).isEqualTo(value);
  }

  @Test
  void plain_with_default_charset(@Randomize(length = 2, unicodeBlocks = "EMOTICONS") String value)
      throws IOException {
    Charset charset = Charset.defaultCharset();
    ByteArrayInputStream input = new ByteArrayInputStream(value.getBytes(charset));
    DataParser<TestData, String> parser =
        StringParsers.plain("plain", (int) value.codePoints().count());
    assertThat(parser.parse(input)).isEqualTo(value);
  }

  @Test
  void ubyte_plain(
      @Randomize(intMin = 1, intMax = 9) Integer length, @Randomize(length = 10) String value)
      throws IOException {
    ByteBuffer buffer = ByteBuffer.allocate(Byte.BYTES + value.getBytes().length);
    buffer.put(length.byteValue()).put(value.getBytes());
    DataParser<TestData, String> parser =
        StringParsers.ubytePlain("ubyte-plain", Charset.defaultCharset());
    ByteArrayInputStream input = new ByteArrayInputStream(buffer.array());
    assertThat(parser.parse(input)).isEqualTo(value.substring(0, length));

    Integer remaining = 10 - length;
    assertThat(input.available()).isEqualTo(remaining);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    parser.pack(new String(input.readAllBytes()), output);

    byte[] packed = output.toByteArray();
    assertThat(packed[0]).isEqualTo(remaining.byteValue());
  }

  @Test
  void ushort_plain(
      @Randomize(intMin = 1, intMax = 10) Integer length, @Randomize(length = 10) String value)
      throws IOException {
    ByteBuffer buffer = ByteBuffer.allocate(Short.BYTES + value.getBytes().length);
    buffer.putShort(length.shortValue()).put(value.getBytes());
    DataParser<TestData, String> parser =
        StringParsers.ushortPlain("ubyte-short", Charset.defaultCharset());
    ByteArrayInputStream input = new ByteArrayInputStream(buffer.array());
    assertThat(parser.parse(input)).isEqualTo(value.substring(0, length));
  }

  @Test
  void hex(@Randomize byte[] value) throws IOException {
    ByteArrayInputStream input = new ByteArrayInputStream(value);
    DataParser<TestData, String> parser = StringParsers.hex("hex", value.length * 2);
    assertThat(parser.parse(input)).isEqualTo(HEX_FORMAT.formatHex(value));
  }

  @Test
  void bcd(@Randomize(intMin = 0, intMax = 100) int value) throws IOException {
    String bcdString = String.format("%02d", value);
    ByteArrayInputStream input = new ByteArrayInputStream(HEX_FORMAT.parseHex(bcdString));
    DataParser<TestData, String> parser = StringParsers.bcd("bcd", 2);
    assertThat(parser.parse(input)).isEqualTo(bcdString);
  }
}
