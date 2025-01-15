package org.bytestreamparser.iso8583.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bytestreamparser.api.data.Data;
import org.bytestreamparser.api.parser.DataParser;
import org.bytestreamparser.iso8583.data.MessageTypeIndicator;
import org.bytestreamparser.scalar.parser.StringParser;

public class MtiParser<P extends Data<P>> extends DataParser<P, MessageTypeIndicator> {
  private final StringParser<P> parser;

  public MtiParser(StringParser<P> parser) {
    super(parser.getId(), parser::applicable);
    this.parser = parser;
  }

  @Override
  public void pack(MessageTypeIndicator mti, OutputStream output) throws IOException {
    parser.pack(mti.toString(), output);
  }

  @Override
  public MessageTypeIndicator parse(InputStream input) throws IOException {
    return new MessageTypeIndicator(parser.parse(input));
  }
}
