package ohtu.storage;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import ohtu.domain.BookSuggestion;
import ohtu.domain.Suggestion;

public abstract class SuggestionDAOTest<T extends SuggestionDao> {
  SuggestionDao dao;

  abstract SuggestionDao createSuggestionDao();

  BookSuggestion createBookSuggestion() {
    BookSuggestion sugg = new BookSuggestion();
    sugg.setTitle("Infinite Jest");
    sugg.setAuthor("David Foster Wallace");
    sugg.setIsbn("9780316920049");
    return sugg;
  }

  @BeforeEach
  void initializeDao() {
    dao = createSuggestionDao();
    dao.setup();
  }

  @Test
  void suggestionCanBeRetrieved() {
    BookSuggestion sugg = createBookSuggestion();
    dao.saveSuggestion(sugg);

    List<Suggestion> suggestions = dao.getSuggestions();

    assertEquals(1, suggestions.size());
  }

  @Test
  void commonSuggestionFieldsAreRetrievedCorrectly() {
    BookSuggestion sugg = createBookSuggestion();
    dao.saveSuggestion(sugg);

    List<Suggestion> suggestions = dao.getSuggestions();
    Suggestion retrieved = suggestions.get(0);

    assertEquals(sugg.getTitle(), retrieved.getTitle());
    assertEquals(sugg.getAuthor(), retrieved.getAuthor());
  }

  @Test
  void retrievedSuggestionIsOfTheCorrectSubClass() {
    BookSuggestion sugg = createBookSuggestion();

    dao.saveSuggestion(sugg);

    List<Suggestion> suggestions = dao.getSuggestions();
    Suggestion retrieved = suggestions.get(0);

    assertTrue(retrieved instanceof BookSuggestion);
  }

  @Test
  void retrievedSuggestionHasCorrectSubclassFieldValues() {
    BookSuggestion sugg = createBookSuggestion();
    dao.saveSuggestion(sugg);

    List<Suggestion> suggestions = dao.getSuggestions();
    BookSuggestion retrieved = (BookSuggestion) suggestions.get(0);

    assertEquals(sugg.getIsbn(), retrieved.getIsbn());
  }
}
