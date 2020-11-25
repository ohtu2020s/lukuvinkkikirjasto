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
      public void visitString(SuggestionFieldValue<String> field) {
        if (field.getName().equals("isbn")) {
          assertEquals(field.getValue(), "isbn");
          isbnVisited[0] = true;
        }
      }
    });

    assertTrue(isbnVisited[0]);
  }

  @Nested
  class CreationTests {
    MockDataProvider mock;

    @BeforeEach
    void initializeMock() {
      mock = new MockDataProvider();
    }

    @Test
    void classOfTheBuiltInstanceMatchesTheKindWhenItsBook() {
      Suggestion sugg = SuggestionFactory.create(BookSuggestion.KIND, mock);
      assertTrue(sugg instanceof BookSuggestion);
    }

    @Test
    void bookSuggestionIsbnIsSetCorrectly() {
      mock.setString("isbn", "978-1451673319");
      BookSuggestion sugg = (BookSuggestion) SuggestionFactory.create(BookSuggestion.KIND, mock);
      assertEquals(sugg.getIsbn(), "978-1451673319");
    }
  }
}

