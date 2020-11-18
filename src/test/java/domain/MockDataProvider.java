package ohtu.domain;

import java.util.HashMap;
import java.util.Optional;

public class MockDataProvider implements SuggestionDataProvider {
  private HashMap<String, String> stringValues = new HashMap<>();

  public void setString(String name, String value) {
    stringValues.put(name, value);
  }

  public Optional<String> getString(String name) throws IllegalArgumentException {
    return Optional.ofNullable(stringValues.get(name));
  }
}
