package ohtu.domain;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class SuggestionDataProviderTest {
  @Test
  void dataProviderReturnsEmptyOptionalsByDefault() {
    SuggestionDataProvider data = new SuggestionDataProvider() {};

    assertFalse(data.getString("field").isPresent());
    assertFalse(data.getInteger("field").isPresent());
  }
}
