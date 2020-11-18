package ohtu.domain;

import java.util.HashMap;

public class MockDataProvider implements SuggestionDataProvider {
  private HashMap<String, String> stringValues = new HashMap<>();

  public void setString(String name, String value) {
    stringValues.put(name, value);
  }

  public String getString(String name) throws IllegalArgumentException {
    String value = stringValues.get(name);

    if (value == null)
      return "";

    return value;
  }
}
