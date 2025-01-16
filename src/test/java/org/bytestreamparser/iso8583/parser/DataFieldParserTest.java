package org.bytestreamparser.iso8583.parser;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension.Randomize;
import org.bytestreamparser.iso8583.data.ExtendableBitmap;
import org.bytestreamparser.iso8583.data.FixedBitmap;
import org.bytestreamparser.iso8583.data.IsoMessage;
import org.bytestreamparser.scalar.parser.CharStringParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

@ExtendWith(RandomParametersExtension.class)
class DataFieldParserTest {
  private DataFieldParser<TestIsoMessage, String> fieldParser;
  private int bit;

  @BeforeEach
  void setUp(@Randomize(intMin = 1, intMax = 65) int bit) {
    fieldParser = new DataFieldParser<>(bit, new CharStringParser<>("PAN", 19, UTF_8));
    this.bit = bit;
  }

  @Test
  void applicable() {
    ExtendableBitmap bitmap = new ExtendableBitmap(8).addExtensions(List.of(new FixedBitmap(8)));
    TestIsoMessage message = Mockito.spy(TestIsoMessage.class);
    Mockito.when(message.getBitmap()).thenReturn(bitmap);

    assertThat(fieldParser.applicable(message)).isFalse();

    bitmap.set(bit);
    assertThat(fieldParser.applicable(message)).isTrue();

    bitmap.clear(bit);
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

  public interface TestIsoMessage extends IsoMessage<TestIsoMessage> {}
}
