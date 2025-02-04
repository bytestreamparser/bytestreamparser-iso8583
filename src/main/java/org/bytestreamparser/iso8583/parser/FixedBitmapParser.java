package org.bytestreamparser.iso8583.parser;

import static org.bytestreamparser.scalar.util.InputStreams.readFully;
import static org.bytestreamparser.scalar.util.Preconditions.check;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bytestreamparser.api.parser.DataParser;
import org.bytestreamparser.iso8583.data.FixedBitmap;

/** A parser for parsing a {@link FixedBitmap}. */
public class FixedBitmapParser extends DataParser<FixedBitmap> {
  private static final String ERROR_MESSAGE = "%s: value must be %d bytes, but got [%d]";
  private final int bytes;

  /**
   * Creates a new FixedBitmapParser.
   *
   * @param id the ID of the parser.
   * @param bytes the number of bytes in the bitmap.
   */
  public FixedBitmapParser(String id, int bytes) {
    super(id);
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
