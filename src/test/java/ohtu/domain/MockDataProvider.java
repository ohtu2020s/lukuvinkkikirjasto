package ohtu.domain;

import java.util.HashMap;
import java.util.Optional;

/**
 * Mock implementation of {@link SuggestionDataProvider} for testing purposes.
 */
public class MockDataProvider implements SuggestionDataProvider {
  private HashMap<String, String> stringValues = new HashMap<>();

  /**
   * Stores a {@link String} value for a field.
   *
   * @param name Name of the field.
   * @param value {@link String} value for the field.
   */
  public void setString(String name, String value) {
    stringValues.put(name, value);
  }

  /** {@inheritDoc} */
  public Optional<String> getString(String name) throws IllegalArgumentException {
    return Optional.ofNullable(stringValues.get(name));
  }
}
