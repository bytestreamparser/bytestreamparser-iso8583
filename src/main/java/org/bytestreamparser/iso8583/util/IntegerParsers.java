package org.bytestreamparser.iso8583.util;

import java.nio.charset.Charset;
import org.bytestreamparser.api.parser.DataParser;
import org.bytestreamparser.scalar.parser.StringIntegerParser;
import org.bytestreamparser.scalar.parser.UnsignedByteParser;
import org.bytestreamparser.scalar.parser.UnsignedShortParser;

public final class IntegerParsers {
  private IntegerParsers() {}

  public static DataParser<Integer> ubyte(String id) {
    return new UnsignedByteParser(id);
  }

  public static DataParser<Integer> ushort(String id) {
    return new UnsignedShortParser(id);
  }

  public static DataParser<Integer> plain(String id, int length) {
    return plain(id, length, 10, Charset.defaultCharset());
  }

  public static DataParser<Integer> plain(String id, int length, int radix, Charset charset) {
    return new StringIntegerParser(id, StringParsers.plain(id, length, charset), length, radix);
  }

  public static DataParser<Integer> bcd(String id, int length) {
    return new StringIntegerParser(id, StringParsers.bcd(id, length), length, 10);
  }
}
