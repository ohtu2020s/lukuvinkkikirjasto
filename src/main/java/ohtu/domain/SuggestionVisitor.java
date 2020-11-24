package ohtu.domain;

/**
 * Interface for iterating through {@link Suggestion Suggestions'} fields and their values.
 *
 * The default implementations for the methods do nothing.
 */
public interface SuggestionVisitor {
  /**
   * Called for each field with type {@link String}.
   *
   * @param name Name of the field.
   * @param value Value of the field.
   */
  default void visitString(String name, String value) {}
}
