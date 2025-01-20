package org.bytestreamparser.iso8583.util;

import org.bytestreamparser.api.data.Data;
import org.bytestreamparser.api.parser.DataParser;
import org.bytestreamparser.composite.parser.VariableLengthParser;
import org.bytestreamparser.iso8583.data.ExtendableBitmap;
import org.bytestreamparser.iso8583.data.FixedBitmap;
import org.bytestreamparser.iso8583.parser.ExtendableBitmapParser;
import org.bytestreamparser.iso8583.parser.FixedBitmapParser;
import org.bytestreamparser.scalar.parser.BinaryParser;

public final class BinaryParsers {
  private BinaryParsers() {}

  public static <P extends Data<P>> DataParser<P, byte[]> bin(String id, int length) {
    return new BinaryParser<>(id, length);
  }

  public static <P extends Data<P>> DataParser<P, byte[]> ubyteBin(String id) {
    return varBin(id, IntegerParsers.ubyte(id));
  }

  public static <P extends Data<P>> DataParser<P, byte[]> ushortBin(String id) {
    return varBin(id, IntegerParsers.ushort(id));
  }

  public static <P extends Data<P>> DataParser<P, byte[]> varBin(
      String id, DataParser<?, Integer> lengthParser) {
    return new VariableLengthParser<>(
        id, lengthParser, length -> bin(id, length), bytes -> bytes.length);
  }

  public static <P extends Data<P>> DataParser<P, FixedBitmap> bmp(String id, int bytes) {
    return new FixedBitmapParser<>(id, bytes);
  }

  public static <P extends Data<P>> DataParser<P, ExtendableBitmap> ebmp(String id, int bytes) {
    return new ExtendableBitmapParser<>(id, bytes);
  }
}
