package org.bytestreamparser.iso8583.data;

import org.bytestreamparser.composite.data.DataObject;

public interface IsoMessage<T extends IsoMessage<T>> extends DataObject<T> {
  Bitmap getBitmap();

  default boolean hasDataField(int id) {
    return getBitmap().get(id);
  }
}
