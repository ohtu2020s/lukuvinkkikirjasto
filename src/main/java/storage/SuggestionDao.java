package ohtu.storage;

import ohtu.domain.Suggestion;

public interface SuggestionDao {
  default void setup() {}
  void saveSuggestion(Suggestion suggestion);
}
