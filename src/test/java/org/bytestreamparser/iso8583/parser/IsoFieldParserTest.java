package org.bytestreamparser.iso8583.parser;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension.Randomize;
import org.bytestreamparser.iso8583.helper.TestIsoMessage;
import org.bytestreamparser.scalar.parser.CharStringParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(RandomParametersExtension.class)
class IsoFieldParserTest {
  private IsoFieldParser<TestIsoMessage, String> fieldParser;
  private int bit;

  @BeforeEach
  void setUp(@Randomize(intMin = 2) int bit) {
    fieldParser = new IsoFieldParser<>(bit, new CharStringParser("PAN", 19, UTF_8));
    this.bit = bit;
  }

  @Test
  void applicable() {
    TestIsoMessage message = new TestIsoMessage();

    assertThat(fieldParser.applicable(message)).isFalse();

    message.set(String.valueOf(bit), "1234567890123456789");
    assertThat(fieldParser.applicable(message)).isTrue();

    message.clear(String.valueOf(bit));
    assertThat(fieldParser.applicable(message)).isFalse();
  }

  @Test
  void pack(@Randomize(length = 19) String value) throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    fieldParser.pack(value, output);
    assertThat(output.toByteArray()).isEqualTo(value.getBytes(UTF_8));
  }

  @Test
  void parse(@Randomize(length = 19) String value) throws IOException {
    ByteArrayInputStream input = new ByteArrayInputStream(value.getBytes(UTF_8));
    assertThat(fieldParser.parse(input)).isEqualTo(value);
  }
}
