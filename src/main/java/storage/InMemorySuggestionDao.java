package ohtu.storage;

import java.util.HashMap;
import ohtu.domain.Suggestion;

public class InMemorySuggestionDao implements SuggestionDao {
  private HashMap<Integer, Suggestion> data = new HashMap<>();
  private int idCounter = 0;

  public void saveSuggestion(Suggestion suggestion) {
    suggestion.setId(idCounter);
    idCounter += 1;

    data.put(suggestion.getId(), suggestion);
  }
}
