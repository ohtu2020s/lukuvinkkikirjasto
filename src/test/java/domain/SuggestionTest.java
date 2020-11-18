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

  @Nested
  class CreationTests {
    MockDataProvider mock;

    @BeforeEach
    void initializeMock() {
      mock = new MockDataProvider();
    }

    @ParameterizedTest
    @ValueSource(strings = { "BOOK" })
    void suggestionTitleIsSetCorrectly(String kind) {
      mock.setString("title", "Farenheit 451");
      Suggestion sugg = Suggestion.create(kind, mock);
      assertEquals(sugg.getTitle(), "Farenheit 451");
    }

    @ParameterizedTest
    @ValueSource(strings = { "BOOK" })
    void suggestionAuthorIsSetCorrectly(String kind) {
      mock.setString("author", "Ray Bradbury");
      Suggestion sugg = Suggestion.create(kind, mock);
      assertEquals(sugg.getAuthor(), "Ray Bradbury");
    }
  }
}
