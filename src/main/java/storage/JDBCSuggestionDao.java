package ohtu.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import ohtu.domain.Suggestion;
import ohtu.domain.SuggestionVisitor;

class SuggestionFieldInsertor extends SuggestionVisitor {
  private Connection connection;
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
    try {
      connection
        .createStatement()
        .execute(
          "CREATE TABLE IF NOT EXISTS suggestion_fields (" +
          "  suggestion_id INTEGER NOT NULL," +
          "  field_name TEXT NOT NULL," +
          "  field_value TEXT NOT NULL" +
          ")"
        );
    } catch (SQLException sqle) {
      sqle.printStackTrace();
    }
  }

  public void saveSuggestion(Suggestion suggestion) {
    suggestion.visit(new SuggestionFieldInsertor(connection, suggestion));
  }
}
