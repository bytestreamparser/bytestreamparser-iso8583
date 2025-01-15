package org.bytestreamparser.iso8583.data;

import static org.bytestreamparser.scalar.util.Preconditions.check;

import java.util.regex.Pattern;

public class MessageTypeIndicator {
  private static final Pattern PATTERN = Pattern.compile("^\\d{4}$");
  private final String value;

  public MessageTypeIndicator(String value) {
    check(PATTERN.matcher(value).matches(), "Invalid MTI: [%s]", value);
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }
}
