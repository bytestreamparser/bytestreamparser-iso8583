package org.bytestreamparser.iso8583.util;

import java.nio.charset.Charset;
import org.bytestreamparser.api.data.Data;
import org.bytestreamparser.scalar.parser.BcdStringParser;
import org.bytestreamparser.scalar.parser.CharStringParser;
import org.bytestreamparser.scalar.parser.HexStringParser;
import org.bytestreamparser.scalar.parser.StringParser;

public final class StringParsers {
  private StringParsers() {}

  public static <P extends Data<P>> StringParser<P> plain(String id, int length) {
    return plain(id, length, Charset.defaultCharset());
  }

  public static <P extends Data<P>> StringParser<P> plain(String id, int length, Charset charset) {
    return new CharStringParser<>(id, length, charset);
  }

  public static <P extends Data<P>> StringParser<P> hex(String id, int length) {
    return new HexStringParser<>(id, length);
  }

  public static <P extends Data<P>> StringParser<P> bcd(String id, int length) {
    return new BcdStringParser<>(id, length);
  }
}
