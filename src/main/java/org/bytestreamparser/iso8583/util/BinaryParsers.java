package org.bytestreamparser.iso8583.util;

import org.bytestreamparser.api.parser.DataParser;
import org.bytestreamparser.composite.parser.VariableLengthParser;
import org.bytestreamparser.iso8583.data.ExtendableBitmap;
import org.bytestreamparser.iso8583.data.FixedBitmap;
import org.bytestreamparser.iso8583.parser.ExtendableBitmapParser;
import org.bytestreamparser.iso8583.parser.FixedBitmapParser;
import org.bytestreamparser.scalar.parser.BinaryParser;

/** Utility class for creating binary data parsers. */
public final class BinaryParsers {
  private BinaryParsers() {}

  /**
   * Creates a new fixed length binary data parser.
   *
   * @param id the ID of the parser.
   * @param length the length of the binary data.
   */
  public static DataParser<byte[]> fixedLength(String id, int length) {
    return new BinaryParser(id, length);
  }

  /**
   * Creates a variable length parser where the length is encoded in a single unsigned byte.
   *
   * @param id the ID of the parser.
   */
  public static DataParser<byte[]> binaryLVar(String id) {
    return variableLength(id, IntegerParsers.ubyte(id));
  }

  /**
   * Creates a variable length parser where the length is encoded in unsigned short.
   *
   * @param id the ID of the parser.
   */
  public static DataParser<byte[]> binaryLLVar(String id) {
    return variableLength(id, IntegerParsers.ushort(id));
  }

  /**
   * Creates a variable length parser where the length is encoded in two digits text.
   *
   * @param id the ID of the parser.
   */
  public static DataParser<byte[]> textLLVar(String id) {
    return variableLength(id, IntegerParsers.text(id, 2));
  }

  /**
   * Creates a variable length parser where the length is encoded in three digits text.
   *
   * @param id the ID of the parser.
   */
  public static DataParser<byte[]> textLLLVar(String id) {
    return variableLength(id, IntegerParsers.text(id, 3));
  }

  /**
   * Creates a variable length parser using a {@code DataParser<Integer>} parser.
   *
   * @param id the ID of the parser.
   * @param lengthParser the parser for the length.
   */
  public static DataParser<byte[]> variableLength(String id, DataParser<Integer> lengthParser) {
    return new VariableLengthParser<>(
        id, lengthParser, length -> fixedLength(id, length), bytes -> bytes.length);
  }

  /**
   * Creates a new fixed bitmap parser.
   *
   * @param id the ID of the parser.
   * @param bytes the number of bytes in the bitmap.
   */
  public static DataParser<FixedBitmap> fixedBitmap(String id, int bytes) {
    return new FixedBitmapParser(id, bytes);
  }

  /**
   * Creates a new extendable bitmap parser.
   *
   * @param id the ID of the parser.
   * @param bytes the number of bytes per bitmap.
   */
  public static DataParser<ExtendableBitmap> extendableBitmap(String id, int bytes) {
    return new ExtendableBitmapParser(id, bytes);
  }
}
