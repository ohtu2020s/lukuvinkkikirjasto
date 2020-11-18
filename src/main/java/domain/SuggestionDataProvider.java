package ohtu.domain;

import java.util.Optional;

public interface SuggestionDataProvider {
  Optional<String> getString(String name);
}
