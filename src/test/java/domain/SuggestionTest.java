package ohtu.domain;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

public class SuggestionTest {
  @Test
  void suggestionIsVisitedCorrectly() {
    Suggestion sugg = new BookSuggestion();

    sugg.setTitle("title");
    sugg.setAuthor("author");

    sugg.visit(new SuggestionVisitor() {
      void visitString(String name, String value) {
        if (name.equals("title"))
          assertEquals(value, "title");
        else if (name.equals("author"))
          assertEquals(value, "author");
      }
    });
  }
}
