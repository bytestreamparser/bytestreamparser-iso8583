package org.bytestreamparser.iso8583.parser;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension.Randomize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(RandomParametersExtension.class)
class UndefinedDataFieldParserTest {

  private UndefinedDataFieldParser parser;

  @BeforeEach
  void setUp(@Randomize(intMin = 2, intMax = 193) int field) {
    parser = new UndefinedDataFieldParser(String.valueOf(field));
  }

  @Test
  void pack() {
    Object value = new Object();
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    assertThatThrownBy(() -> parser.pack(value, output))
        .isInstanceOf(UnsupportedOperationException.class)
        .hasMessage("%s: Undefined data field", parser.getId());
  }

  @Test
  void parse(@Randomize byte[] value) {
    ByteArrayInputStream input = new ByteArrayInputStream(value);
    assertThatThrownBy(() -> parser.parse(input))
        .isInstanceOf(UnsupportedOperationException.class)
        .hasMessage("%s: Undefined data field", parser.getId());
  }
}
