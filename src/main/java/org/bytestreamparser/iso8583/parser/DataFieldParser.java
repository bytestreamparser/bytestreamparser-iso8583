package org.bytestreamparser.iso8583.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bytestreamparser.api.parser.DataParser;
import org.bytestreamparser.iso8583.data.IsoMessage;

public class DataFieldParser<P extends IsoMessage<P>, V> extends DataParser<P, V> {
  private final DataParser<?, V> fieldParser;

  public DataFieldParser(int id, DataParser<?, V> fieldParser) {
    super(String.valueOf(id), message -> message.hasDataField(id));
    this.fieldParser = fieldParser;
  }

  @Override
  public void pack(V value, OutputStream output) throws IOException {
    fieldParser.pack(value, output);
  }

  @Override
  public V parse(InputStream input) throws IOException {
    return fieldParser.parse(input);
  }
}
