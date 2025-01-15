package org.bytestreamparser.iso8583.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.bytestreamparser.api.testing.extension.RandomParametersExtension;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension.Randomize;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(RandomParametersExtension.class)
class MessageTypeIndicatorTest {
  @Test
  void valid_mti(@Randomize(intMin = 0, intMax = 10000) int value) {
    assertThatNoException()
        .isThrownBy(() -> new MessageTypeIndicator(String.format("%04d", value)));
  }

  @Test
  void invalid_mti(@Randomize String value) {
    assertThatThrownBy(() -> new MessageTypeIndicator(value))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Invalid MTI: [%s]", value);
  }

  @Test
  void to_string(@Randomize(intMin = 0, intMax = 10000) int value) {
    String expected = String.format("%04d", value);
    MessageTypeIndicator mti = new MessageTypeIndicator(expected);
    assertThat(mti).hasToString(expected);
  }
}
