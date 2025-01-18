package org.bytestreamparser.iso8583.util;

import org.bytestreamparser.api.data.Data;
import org.bytestreamparser.scalar.parser.NumberParser;
import org.bytestreamparser.scalar.parser.UnsignedByteParser;
import org.bytestreamparser.scalar.parser.UnsignedShortParser;

public final class IntegerParsers {
  private IntegerParsers() {}

  public static <P extends Data<P>> NumberParser<P, Integer> ubyte(String id) {
    return new UnsignedByteParser<>(id);
  }

  public static <P extends Data<P>> NumberParser<P, Integer> ushort(String id) {
    return new UnsignedShortParser<>(id);
  }
}
