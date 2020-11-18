package ohtu.domain;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

public class SuggestionFactoryTest {
  MockDataProvider mock;

  @BeforeEach
  void initializeMock() {
    mock = new MockDataProvider();
  }

  @ParameterizedTest
  @ValueSource(strings = { "BOOK" })
  void suggestionTitleIsSetCorrectly(String kind) {
    mock.setString("title", "Farenheit 451");
    Suggestion sugg = SuggestionFactory.build(kind, mock);
    assertEquals(sugg.getTitle(), "Farenheit 451");
  }

  @ParameterizedTest
  @ValueSource(strings = { "BOOK" })
  void suggestionAuthorIsSetCorrectly(String kind) {
    mock.setString("author", "Ray Bradbury");
    Suggestion sugg = SuggestionFactory.build(kind, mock);
    assertEquals(sugg.getAuthor(), "Ray Bradbury");
  }

  @Test
  void classOfTheBuiltInstanceMatchesTheKindWhenItsBook() {
    Suggestion sugg = SuggestionFactory.build(BookSuggestion.KIND, mock);
    assertTrue(sugg instanceof BookSuggestion);
  }

  @Test
  void bookSuggestionIsbnIsSetCorrectly() {
    mock.setString("isbn", "978-1451673319");
    BookSuggestion sugg = (BookSuggestion) SuggestionFactory.build(BookSuggestion.KIND, mock);
    assertEquals(sugg.getIsbn(), "978-1451673319");
  }
}
