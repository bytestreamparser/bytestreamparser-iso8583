package org.bytestreamparser.iso8583.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension.Randomize;
import org.bytestreamparser.iso8583.data.FixedBitmap;
import org.bytestreamparser.iso8583.helper.TestHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(RandomParametersExtension.class)
class FixedBitmapParserTest {

  @Test
  void pack(@Randomize byte[] content) throws IOException {
    FixedBitmap bitmap = FixedBitmap.valueOf(content);
    FixedBitmapParser parser = new FixedBitmapParser("bitmap", content.length);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    parser.pack(bitmap, output);
    assertArrayEquals(content, output.toByteArray());
  }

  @Test
  void pack_insufficient_data(@Randomize byte[] content) {
    FixedBitmap bitmap = FixedBitmap.valueOf(content);
    FixedBitmapParser parser = new FixedBitmapParser("bitmap", content.length + 1);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    assertThatThrownBy(() -> parser.pack(bitmap, output))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "bitmap: value must be %d bytes, but got [%d]", content.length + 1, content.length);
  }

  @Test
  void pack_oversize_data(@Randomize byte[] content) {
    FixedBitmap bitmap = FixedBitmap.valueOf(content);
    FixedBitmapParser parser = new FixedBitmapParser("bitmap", content.length - 1);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    assertThatThrownBy(() -> parser.pack(bitmap, output))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "bitmap: value must be %d bytes, but got [%d]", content.length - 1, content.length);
  }

  @Test
  void parse(@Randomize byte[] content) throws IOException {
    ByteArrayInputStream input = new ByteArrayInputStream(content);
    FixedBitmapParser parser = new FixedBitmapParser("id", content.length);
    FixedBitmap bitmap = parser.parse(input);
    char[] chars = TestHelper.toBinaryString(content).toCharArray();
    for (int index = 0; index < chars.length; index++) {
      assertThat(bitmap.get(index + 1)).isEqualTo(chars[index] == '1');
    }
  }

  @Test
  void parse_insufficient_data(@Randomize byte[] content) {
    ByteArrayInputStream input = new ByteArrayInputStream(content);
    FixedBitmapParser parser = new FixedBitmapParser("id", content.length + 1);
    assertThatThrownBy(() -> parser.parse(input))
        .isInstanceOf(EOFException.class)
        .hasMessage(
            "End of stream reached after reading %d bytes, bytes expected [%d]",
            content.length, content.length + 1);
  }
}
