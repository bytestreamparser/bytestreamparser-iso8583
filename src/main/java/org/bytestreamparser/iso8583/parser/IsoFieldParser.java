package org.bytestreamparser.iso8583.parser;

import org.bytestreamparser.api.parser.DataParser;
import org.bytestreamparser.composite.data.DataObject;
import org.bytestreamparser.composite.parser.DataFieldParser;
import org.bytestreamparser.iso8583.data.IsoMessage;

/**
 * Parser for ISO fields.
 *
 * @param <D> the type of the ISO message.
 * @param <V> the type of the field value.
 */
public class IsoFieldParser<D extends DataObject<D> & IsoMessage, V> extends DataFieldParser<D, V> {
  /**
   * Creates a new ISO field parser.
   *
   * @param id the ID of the field.
   * @param fieldParser the parser for the field value.
   */
  public IsoFieldParser(int id, DataParser<V> fieldParser) {
    super(String.valueOf(id), fieldParser, message -> message.hasDataField(id));
  }
}
