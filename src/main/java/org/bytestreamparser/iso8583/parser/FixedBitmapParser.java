package org.bytestreamparser.iso8583.parser;

import static org.bytestreamparser.api.util.Predicates.alwaysTrue;
import static org.bytestreamparser.scalar.util.InputStreams.readFully;
import static org.bytestreamparser.scalar.util.Preconditions.check;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Predicate;
import org.bytestreamparser.api.data.Data;
import org.bytestreamparser.api.parser.DataParser;
import org.bytestreamparser.iso8583.data.FixedBitmap;

public class FixedBitmapParser<P extends Data<P>> extends DataParser<P, FixedBitmap> {
  public static final String ERROR_MESSAGE = "%s: value must be %d bytes, but got [%d]";
  private final int bytes;

  public FixedBitmapParser(String id, int bytes) {
    this(id, alwaysTrue(), bytes);
  }

  public FixedBitmapParser(String id, Predicate<P> applicable, int bytes) {
    super(id, applicable);
    this.bytes = bytes;
  }

  @Override
  public void pack(FixedBitmap bitmap, OutputStream output) throws IOException {
    byte[] byteArray = bitmap.toByteArray();
    check(byteArray.length == bytes, ERROR_MESSAGE, getId(), bytes, byteArray.length);
    output.write(byteArray);
  }

  @Override
  public FixedBitmap parse(InputStream input) throws IOException {
    return FixedBitmap.valueOf(readFully(input, bytes));
  }
}
