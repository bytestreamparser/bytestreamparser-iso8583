package org.bytestreamparser.iso8583.util;

import java.nio.charset.Charset;
import org.bytestreamparser.api.data.Data;
import org.bytestreamparser.api.parser.DataParser;
import org.bytestreamparser.scalar.parser.StringIntegerParser;
import org.bytestreamparser.scalar.parser.UnsignedByteParser;
import org.bytestreamparser.scalar.parser.UnsignedShortParser;

public final class IntegerParsers {
  private IntegerParsers() {}

  public static <P extends Data<P>> DataParser<P, Integer> ubyte(String id) {
    return new UnsignedByteParser<>(id);
  }

  public static <P extends Data<P>> DataParser<P, Integer> ushort(String id) {
    return new UnsignedShortParser<>(id);
  }

  public static <P extends Data<P>> DataParser<P, Integer> plain(String id, int length) {
    return plain(id, length, 10, Charset.defaultCharset());
  }

  public static <P extends Data<P>> DataParser<P, Integer> plain(
      String id, int length, int radix, Charset charset) {
    return new StringIntegerParser<>(id, StringParsers.plain(id, length, charset), length, radix);
  }
}
