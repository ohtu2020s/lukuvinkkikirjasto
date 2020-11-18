package ohtu.domain;

public interface SuggestionDataProvider {
  String getString(String name) throws IllegalArgumentException;
  int getInteger(String name) throws IllegalArgumentException;
}
