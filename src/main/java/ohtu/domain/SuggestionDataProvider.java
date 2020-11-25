package ohtu.domain;

import java.util.Optional;

/**
 * An interface for providing values for {@link Suggestion Suggestion's} fields.
 */
public interface SuggestionDataProvider {
  /**
   * Get the value for a field of type {@link String}.
   *
   * @param name Name of the field.
   *
   * @return Value of the field, or empty optional if no such field exists
   *    or it's value cannot be converted into a {@link String}.
   */
  default Optional<String> getString(String name) {
      return Optional.empty();
  }

  default Optional<Integer> getInteger(String name) {
      return Optional.empty();
  }
}
