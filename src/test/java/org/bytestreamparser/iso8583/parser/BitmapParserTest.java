package org.bytestreamparser.iso8583.parser;

import static com.github.lyang.iso8583.core.parser.BitmapParser.ERROR_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.lyang.iso8583.core.datatype.Bitmap;
import com.github.lyang.iso8583.testing.TestCompositeComponent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class BitmapParserTest {

  private BitmapParser<TestCompositeComponent> parser;

  @Test
  void parse_fixed() throws IOException {
    parser = new BitmapParser<>("bmp", 1, 0);
    ByteArrayInputStream input = new ByteArrayInputStream(new byte[] {(byte) 0x80, 0x01});
    Bitmap bitmap = parser.parse(input).getValue();
    assertThat(bitmap.capacity()).isEqualTo(8);
    assertThat(bitmap.cardinality()).isEqualTo(1);
    assertThat(bitmap.get(1)).isTrue();
    assertThat(input.available()).isEqualTo(1);
  }

  @Test
  void parse_used_extension() throws IOException {
    parser = new BitmapParser<>("bmp", 1, 1);
    ByteArrayInputStream input = new ByteArrayInputStream(new byte[] {(byte) 0x80, 0x01});
    Bitmap bitmap = parser.parse(input).getValue();
    assertThat(bitmap.capacity()).isEqualTo(16);
    assertThat(bitmap.cardinality()).isEqualTo(2);
    assertThat(bitmap.get(1)).isTrue();
    assertThat(bitmap.get(16)).isTrue();
    assertThat(input.available()).isEqualTo(0);
  }

  @Test
  void parse_unused_extension() throws IOException {
    parser = new BitmapParser<>("bmp", 1, 1);
    ByteArrayInputStream input = new ByteArrayInputStream(new byte[] {(byte) 0x01, 0x01});
    Bitmap bitmap = parser.parse(input).getValue();
    assertThat(bitmap.capacity()).isEqualTo(16);
    assertThat(bitmap.cardinality()).isEqualTo(1);
    assertThat(bitmap.get(8)).isTrue();
    assertThat(input.available()).isEqualTo(1);
  }

  @Test
  void pack_fixed() {
    parser = new BitmapParser<>("bmp", 1, 0);
    Bitmap value = new Bitmap(1);
    assertThat(parser.pack(value)).isEqualTo(value.toByteArray());
  }

  @Test
  void pack_used_extension() {
    parser = new BitmapParser<>("bmp", 1, 1);
    Bitmap value = new Bitmap(1, 1);
    value.set(value.capacity());
    assertThat(parser.pack(value)).isEqualTo(value.toByteArray());
  }

  @Test
  void pack_unused_extension() {
    parser = new BitmapParser<>("bmp", 1, 1);
    Bitmap value = new Bitmap(1, 1);
    assertThat(parser.pack(value)).isEqualTo(value.toByteArray());
  }

  @Test
  void pack_over_sized_bitmap() {
    parser = new BitmapParser<>("bmp", 1, 1);
    Bitmap value = new Bitmap(1, 2);
    assertThatThrownBy(() -> parser.pack(value))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(ERROR_MESSAGE + " [%s]", parser.getId(), 16, 24);
  }
}
