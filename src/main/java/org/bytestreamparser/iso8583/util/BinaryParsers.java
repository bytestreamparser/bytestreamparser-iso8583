package org.bytestreamparser.iso8583.util;

import org.bytestreamparser.api.parser.DataParser;
import org.bytestreamparser.composite.parser.VariableLengthParser;
import org.bytestreamparser.iso8583.data.ExtendableBitmap;
import org.bytestreamparser.iso8583.data.FixedBitmap;
import org.bytestreamparser.iso8583.parser.ExtendableBitmapParser;
import org.bytestreamparser.iso8583.parser.FixedBitmapParser;
import org.bytestreamparser.scalar.parser.BinaryParser;

public final class BinaryParsers {
  private BinaryParsers() {}

  public static DataParser<byte[]> fixedLength(String id, int length) {
    return new BinaryParser(id, length);
  }

  public static DataParser<byte[]> binaryLVar(String id) {
    return variableLength(id, IntegerParsers.ubyte(id));
  }

  public static DataParser<byte[]> binaryLLVar(String id) {
    return variableLength(id, IntegerParsers.ushort(id));
  }

  public static DataParser<byte[]> textLLVar(String id) {
    return variableLength(id, IntegerParsers.text(id, 2));
  }

  public static DataParser<byte[]> textLLLVar(String id) {
    return variableLength(id, IntegerParsers.text(id, 3));
  }

  public static DataParser<byte[]> variableLength(String id, DataParser<Integer> lengthParser) {
    return new VariableLengthParser<>(
        id, lengthParser, length -> fixedLength(id, length), bytes -> bytes.length);
  }

  public static DataParser<FixedBitmap> fixedBitmap(String id, int bytes) {
    return new FixedBitmapParser(id, bytes);
  }

  public static DataParser<ExtendableBitmap> extendableBitmap(String id, int bytes) {
    return new ExtendableBitmapParser(id, bytes);
  }
}
