package ohtu.domain;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

public class BookSuggestionTest {
  @Test
  void bookSuggestionIsVisitedCorrectly() {
    BookSuggestion sugg = new BookSuggestion();

    sugg.setIsbn("isbn");

    final boolean[] isbnVisited = { false };

    sugg.visit(new SuggestionVisitor() {
      void visitString(String name, String value) {
        if (name.equals("isbn")) {
          assertEquals(value, "isbn");
          isbnVisited[0] = true;
        }
      }
    });

    assertTrue(isbnVisited[0]);
  }
}

