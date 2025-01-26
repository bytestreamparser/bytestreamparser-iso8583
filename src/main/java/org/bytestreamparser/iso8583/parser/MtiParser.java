package org.bytestreamparser.iso8583.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bytestreamparser.api.parser.DataParser;
import org.bytestreamparser.iso8583.data.MessageTypeIndicator;

public class MtiParser extends DataParser<MessageTypeIndicator> {
  private final DataParser<String> parser;

  public MtiParser(DataParser<String> parser) {
    super(parser.getId());
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
