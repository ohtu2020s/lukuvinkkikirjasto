package ohtu.storage;

import java.util.List;

import ohtu.domain.Suggestion;

/**
 * Inteface for {@link Suggestion} Data Access Objects, which allow the program to
 * store, retrieve, modify and delete {@link Suggestion Suggestions} without
 * needing to care about the specific storage implementation.
 */
public interface SuggestionDao {
  /**
   * Perform any operations neccessary before the storage implementation can be used.
   *
   * The default implementation is a no-op.
   */
  default void setup() {}

  /**
   * Save a <i>new</i> suggestion.
   *
   * This operation generates an unique identifier for the suggestion and
   * assigns it to the provided instance. This is done regardless of whether
   * the {@link Suggestion} is already stored in the backend, resulting in a
   * duplicate entry.
   *
   * @param suggestion A new suggestion to be saved.
   */
  void saveSuggestion(Suggestion suggestion);

  /**
   * Retrieves a list of all stored {@link Suggestion Suggestions}.
   *
   * @return A list of all stored {@link Suggestion Suggestions}.
   */
  List<Suggestion> getSuggestions();

  Suggestion getSuggestionById(int id);

  void updateSuggestion(Suggestion id);
}
