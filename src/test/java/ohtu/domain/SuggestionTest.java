package ohtu.domain;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

public class SuggestionTest {
  @Test
  void suggestionKindIsReturnedCorrectly() {
    assertEquals("BOOK", new BookSuggestion().getKind());
  }

  private static class NoKind extends Suggestion {
    String nonSuggestionField;

    @Override
    public String toString() {
      return "NoKind";
    }
  }

  @Test
  void suggestionKindReturnsNullIfNotDefined() {
    assertNull(Suggestion.getKind(NoKind.class));
  }

  @Test
  void nonSuggestionFieldsAreNotVisited() {
    final int[] visited = { 0 };

    new NoKind().visit(new SuggestionVisitor() {
      @Override
      public void visit(SuggestionFieldValue<?> field) {
        visited[0]++;
      }
    });

    assertEquals(7, visited[0]);
  }

  @Test
  void suggestionIsVisitedCorrectly() {
    Suggestion sugg = new BookSuggestion();

    sugg.setTitle("title");
    sugg.setAuthor("author");

    sugg.visit(new SuggestionVisitor() {
      public void visitString(String name, String value) {
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
      Suggestion sugg = SuggestionFactory.create(kind, mock);
      assertEquals(sugg.getTitle(), "Farenheit 451");
    }

    @ParameterizedTest
    @ValueSource(strings = { "BOOK" })
    void suggestionAuthorIsSetCorrectly(String kind) {
      mock.setString("author", "Ray Bradbury");
      Suggestion sugg = SuggestionFactory.create(kind, mock);
      assertEquals(sugg.getAuthor(), "Ray Bradbury");
    }
  }
}
