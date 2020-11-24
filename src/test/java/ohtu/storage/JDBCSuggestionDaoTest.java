package ohtu.storage;

import ohtu.domain.BookSuggestion;
import ohtu.domain.Suggestion;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.List;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class JDBCSuggestionDaoTest {
  Connection connection;
  JDBCSuggestionDao dao;

  private BookSuggestion createBookSuggestion() {
    BookSuggestion sugg = new BookSuggestion();
    sugg.setTitle("Infinite Jest");
    sugg.setAuthor("David Foster Wallace");
    sugg.setIsbn("9780316920049");
    return sugg;
  }

  @BeforeEach
  void createSqliteDatabase() throws SQLException {
    connection = DriverManager.getConnection("jdbc:sqlite::memory:");
    dao = new JDBCSuggestionDao(connection);
    dao.setup();
  }

  @Test
  void bookSuggestionIsSavedWithoutExceptions() {
    dao.saveSuggestion(createBookSuggestion());
  }

  @Test
  void creatingSuggestionInsertsCorrectNumberOfRows() throws SQLException {
    BookSuggestion sugg = createBookSuggestion();
    dao.saveSuggestion(sugg);

    ResultSet results = connection.createStatement()
      .executeQuery("SELECT COUNT(*) FROM suggestions");
    assertEquals(1, results.getInt(1));

    results = connection.createStatement()
      .executeQuery("SELECT COUNT(*) FROM suggestion_fields");
    assertEquals(3, results.getInt(1));
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
