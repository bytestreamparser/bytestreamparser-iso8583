package org.bytestreamparser.iso8583.data;

/** Interface for ISO messages. */
public interface IsoMessage {
  /**
   * Returns whether the message has a data field with the given ID.
   *
   * @param id the ID of the data field.
   * @return {@code true} if the message has a data field with the given ID, {@code false}
   *     otherwise.
   */
  boolean hasDataField(int id);
}
