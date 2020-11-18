package ohtu.domain;

/**
 * Class for iterating through {@link Suggestion Suggestions'} fields and their values.
 *
 * Designed to be subclassed. The default implementations for the methods do nothing.
 */
public class SuggestionVisitor {
  /**
   * Called for each field with type {@link String}.
   */
  void visitString(String name, String value) {}
}
