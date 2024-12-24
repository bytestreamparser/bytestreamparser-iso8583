package org.bytestreamparser.iso8583.parser;

import static com.github.lyang.iso8583.core.util.InputStreamUtil.readFully;
import static com.google.common.base.Preconditions.checkArgument;
import static org.bytestreamparser.scalar.util.InputStreams.readFully;

import com.github.lyang.iso8583.core.component.AbstractCompositeComponent;
import com.github.lyang.iso8583.core.component.ScalarComponent;
import com.github.lyang.iso8583.core.datatype.Bitmap;
import com.github.lyang.iso8583.core.util.PredicateUtil;
import com.google.common.primitives.Bytes;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Predicate;

public class BitmapParser<P extends AbstractCompositeComponent<P>>
    extends AbstractScalarComponentParser<P, Bitmap> {

  public static final String ERROR_MESSAGE =
      "%s: Expecting capacity to be less than or equal to %s, but got";
  private final int bytes;
  private final int extensions;

  public BitmapParser(String id, int bytes, int extensions) {
    this(id, bytes, extensions, PredicateUtil.alwaysTrue());
  }

  public BitmapParser(String id, int bytes, int extensions, Predicate<P> isApplicable) {
    super(id, isApplicable);
    this.bytes = bytes;
    this.extensions = extensions;
  }

  private static boolean hasExtension(byte[][] bits, int index) {
    return (bits[index - 1][0] & 0b10000000) > 0;
  }

  @Override
  public ScalarComponent<Bitmap> parse(InputStream input) throws IOException {
    byte[][] bits = new byte[1 + extensions][bytes];
    bits[0] = readFully(input, bytes);
    for (int index = 1; index <= extensions; index++) {
      if (hasExtension(bits, index)) {
        bits[index] = readFully(input, bytes);
      }
    }
    return new ScalarComponent<>(getId(), Bitmap.valueOf(bytes, Bytes.concat(bits)));
  }

  @Override
  protected byte[] pack(Bitmap value) {
    int max = bytes * Byte.SIZE * (1 + extensions);
    checkArgument(max >= value.capacity(), ERROR_MESSAGE, getId(), max, value.capacity());
    return value.toByteArray();
  }
}
