package org.bytestreamparser.iso8583.util;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.lyang.randomparamsresolver.RandomParametersExtension;
import io.github.lyang.randomparamsresolver.RandomParametersExtension.Randomize;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HexFormat;
import org.bytestreamparser.api.parser.DataParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@ExtendWith(RandomParametersExtension.class)
class StringParsersTest {
  private static final HexFormat HEX_FORMAT = HexFormat.of();

  @ParameterizedTest
  @ValueSource(strings = {"US-ASCII", "IBM1047", "ISO-8859-1"})
  void fixedLength(String charset, @Randomize String value) throws IOException {
    ByteArrayInputStream input = new ByteArrayInputStream(value.getBytes(charset));
    DataParser<String> parser =
        StringParsers.fixedLength("fixedLength", value.length(), Charset.forName(charset));
    assertThat(parser.parse(input)).isEqualTo(value);
  }

  @Test
  void fixed_length_with_default_charset(
      @Randomize(length = 2, unicodeBlocks = "EMOTICONS") String value) throws IOException {
    Charset charset = Charset.defaultCharset();
    ByteArrayInputStream input = new ByteArrayInputStream(value.getBytes(charset));
    DataParser<String> parser =
        StringParsers.fixedLength("fixedLength", (int) value.codePoints().count());
    assertThat(parser.parse(input)).isEqualTo(value);
  }

  @Test
  void binaryLVar(
      @Randomize(intMin = 1, intMax = 9) Integer length, @Randomize(length = 10) String value)
      throws IOException {
    ByteBuffer buffer = ByteBuffer.allocate(Byte.BYTES + value.getBytes().length);
    buffer.put(length.byteValue()).put(value.getBytes());
    DataParser<String> parser = StringParsers.binaryLVar("binaryLVar", Charset.defaultCharset());
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
  void binaryLLVar(
      @Randomize(intMin = 1, intMax = 10) Integer length, @Randomize(length = 10) String value)
      throws IOException {
    ByteBuffer buffer = ByteBuffer.allocate(Short.BYTES + value.getBytes().length);
    buffer.putShort(length.shortValue()).put(value.getBytes());
    DataParser<String> parser = StringParsers.binaryLLVar("binaryLLVar", Charset.defaultCharset());
    ByteArrayInputStream input = new ByteArrayInputStream(buffer.array());
    assertThat(parser.parse(input)).isEqualTo(value.substring(0, length));
  }

  @Test
  void textLLVar(
      @Randomize(intMin = 1, intMax = 10) int length, @Randomize(length = 10) String value)
      throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    output.write(String.format("%02d", length).getBytes());
    output.write(value.getBytes());
    DataParser<String> parser = StringParsers.textLLVar("plainLLVar");
    ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
    assertThat(parser.parse(input)).isEqualTo(value.substring(0, length));
    assertThat(input.available()).isEqualTo(value.length() - length);
  }

  @Test
  void textLLLVar(
      @Randomize(intMin = 1, intMax = 10) int length, @Randomize(length = 10) String value)
      throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    output.write(String.format("%03d", length).getBytes());
    output.write(value.getBytes());
    DataParser<String> parser = StringParsers.textLLLVar("plainLLLVar");
    ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
    assertThat(parser.parse(input)).isEqualTo(value.substring(0, length));
    assertThat(input.available()).isEqualTo(value.length() - length);
  }

  @Test
  void fixedLengthHex(@Randomize byte[] value) throws IOException {
    ByteArrayInputStream input = new ByteArrayInputStream(value);
    DataParser<String> parser = StringParsers.fixedLengthHex("hex", value.length * 2);
    assertThat(parser.parse(input)).isEqualTo(HEX_FORMAT.formatHex(value));
  }

  @Test
  void fixedLengthBcd(@Randomize(intMin = 0, intMax = 100) int value) throws IOException {
    String bcdString = String.format("%02d", value);
    ByteArrayInputStream input = new ByteArrayInputStream(HEX_FORMAT.parseHex(bcdString));
    DataParser<String> parser = StringParsers.fixedLengthBcd("bcd", 2);
    assertThat(parser.parse(input)).isEqualTo(bcdString);
  }

  @Test
  void binaryLVarBcd(@Randomize(intMin = 0, intMax = 999999999) int value) throws IOException {
    String stringValue = Integer.toString(value);
    String padded = stringValue.length() % 2 == 0 ? stringValue : "0" + stringValue;
    byte[] bytes = HexFormat.of().parseHex(padded);
    ByteBuffer buffer = ByteBuffer.allocate(Byte.BYTES + bytes.length);
    buffer.put((byte) stringValue.length());
    buffer.put(bytes);
    ByteArrayInputStream input = new ByteArrayInputStream(buffer.array());

    DataParser<String> parser = StringParsers.binaryLVarBcd("binaryLVarBcd");

    String parsed = parser.parse(input);
    assertThat(parsed).isEqualTo(stringValue);

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    parser.pack(parsed, output);
    assertThat(output.toByteArray()).isEqualTo(buffer.array());
  }
}
