package org.bytestreamparser.iso8583.util;

import java.nio.charset.Charset;
import org.bytestreamparser.api.parser.DataParser;
import org.bytestreamparser.scalar.parser.StringLongParser;

public final class LongParsers {
  private LongParsers() {}

  public static DataParser<Long> plain(String id, int length) {
    return plain(id, length, 10, Charset.defaultCharset());
  }

  public static DataParser<Long> plain(String id, int length, int radix, Charset charset) {
    return new StringLongParser(id, StringParsers.plain(id, length, charset), length, radix);
  }

  public static DataParser<Long> bcd(String id, int length) {
    return new StringLongParser(id, StringParsers.bcd(id, length), length, 10);
  }
}
