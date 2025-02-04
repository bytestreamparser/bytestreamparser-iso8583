package org.bytestreamparser.iso8583.util;

import java.nio.charset.Charset;
import org.bytestreamparser.api.parser.DataParser;
import org.bytestreamparser.scalar.parser.StringLongParser;

/** A utility class for creating {@link Long} parsers. */
public final class LongParsers {
  private LongParsers() {}

  /**
   * Creates a new text parser for parsing a {@link Long}. The radix is 10 and the charset of the
   * text is the {@link Charset#defaultCharset()}.
   *
   * @param id the ID of the parser.
   * @param length the length of the text.
   */
  public static DataParser<Long> text(String id, int length) {
    return text(id, length, 10, Charset.defaultCharset());
  }

  /**
   * Creates a new text parser for parsing a {@link Long}.
   *
   * @param id the ID of the parser.
   * @param length the length of the text.
   * @param radix the radix of the long.
   * @param charset the charset of the text.
   */
  public static DataParser<Long> text(String id, int length, int radix, Charset charset) {
    return new StringLongParser(id, StringParsers.fixedLength(id, length, charset), length, radix);
  }

  /**
   * Creates a new BCD parser for parsing a {@link Long}.
   *
   * @param id the ID of the parser.
   * @param length the number of digits in the BCD.
   * @see <a href="https://en.wikipedia.org/wiki/Binary-coded_decimal">Binary Coded Decimal</a>
   */
  public static DataParser<Long> bcd(String id, int length) {
    return new StringLongParser(id, StringParsers.fixedLengthBcd(id, length), length, 10);
  }
}
