package org.bytestreamparser.iso8583.util;

import java.nio.charset.Charset;
import org.bytestreamparser.api.parser.DataParser;
import org.bytestreamparser.composite.parser.VariableLengthParser;
import org.bytestreamparser.scalar.parser.BcdStringParser;
import org.bytestreamparser.scalar.parser.CharStringParser;
import org.bytestreamparser.scalar.parser.HexStringParser;

/** A utility class for creating {@link String} parsers. */
public final class StringParsers {
  private StringParsers() {}

  /**
   * Creates a new fixed length {@link String} parser. The charset of the text is the {@link
   * Charset#defaultCharset()}.
   *
   * @param id the ID of the parser.
   * @param length the number of characters in the string.
   */
  public static DataParser<String> fixedLength(String id, int length) {
    return fixedLength(id, length, Charset.defaultCharset());
  }

  /**
   * Creates a new fixed length {@link String} parser.
   *
   * @param id the ID of the parser.
   * @param length the number of characters in the string.
   * @param charset the charset of the text.
   */
  public static DataParser<String> fixedLength(String id, int length, Charset charset) {
    return new CharStringParser(id, length, charset);
  }

  /**
   * Creates a variable length parser where the length is encoded in a single unsigned byte.
   *
   * @param id the ID of the parser.
   * @param charset the charset of the text.
   */
  public static DataParser<String> binaryLVar(String id, Charset charset) {
    return variableLength(id, IntegerParsers.ubyte(id), charset);
  }

  /**
   * Creates a variable length parser where the length is encoded in unsigned short.
   *
   * @param id the ID of the parser.
   * @param charset the charset of the text.
   */
  public static DataParser<String> binaryLLVar(String id, Charset charset) {
    return variableLength(id, IntegerParsers.ushort(id), charset);
  }

  /**
   * Creates a variable length parser where the length is encoded in two digits text. The charset of
   * the text is the {@link Charset#defaultCharset()}.
   *
   * @param id the ID of the parser.
   */
  public static DataParser<String> textLLVar(String id) {
    return variableLength(id, IntegerParsers.text(id, 2), Charset.defaultCharset());
  }

  /**
   * Creates a variable length parser where the length is encoded in three digits text. The charset
   * of the text is the {@link Charset#defaultCharset()}.
   *
   * @param id the ID of the parser.
   */
  public static DataParser<String> textLLLVar(String id) {
    return variableLength(id, IntegerParsers.text(id, 3), Charset.defaultCharset());
  }

  /**
   * Creates a variable length parser using a {@code DataParser<Integer>} parser.
   *
   * @param id the ID of the parser.
   * @param lengthParser the parser for the length.
   * @param charset the charset of the text.
   */
  public static DataParser<String> variableLength(
      String id, DataParser<Integer> lengthParser, Charset charset) {
    return new VariableLengthParser<>(
        id,
        lengthParser,
        length -> fixedLength(id, length, charset),
        string -> (int) string.codePoints().count());
  }

  /**
   * Creates a new fixed length hex {@link String} parser.
   *
   * @param id the ID of the parser.
   * @param length the number of characters in the string.
   */
  public static DataParser<String> fixedLengthHex(String id, int length) {
    return new HexStringParser(id, length);
  }

  /**
   * Creates a new fixed length BCD {@link String} parser.
   *
   * @param id the ID of the parser.
   * @param length the number of characters in the string.
   * @see <a href="https://en.wikipedia.org/wiki/Binary-coded_decimal">Binary Coded Decimal</a>
   */
  public static DataParser<String> fixedLengthBcd(String id, int length) {
    return new BcdStringParser(id, length);
  }

  /**
   * Creates a new variable length BCD {@link String} parser where the length is encoded in a single
   * unsigned byte.
   *
   * @param id the ID of the parser.
   * @see <a href="https://en.wikipedia.org/wiki/Binary-coded_decimal">Binary Coded Decimal</a>
   */
  public static DataParser<String> binaryLVarBcd(String id) {
    return new VariableLengthParser<>(
        id,
        IntegerParsers.ubyte(id),
        length -> fixedLengthBcd(id, length),
        string -> (int) string.codePoints().count());
  }
}
