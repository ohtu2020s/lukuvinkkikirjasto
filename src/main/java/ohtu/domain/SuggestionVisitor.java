package ohtu.domain;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Utility class for the default implementation of {@link SuggestionVisitor#visit}.
 *
 * This class is used to try and convert a {@link SuggestionFieldValue} into a type
 * {@code T}. If the conversion is successfull, the given callback is executed. 
 */
class VisitorHandler<T> {
  /**
   * Type into which conversion is to be attempted.
   */
  Class<T> valueClass;

  /**
   * Callback which is to be called with the converted value if
   * the conversion is successfull.
   */
  Consumer<SuggestionFieldValue<T>> callback;

  VisitorHandler(Class<T> vc, Consumer<SuggestionFieldValue<T>> cb) {
    valueClass = vc;
    callback = cb;
  }

  /**
   * Tries the conversion and executes the callback if successfull.
   */
  boolean tryHandle(SuggestionFieldValue<?> field) {
    SuggestionFieldValue<T> converted = null;

    try {
      converted = field.cast(valueClass);
    } catch (ClassCastException cce) {
      return false;
    }

    callback.accept(converted);
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

  /**
   * Called for each field with type {@link Integer}.
   */
  default void visitInteger(SuggestionFieldValue<Integer> field) {}

  /**
   * Called for each field with type {@code List<String>}.
   */
  default void visitStringList(SuggestionFieldValue<List<String>> field) {}

  default void visitListField(SuggestionFieldValue<List> field) {
    visitStringList(SuggestionFieldValue.class.cast(field));
  }

  /**
   * Called for each field.
   *
   * Default implementation determines the underlying type of the field and
   * dispatch the applicable type-specific method.
   */
  default void visit(SuggestionFieldValue<?> field) {
    ArrayList<VisitorHandler<?>> handlers = new ArrayList<>();

    handlers.add(new VisitorHandler<>(String.class, this::visitString));
    handlers.add(new VisitorHandler<>(Integer.class, this::visitInteger));
    handlers.add(new VisitorHandler<>(List.class, this::visitListField));

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
