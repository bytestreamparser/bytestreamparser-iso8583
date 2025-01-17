package org.bytestreamparser.iso8583.util;

import org.bytestreamparser.api.data.Data;
import org.bytestreamparser.api.parser.DataParser;
import org.bytestreamparser.iso8583.data.ExtendableBitmap;
import org.bytestreamparser.iso8583.data.FixedBitmap;
import org.bytestreamparser.iso8583.parser.ExtendableBitmapParser;
import org.bytestreamparser.iso8583.parser.FixedBitmapParser;
import org.bytestreamparser.scalar.parser.BinaryParser;

public final class BinaryParsers {
  private BinaryParsers() {}

  public static <P extends Data<P>> DataParser<P, byte[]> BIN(String id, int length) {
    return new BinaryParser<>(id, length);
  }

  public static <P extends Data<P>> DataParser<P, FixedBitmap> BMP(String id, int bytes) {
    return new FixedBitmapParser<>(id, bytes);
  }

  public static <P extends Data<P>> DataParser<P, ExtendableBitmap> EBMP(String id, int bytes) {
    return new ExtendableBitmapParser<>(id, bytes);
  }
}
