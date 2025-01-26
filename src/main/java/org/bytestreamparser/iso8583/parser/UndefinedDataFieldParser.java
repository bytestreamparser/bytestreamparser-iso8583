package org.bytestreamparser.iso8583.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bytestreamparser.api.parser.DataParser;

public class UndefinedDataFieldParser extends DataParser<Object> {
  public UndefinedDataFieldParser(String id) {
    super(id);
  }

  @Override
  public void pack(Object value, OutputStream output) throws IOException {
    throw getException();
  }

  @Override
  public Object parse(InputStream input) throws IOException {
    throw getException();
  }

  private UnsupportedOperationException getException() {
    return new UnsupportedOperationException(String.format("%s: Undefined data field", getId()));
  }
}
