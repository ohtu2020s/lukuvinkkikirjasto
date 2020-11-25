package ohtu.domain;

import java.util.ArrayList;

class VisitorHandler<T> {
  Class<T> valueClass;
  Callback<T> callback;

  static interface Callback<T> {
    void call(SuggestionFieldValue<T> field);
  }

  VisitorHandler(Class<T> vc, Callback<T> cb) {
    valueClass = vc;
    callback = cb;
  }

  boolean tryHandle(SuggestionFieldValue<?> field) {
    SuggestionFieldValue<T> converted = null;

    try {
      converted = field.cast(valueClass);
    } catch (ClassCastException cce) {
      return false;
    }

    callback.call(converted);
    return true;
  }
}

/**
 * Interface for iterating through {@link Suggestion Suggestions'} fields and their values.
 *
 * The default implementations for the methods do nothing.
 */
public interface SuggestionVisitor {
  /**
   * Called for each field with type {@link String}.
   */
  default void visitString(SuggestionFieldValue<String> field) {}

  default void visitInteger(SuggestionFieldValue<Integer> field) {}

  default void visit(SuggestionFieldValue<?> field) {
    ArrayList<VisitorHandler<?>> handlers = new ArrayList<>();

    handlers.add(new VisitorHandler<>(String.class, this::visitString));
    handlers.add(new VisitorHandler<>(Integer.class, this::visitInteger));

    for (VisitorHandler<?> handler : handlers) {
      if (handler.tryHandle(field)) {
        return;
      }
    }

    throw new IllegalArgumentException(
      String.format("field '%s' has unsupported type %s", field.getName(), field.getType())
    );
  }
}
