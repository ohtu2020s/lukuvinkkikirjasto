package ohtu.storage;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.javatuples.Pair;

import ohtu.domain.Suggestion;
import ohtu.domain.SuggestionFactory;
import ohtu.domain.SuggestionFieldValue;
import ohtu.domain.SuggestionDataProvider;
import ohtu.domain.SuggestionVisitor;

/**
 * Temporary in-memory storage for Suggestion field values.
 *
 * Implements both {@link SuggestionVisitor} and {@link SuggestionDataProvider}
 * interfaces, so that {@link Suggestion}'s fields can be stored in an instance
 * of this class <i>and</i> a {@link Suggestion} can be populated with the
 * values stored in an instance.
 */
class SuggestionFieldHashMap implements SuggestionVisitor, SuggestionDataProvider {
  /**
   * Map from field names to Java Objects.
   */
  private HashMap<String, Object> fields = new HashMap<>();

  /**
   * Gets a String value for a field.
   *
   * @param field Name of the field.
   *
   * @return An optional containing the {@link String} value if one exists for a field
   *   with the provided name. If there is no value for the field, or the value is of some
   *   other type than {@link String}, returns an empty Optional.
   */
  public Optional<String> getString(String field) {
    Object value = fields.get(field);

    if (value != null && value instanceof String)
      return Optional.of((String) value);

    return Optional.empty();
  }

  /**
   * Stores the value of a String field in the internal collection.
   */
  @Override
  public void visitString(SuggestionFieldValue<String> field) {
    fields.put(field.getName(), field.getValue());
  }
}

/**
 * In-memory implementation of {@link SuggestionDao}.
 *
 * Suggestions stored using this implementation do not persist
 * across executions. They are simply stored as Java objects in
 * an in-memory data structure.
 */
public class InMemorySuggestionDao implements SuggestionDao {
  /**
   * Map from Suggestion indentifiers to the Suggestion objects themselves.
   */
  private HashMap<Integer, Pair<String, SuggestionFieldHashMap>> data = new HashMap<>();

  /**
   * Counter for generating new identifiers for Suggestions.
   */
  private int idCounter = 0;

  /**
   * Saves the suggestion and assigns it a new identifier.
   */
  public void saveSuggestion(Suggestion suggestion) {
    SuggestionFieldHashMap collector = new SuggestionFieldHashMap();
    suggestion.visit(collector);

    suggestion.setId(idCounter);
    idCounter += 1;

    data.put(suggestion.getId(), Pair.with(suggestion.getKind(), collector));
  }

  /**
   * Gets a list of all stored Suggestions.
   */
  public List<Suggestion> getSuggestions() {
    return data.values()
      .stream()
      .map(pair -> SuggestionFactory.create(pair.getValue0(), pair.getValue1()))
      .collect(Collectors.toList());
  }

  public Suggestion getSuggestionById(int id) {
    Pair<String, SuggestionFieldHashMap> pair = data.get(id);

    if (pair == null) {
      return null;
    }

    return SuggestionFactory.create(pair.getValue0(), pair.getValue1());
  }

  public void updateSuggestion(Suggestion suggestion) {
    Pair<String, SuggestionFieldHashMap> pair = data.get(suggestion.getId());

    if (pair != null) {
      suggestion.visit(pair.getValue1());
    }
  }
}
