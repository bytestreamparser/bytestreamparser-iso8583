package org.bytestreamparser.iso8583.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.List;
import org.bytestreamparser.api.testing.data.TestData;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension.Randomize;
import org.bytestreamparser.iso8583.data.ExtendableBitmap;
import org.bytestreamparser.iso8583.data.FixedBitmap;
import org.bytestreamparser.iso8583.helper.TestHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(RandomParametersExtension.class)
class ExtendableBitmapParserTest {

  @Test
  void pack(@Randomize byte[] content) throws IOException {
    content[0] = (byte) (0b01111111 & content[0]);
    ExtendableBitmap bitmap =
        new ExtendableBitmap(content.length).addExtensions(List.of(FixedBitmap.valueOf(content)));
    ExtendableBitmapParser<TestData> parser =
        new ExtendableBitmapParser<>("bitmap", content.length);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    parser.pack(bitmap, output);
    assertArrayEquals(content, output.toByteArray());
  }

  @Test
  void pack_with_extension(@Randomize(length = 10) byte[] content) throws IOException {
    content[0] = (byte) (0b10000000 | content[0]);
    content[content.length / 2] = (byte) (0b01111111 & content[0]);
    ByteArrayInputStream input = new ByteArrayInputStream(content);
    ExtendableBitmapParser<TestData> parser =
        new ExtendableBitmapParser<>("bitmap", content.length / 2);
    ExtendableBitmap bitmap = parser.parse(input);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    parser.pack(bitmap, output);
    assertThat(output.toByteArray()).isEqualTo(content);
  }

  @Test
  void pack_insufficient_data(@Randomize byte[] content) {
    ExtendableBitmap bitmap =
        new ExtendableBitmap(content.length).addExtensions(List.of(FixedBitmap.valueOf(content)));
    ExtendableBitmapParser<TestData> parser =
        new ExtendableBitmapParser<>("bitmap", content.length + 1);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    assertThatThrownBy(() -> parser.pack(bitmap, output))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "bitmap: value must be a multiple of %d bytes, but got [%d]",
            content.length + 1, content.length);
  }

  @Test
  void pack_oversize_data(@Randomize byte[] content) {
    ExtendableBitmap bitmap =
        new ExtendableBitmap(content.length).addExtensions(List.of(FixedBitmap.valueOf(content)));
    ExtendableBitmapParser<TestData> parser =
        new ExtendableBitmapParser<>("bitmap", content.length - 1);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    assertThatThrownBy(() -> parser.pack(bitmap, output))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "bitmap: value must be a multiple of %d bytes, but got [%d]",
            content.length - 1, content.length);
  }

  @Test
  void parse(@Randomize byte[] content) throws IOException {
    content[0] = (byte) (0b01111111 & content[0]);
    ByteArrayInputStream input = new ByteArrayInputStream(content);
    ExtendableBitmapParser<TestData> parser =
        new ExtendableBitmapParser<>("bitmap", content.length);
    ExtendableBitmap bitmap = parser.parse(input);
    char[] chars = TestHelper.toBinaryString(content).toCharArray();
    for (int index = 0; index < chars.length; index++) {
      assertThat(bitmap.get(index + 1)).isEqualTo(chars[index] == '1');
    }
  }

  @Test
  void parse_with_extension(@Randomize(length = 10) byte[] content) throws IOException {
    content[0] = (byte) (0b10000000 | content[0]);
    content[content.length / 2] = (byte) (0b01111111 & content[content.length / 2]);
    content[content.length / 2] = (byte) (0b00000001 | content[content.length / 2]);
    ByteArrayInputStream input = new ByteArrayInputStream(content);
    ExtendableBitmapParser<TestData> parser =
        new ExtendableBitmapParser<>("bitmap", content.length / 2);
    ExtendableBitmap bitmap = parser.parse(input);
    char[] chars = TestHelper.toBinaryString(content).toCharArray();
    for (int index = 0; index < chars.length; index++) {
      assertThat(bitmap.get(index + 1)).isEqualTo(chars[index] == '1');
    }
  }

  @Test
  void parse_insufficient_data(@Randomize byte[] content) {
    ByteArrayInputStream input = new ByteArrayInputStream(content);
    ExtendableBitmapParser<TestData> parser =
        new ExtendableBitmapParser<>("id", content.length + 1);
    assertThatThrownBy(() -> parser.parse(input))
        .isInstanceOf(EOFException.class)
        .hasMessage(
            "End of stream reached after reading %d bytes, bytes expected [%d]",
            content.length, content.length + 1);
  }
}
