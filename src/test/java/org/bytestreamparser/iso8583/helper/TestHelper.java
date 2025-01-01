package org.bytestreamparser.iso8583.helper;

public class TestHelper {
  public static String toBinaryString(byte[] bytes) {
    StringBuilder builder = new StringBuilder();
    for (byte b : bytes) {
      builder.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)));
    }
    return builder.toString().replace(' ', '0');
  }
}
