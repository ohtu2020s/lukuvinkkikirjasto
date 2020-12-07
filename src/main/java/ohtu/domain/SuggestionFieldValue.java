package ohtu.domain;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Represents a field of a {@link Suggestion} instance on runtime.
 */
public class SuggestionFieldValue<T extends Object> {
  /**
   * The instance this field is part of.
   */
  private Suggestion object;

  /**
   * The Java field in the {@link Suggestion} instance {@link #object}.
   */
  private Field field;

  /**
   * Value of the annotation on the Java field.
   */
  private SuggestionField annotation;

  /**
   * Value of the field.
   */
  private T value;

  private SuggestionFieldValue(Suggestion object, Field field, SuggestionField annotation, T value) {
    this.object = object;
    this.field = field;
    this.annotation = annotation;
    this.value = value;
  }

  /**
   * Creates an instance from an {@link Suggestion} instance and a {@link Field} of that instance.
   *
   * @param object The {@link Suggestion} instance of which {@code field} is part of.
   * @param field Field in the {@code object}. Should be annotated with {@link SuggestionField}.
   *
   * @return An instance representing the given field.
   */
  public static SuggestionFieldValue<Object> fromField(Suggestion object, Field field) throws IllegalArgumentException {
    SuggestionField annotation = field.getAnnotation(SuggestionField.class);

    if (annotation == null) {
      throw new IllegalArgumentException("the field does not have SuggestionField annotation");
    }

    try {
      Object value = field.get(object);
      return new SuggestionFieldValue<Object>(object, field, annotation, value);
    } catch (IllegalAccessException iae) {
      throw new IllegalArgumentException("the field cannot be accessed");
    }
  }

  /**
   * Tries to cast this instance to an instance with different, but compatible, wrapped type.
   *
   * @param type Class of the type into which casting is tried.
   */
  public <T2> SuggestionFieldValue<T2> cast(Class<T2> type) {
    if (this.value == null && !type.isAssignableFrom(field.getType())) {
      throw new ClassCastException(field.getType() + " cannot be assigned to " + type);
    }

    T2 value = type.cast(this.value);
    return new SuggestionFieldValue<T2>(object, field, annotation, value);
  }

  /**
   * Gets the non-user-facing name of the field.
   *
   * This value can be defined with the {@link SuggestionField#name} property.
   * Defaults to the name of the Java field.
   *
   * @return Non-user-facing name of the field.
   */
  public String getName() {
    if (!annotation.name().equals("")) {
      return annotation.name();
    }

    return field.getName();
  }

  /**
   * Gets the user-facing name of the field.
   *
   * This value can be defined with the {@link SuggestionField#name} property.
   * Defaults to capitalized version of the Java field's name.
   *
   * @return User-facing name of the field.
   */
  public String getDisplayName() {
    if (!annotation.display().equals("")) {
      return annotation.display();
    }

    return Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1);
  }

  /**
   * Gets the value of the field.
   */
  public T getValue() {
    return value;
  }

  /**
   * Gets the {@link Class} representing the field's type.
   */
  public Class<T> getType() {
    return (Class<T>) field.getType();
  }

  /**
   * Sets the fields value.
   */
  public void setValue(T value) {
    this.value = value;

    try {
      field.set(object, value);
    } catch (IllegalAccessException iae) {
      iae.printStackTrace();
    }
  }

  /**
   * Populates the field with a value from {@code dataProvider}.
   *
   * @param dataProvider Data provider from which the new value for this field is fetched.
   */
  public void populate(SuggestionDataProvider dataProvider) {
    (new SuggestionVisitor() {
      @Override
      public void visitString(SuggestionFieldValue<String> field) {
        dataProvider.getString(field.getName()).ifPresent(field::setValue);
      }

      @Override
      public void visitInteger(SuggestionFieldValue<Integer> field) {
        dataProvider.getInteger(field.getName()).ifPresent(field::setValue);
      }

      @Override
      public void visitStringList(SuggestionFieldValue<List<String>> field) {
        dataProvider.getStringList(field.getName()).ifPresent(field::setValue);
      }
    }).visit(this);
  }
}
