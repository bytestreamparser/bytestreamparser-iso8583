package org.bytestreamparser.iso8583.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.bytestreamparser.api.parser.DataParser;
import org.bytestreamparser.api.testing.data.TestData;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension.Randomize;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(RandomParametersExtension.class)
class IntegerParsersTest {

  @Test
  void ubyte(@Randomize byte[] value) throws IOException {
    DataParser<TestData, Integer> parser = IntegerParsers.ubyte("ubyte");
    ByteArrayInputStream input = new ByteArrayInputStream(value);
    assertThat(parser.parse(input)).isEqualTo(value[0] & 0xFF);
  }

  @Test
  void ushort(@Randomize byte[] value) throws IOException {
    DataParser<TestData, Integer> parser = IntegerParsers.ushort("ushort");
    ByteArrayInputStream input = new ByteArrayInputStream(value);
    int expected = ((int) value[0] & 0xFF) << 8 | value[1] & 0xFF;
    assertThat(parser.parse(input)).isEqualTo(expected);
  }
}
