package org.bytestreamparser.iso8583.parser;

import org.bytestreamparser.api.parser.DataParser;
import org.bytestreamparser.composite.data.DataObject;
import org.bytestreamparser.composite.parser.DataFieldParser;
import org.bytestreamparser.iso8583.data.IsoMessage;

public class IsoFieldParser<P extends DataObject<P> & IsoMessage, V> extends DataFieldParser<P, V> {
  public IsoFieldParser(int id, DataParser<V> fieldParser) {
    super(String.valueOf(id), fieldParser, message -> message.hasDataField(id));
  }
}
