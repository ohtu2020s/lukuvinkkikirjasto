package ohtu.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import ohtu.domain.Suggestion;
import ohtu.domain.SuggestionDataProvider;
import ohtu.domain.SuggestionVisitor;

/**
 * Implementation of {@link SuggestionVisitor} that stores the fields' values
 * as rows in the {@code suggestion_fields} table.
 */
class SuggestionFieldInsertor implements SuggestionVisitor {
  /**
   * Handle to the JDBC database connection.
   */
  private Connection connection;

  /**
   * The suggestion that is going to be stored.
   */
  private Suggestion suggestion;

  SuggestionFieldInsertor(Connection connection, Suggestion suggestion) {
    this.connection = connection;
    this.suggestion = suggestion;
  }

  private void insertField(String field, String value) {
    String sql = "INSERT INTO suggestion_fields (suggestion_id, field_name, field_value) VALUES (?, ?, ?)";

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, suggestion.getId());
      stmt.setString(2, field);
      stmt.setString(3, value);

      stmt.executeUpdate();
    } catch (SQLException sqle) {
      sqle.printStackTrace();
    }
  }

  @Override
  public void visitString(String name, String value) {
    insertField(name, value);
  }
}

public class JDBCSuggestionDao implements SuggestionDao {
  private Connection connection;

  JDBCSuggestionDao(Connection connection) {
    this.connection = connection;
  }

  @Override
  public void setup() {
    try (Statement stmt = connection.createStatement()) {
      stmt.execute(
        "CREATE TABLE IF NOT EXISTS suggestion_fields (" +
        "  suggestion_id INTEGER NOT NULL," +
        "  field_name TEXT NOT NULL," +
        "  field_value TEXT NOT NULL" +
        ")"
      );

      stmt.execute(
        "CREATE TABLE IF NOT EXISTS suggestions (" +
        "  suggestion_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
        "  kind TEXT NOT NULL" +
        ")"
      );
    } catch (SQLException sqle) {
      sqle.printStackTrace();
    }
  }

  public void saveSuggestion(Suggestion suggestion) {
    try {
      PreparedStatement stmt = connection
        .prepareStatement("INSERT INTO suggestions (kind) VALUES (?)");

      stmt.setString(1, suggestion.getKind());

      stmt.execute();

      suggestion.setId(stmt.getGeneratedKeys().getInt(1));
    } catch (SQLException sqle) {

      sqle.printStackTrace();
    }

    suggestion.visit(new SuggestionFieldInsertor(connection, suggestion));
  }
}
