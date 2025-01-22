package org.bytestreamparser.iso8583.util;

import java.nio.charset.Charset;
import org.bytestreamparser.api.data.Data;
import org.bytestreamparser.api.parser.DataParser;
import org.bytestreamparser.composite.parser.VariableLengthParser;
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

  public static <P extends Data<P>> DataParser<P, String> ubytePlain(String id, Charset charset) {
    return varPlain(id, IntegerParsers.ubyte(id), charset);
  }

  public static <P extends Data<P>> DataParser<P, String> ushortPlain(String id, Charset charset) {
    return varPlain(id, IntegerParsers.ushort(id), charset);
  }

  public static <P extends Data<P>> DataParser<P, String> llPlain(String id) {
    return varPlain(id, IntegerParsers.plain(id, 2), Charset.defaultCharset());
  }

  public static <P extends Data<P>> DataParser<P, String> lllPlain(String id) {
    return varPlain(id, IntegerParsers.plain(id, 3), Charset.defaultCharset());
  }

  public static <P extends Data<P>> DataParser<P, String> varPlain(
      String id, DataParser<?, Integer> lengthParser, Charset charset) {
    return new VariableLengthParser<>(
        id,
        lengthParser,
        length -> plain(id, length, charset),
        string -> (int) string.codePoints().count());
  }

  public static <P extends Data<P>> StringParser<P> hex(String id, int length) {
    return new HexStringParser<>(id, length);
  }

  public static <P extends Data<P>> StringParser<P> bcd(String id, int length) {
    return new BcdStringParser<>(id, length);
  }

  public static <P extends Data<P>> DataParser<P, String> ubyteBcd(String id) {
    return new VariableLengthParser<>(
        id,
        IntegerParsers.ubyte(id),
        length -> bcd(id, length),
        string -> (int) string.codePoints().count());
  }
}
