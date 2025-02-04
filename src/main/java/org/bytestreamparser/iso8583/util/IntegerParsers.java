package org.bytestreamparser.iso8583.util;

import java.nio.charset.Charset;
import org.bytestreamparser.api.parser.DataParser;
import org.bytestreamparser.scalar.parser.StringIntegerParser;
import org.bytestreamparser.scalar.parser.UnsignedByteParser;
import org.bytestreamparser.scalar.parser.UnsignedShortParser;

/** A utility class for creating {@link Integer} parsers. */
public final class IntegerParsers {
  private IntegerParsers() {}

  /**
   * Creates a new unsigned {@link Byte} parser.
   *
   * @param id the ID of the parser.
   */
  public static DataParser<Integer> ubyte(String id) {
    return new UnsignedByteParser(id);
  }

  /**
   * Creates a new unsigned {@link Short} parser.
   *
   * @param id the ID of the parser.
   */
  public static DataParser<Integer> ushort(String id) {
    return new UnsignedShortParser(id);
  }

  /**
   * Creates a new text parser for parsing an {@link Integer}. The radix is 10 and the charset of
   * text is the {@link Charset#defaultCharset()}.
   *
   * @param id the ID of the parser.
   * @param length the length of the text.
   */
  public static DataParser<Integer> text(String id, int length) {
    return text(id, length, 10, Charset.defaultCharset());
  }

  /**
   * Creates a new text parser for parsing an {@link Integer}.
   *
   * @param id the ID of the parser.
   * @param length the length of the text.
   * @param radix the radix of the integer.
   * @param charset the charset of the text.
   */
  public static DataParser<Integer> text(String id, int length, int radix, Charset charset) {
    return new StringIntegerParser(
        id, StringParsers.fixedLength(id, length, charset), length, radix);
  }

  /**
   * Creates a new BCD parser for parsing an {@link Integer}.
   *
   * @param id the ID of the parser.
   * @param length the number of digits in the BCD.
   * @see <a href="https://en.wikipedia.org/wiki/Binary-coded_decimal">Binary Coded Decimal</a>
   */
  public static DataParser<Integer> bcd(String id, int length) {
    return new StringIntegerParser(id, StringParsers.fixedLengthBcd(id, length), length, 10);
  }
}
