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

  /**
   * Inserts a serialized field value into the database.
   *
   * @param field Field's name.
   *   Corresponds directly to the {@code field_name} column in the {@code suggestion_fields} table.
   * @param value Field's value in it's serialized form.
   *   Corresponds directly to the {@code field_value} column in the {@code suggestion_fields} table.
   */
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

  /**
   * Stores a field with a {@link String} value in the database.
   *
   * The database stores the values as strings, so no conversion
   * is needed and the value can be stored as-is.
   */
  @Override
  public void visitString(String name, String value) {
    insertField(name, value);
  }
}

/**
 * Data provider for temporarily storing the serialized field values in memory.
 */
class StringDataProvider implements SuggestionDataProvider {
  /**
   * Map from field names to serialized field values.
   */
  private HashMap<String, String> fields = new HashMap<>();

  /**
   * Clears the stored fields, returning the instance to it's initial state.
   */
  public void clear() {
    fields.clear();
  }

  /**
   * Stores a field's serialized value in the internal buffer.
   *
   * @param name Field's name
   * @param value Field's value, in serialized form.
   */
  public void setField(String name, String value) {
    fields.put(name, value);
  }

  /**
   * Gets a field's value as a string.
   *
   * If no value for the specified field is stored, returns an empty optional.
   */
  public Optional<String> getString(String name) {
    return Optional.ofNullable(fields.get(name));
  }
}

/**
 * {@link SuggestionDao} implementation which uses a relational database as it's backend.
 *
 * Most databases with JDBC drivers should work, but at least SQLite is quaranteed to work.
 *
 * <h2>Database Tables</h2>
 *
 * <h3><code>suggestions</code></h3>
 *
 * The {@code suggestions} table contains a single row for each stored {@link Suggestion}.
 * The task of generating identifiers for the suggestions is offloaded to the database
 * implementation, and this table's {@code suggestion_id} column is used to generate the
 * identifiers.
 *
 * Note that this table contains only implementation-specific information -- all user
 * provided data is stored in the {@code suggestion_fields} table.
 *
 * <blockquote>
 * <table summary="Schema of the suggestions database table" cellspacing=3>
 *   <tr style="background-color: #ccccff; font-family: sans-serif">
 *     <th>Column Name</th>
 *     <th>Column Type</th>
 *     <th>Description</th>
 *   </tr>
 *   <tr>
 *     <td>suggestion_id</td>
 *     <td><code><b>INTEGER</b> PRIMARY KEY AUTOINCREMENT NOT NULL</code></td>
 *     <td>Sequential numerical unique identifier of the suggestion.</td>
 *   </tr>
 *   <tr>
 *     <td>kind</td>
 *     <td><code><b>TEXT</b> NOT NULL</code></td>
 *     <td>
 *       String denoting the kind of the suggestion.
 *       See {@link Suggestion#create} for list of all known suggestion kinds.
 *     </td>
 *   </tr>
 * </table>
 * </blockquote>
 *
 * <h3><code>suggestion_fields</code></h3>
 *
 * This table stores the actual details of suggestions as key-value pairs.
 * The values are converted to text before storing them. No metadata about
 * the original type of the value is stored and so the retriever is given
 * the responsibility of knowing how to interpret the values.
 *
 * <blockquote>
 * <table summary="Schema of the suggestion_fields database table" cellspacing=3 style="vertical-align: top">
 *   <tr style="background-color: #ccccff; font-family: sans-serif">
 *     <th>Column Name</th>
 *     <th>Column Type</th>
 *     <th>Description</th>
 *   </tr>
 *   <tr>
 *     <td>suggestion_id</td>
 *     <td><code><b>INTEGER</b> NOT NULL</code></td>
 *     <td>
 *       Identifier of the {@link Suggestion} this field belongs to.
 *       Foreign key reference to the {@code suggestion_id} field in the {@code suggestions} table.
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>field_name</td>
 *     <td><code><b>TEXT</b> NOT NULL</code></td>
 *     <td>Textual (non-user-facing) name which is used to identify the field.</td>
 *   </tr>
 *   <tr>
 *     <td>field_value</td>
 *     <td><code><b>TEXT</b> NOT NULL</code></td>
 *     <td>Value of the field, serialised into a string.</td>
 *   </tr>
 * </table>
 * </blockquote>
 */
public class JDBCSuggestionDao implements SuggestionDao {
  private Connection connection;

  JDBCSuggestionDao(Connection connection) {
    this.connection = connection;
  }

  /**
   * Creates the neccessary database tables at startup if neccessary.
   */
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

  /**
   * Saves a new suggestion to the database.
   *
   * A new unique ID is generated for the suggestion during the operation.
   * Inserts a single row into the {@code suggestions} table and inserts
   * the suggestion's details as key-value pairs to the {@code suggestion_fields}
   * table.
   *
   * @param suggestion The suggestion to be stored.
   *   The referenced instance's ID is altered during this operation.
   */
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

  /**
   * Gets a list of all suggestions from the database and populates them.
   */
  public List<Suggestion> getSuggestions() {
    ArrayList<Suggestion> suggestions = new ArrayList<>();

    try {
      ResultSet rows = connection
        .createStatement()
        .executeQuery(
          "SELECT suggestions.suggestion_id, kind, field_name, field_value " +
          "FROM suggestions " +
          "LEFT JOIN suggestion_fields " +
          "  ON suggestions.suggestion_id = suggestion_fields.suggestion_id"
        );

      Integer id = null;
      String kind = null;
      StringDataProvider data = new StringDataProvider();

      while (rows.next()) {
        Integer new_id = rows.getInt("suggestion_id");
        String new_kind = rows.getString("kind");

        // This branch is executed whenever a suggestion's
        // all fields have been ingested into `data`.
        if (id != new_id && id != null) {
          suggestions.add(Suggestion.create(kind, data));
          data.clear();
        }

        id = new_id;
        kind = new_kind;

        data.setField(rows.getString("field_name"), rows.getString("field_value"));
      }
    } catch (SQLException sqle) {
      sqle.printStackTrace();
    }

    return suggestions;
  }
}
