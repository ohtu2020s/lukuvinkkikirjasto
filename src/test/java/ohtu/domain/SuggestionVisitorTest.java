package ohtu.domain;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class SuggestionVisitorTest {
  class WithUnsupportedFieldType extends Suggestion {
    @SuggestionField
    Suggestion subSuggestion;

    @Override
    public String toString() {
      return "WithUnsupportedFieldType";
    }
  }

  @Test
  void visitingTypeWithFieldOfUnsupportedTypeThrows() {
    assertThrows(IllegalArgumentException.class, () -> {
      new WithUnsupportedFieldType().visit(new SuggestionVisitor() {});
    });
  }
}
