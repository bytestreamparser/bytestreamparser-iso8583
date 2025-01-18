package org.bytestreamparser.iso8583.util;

import org.bytestreamparser.api.data.Data;
import org.bytestreamparser.api.parser.DataParser;
import org.bytestreamparser.scalar.parser.UnsignedByteParser;
import org.bytestreamparser.scalar.parser.UnsignedShortParser;

public final class IntegerParsers {
  private IntegerParsers() {}

  public static <P extends Data<P>> DataParser<P, Integer> ubyte(String id) {
    return new UnsignedByteParser<>(id);
  }

  public static <P extends Data<P>> DataParser<P, Integer> ushort(String id) {
    return new UnsignedShortParser<>(id);
  }
}
