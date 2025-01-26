package org.bytestreamparser.iso8583.parser;

import static org.bytestreamparser.scalar.util.Preconditions.check;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.bytestreamparser.api.parser.DataParser;
import org.bytestreamparser.iso8583.data.ExtendableBitmap;
import org.bytestreamparser.iso8583.data.FixedBitmap;

public class ExtendableBitmapParser extends DataParser<ExtendableBitmap> {
  private static final String ERROR_MESSAGE =
      "%s: value must be a multiple of %d bytes, but got [%d]";
  private final int bytes;
  private final FixedBitmapParser bitmapParser;

  public ExtendableBitmapParser(String id, int bytes) {
    super(id);
    this.bytes = bytes;
    bitmapParser = new FixedBitmapParser(id, bytes);
  }

  @Override
  public void pack(ExtendableBitmap extendableBitmap, OutputStream output) throws IOException {
    byte[] byteArray = extendableBitmap.toByteArray();
    check(byteArray.length % bytes == 0, ERROR_MESSAGE, getId(), bytes, byteArray.length);
    output.write(byteArray);
  }

  @Override
  public ExtendableBitmap parse(InputStream input) throws IOException {
    ExtendableBitmap extendableBitmap = new ExtendableBitmap(bytes);
    List<FixedBitmap> extensions = new ArrayList<>();
    FixedBitmap bitmap = bitmapParser.parse(input);
    extensions.add(bitmap);
    while (bitmap.get(1)) {
      bitmap = bitmapParser.parse(input);
      extensions.add(bitmap);
    }
    extendableBitmap.addExtensions(extensions);
    return extendableBitmap;
  }
}
