package org.bytestreamparser.iso8583.util;

import java.nio.charset.Charset;
import org.bytestreamparser.api.parser.DataParser;
import org.bytestreamparser.composite.parser.VariableLengthParser;
import org.bytestreamparser.scalar.parser.BcdStringParser;
import org.bytestreamparser.scalar.parser.CharStringParser;
import org.bytestreamparser.scalar.parser.HexStringParser;

public final class StringParsers {
  private StringParsers() {}

  public static DataParser<String> fixedLength(String id, int length) {
    return fixedLength(id, length, Charset.defaultCharset());
  }

  public static DataParser<String> fixedLength(String id, int length, Charset charset) {
    return new CharStringParser(id, length, charset);
  }

  public static DataParser<String> binaryLVar(String id, Charset charset) {
    return variableLength(id, IntegerParsers.ubyte(id), charset);
  }

  public static DataParser<String> binaryLLVar(String id, Charset charset) {
    return variableLength(id, IntegerParsers.ushort(id), charset);
  }

  public static DataParser<String> textLLVar(String id) {
    return variableLength(id, IntegerParsers.text(id, 2), Charset.defaultCharset());
  }

  public static DataParser<String> textLLLVar(String id) {
    return variableLength(id, IntegerParsers.text(id, 3), Charset.defaultCharset());
  }

  public static DataParser<String> variableLength(
      String id, DataParser<Integer> lengthParser, Charset charset) {
    return new VariableLengthParser<>(
        id,
        lengthParser,
        length -> fixedLength(id, length, charset),
        string -> (int) string.codePoints().count());
  }

  public static DataParser<String> fixedLengthHex(String id, int length) {
    return new HexStringParser(id, length);
  }

  public static DataParser<String> fixedLengthBcd(String id, int length) {
    return new BcdStringParser(id, length);
  }

  public static DataParser<String> binaryLVarBcd(String id) {
    return new VariableLengthParser<>(
        id,
        IntegerParsers.ubyte(id),
        length -> fixedLengthBcd(id, length),
        string -> (int) string.codePoints().count());
  }
}
