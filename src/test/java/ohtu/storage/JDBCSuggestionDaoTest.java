package ohtu.storage;

import ohtu.domain.BookSuggestion;
import ohtu.domain.Suggestion;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class JDBCSuggestionDaoTest extends SuggestionDAOTest<JDBCSuggestionDao> {
  Connection connection;

  @Override
  SuggestionDao createSuggestionDao() {
    try {
      connection = DriverManager.getConnection("jdbc:sqlite::memory:");
    } catch (SQLException sqle) {
    }

    return new JDBCSuggestionDao(connection);
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
    assertEquals(8, results.getInt(1));
  }
}
