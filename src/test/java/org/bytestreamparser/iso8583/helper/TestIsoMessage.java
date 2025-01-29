package org.bytestreamparser.iso8583.helper;

import java.util.HashMap;
import org.bytestreamparser.composite.data.AbstractDataObject;
import org.bytestreamparser.iso8583.data.IsoMessage;

public class TestIsoMessage extends AbstractDataObject<TestIsoMessage> implements IsoMessage {
  public TestIsoMessage() {
    super(new HashMap<>());
  }

  @Override
  public boolean hasDataField(int id) {
    return fields().contains(String.valueOf(id));
  }
}
