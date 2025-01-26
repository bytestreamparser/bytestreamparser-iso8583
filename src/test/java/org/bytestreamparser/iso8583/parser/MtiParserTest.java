package org.bytestreamparser.iso8583.parser;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HexFormat;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension.Randomize;
import org.bytestreamparser.iso8583.data.MessageTypeIndicator;
import org.bytestreamparser.scalar.parser.BcdStringParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(RandomParametersExtension.class)
class MtiParserTest {
  private MtiParser parser;

  @BeforeEach
  void setUp() {
    parser = new MtiParser(new BcdStringParser("mti", 4));
  }

  @Test
  void pack(@Randomize(intMin = 0, intMax = 10000) int value) throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    String mti = String.format("%04d", value);
    parser.pack(new MessageTypeIndicator(mti), output);
    assertThat(output.toByteArray()).isEqualTo(HexFormat.of().parseHex(mti));
  }

  @Test
  void parse(@Randomize(intMin = 0, intMax = 10000) int value) throws IOException {
    String mti = String.format("%04d", value);
    ByteArrayInputStream input = new ByteArrayInputStream(HexFormat.of().parseHex(mti));
    MessageTypeIndicator parsed = parser.parse(input);
    assertThat(parsed).hasToString(mti);
  }
}
