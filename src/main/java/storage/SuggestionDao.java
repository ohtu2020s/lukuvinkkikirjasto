package ohtu.storage;

import java.util.List;

import ohtu.domain.Suggestion;

public interface SuggestionDao {
  default void setup() {}
  void saveSuggestion(Suggestion suggestion);

  /**
   * Retrieves a list of all stored {@link Suggestion Suggestions}.
   *
   * @return A list of all stored {@link Suggestion Suggestions}.
   */
  List<Suggestion> getSuggestions();
}
