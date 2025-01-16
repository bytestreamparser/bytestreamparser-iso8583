package org.bytestreamparser.iso8583.data;

import org.bytestreamparser.api.data.Data;

public interface IsoMessage<T extends IsoMessage<T>> extends Data<T> {
  Bitmap getBitmap();

  default boolean hasDataField(int id) {
    return getBitmap().get(id);
  }
}
