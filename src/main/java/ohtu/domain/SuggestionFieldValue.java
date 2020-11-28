package ohtu.domain;

import java.lang.reflect.Field;

public class SuggestionFieldValue<T extends Object> {
  private Object object;
  private Field field;
  private SuggestionField annotation;
  private T value;

  public SuggestionFieldValue(Object object, Field field, SuggestionField annotation, T value) {
    this.object = object;
    this.field = field;
    this.annotation = annotation;
    this.value = value;
  }

  public static SuggestionFieldValue<Object> fromField(Object object, Field field) {
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

  public <T2> SuggestionFieldValue<T2> cast(Class<T2> type) {
    if (this.value == null && !type.isAssignableFrom(field.getType())) {
      throw new ClassCastException(field.getType() + " cannot be assigned to " + type);
    }

    T2 value = type.cast(this.value);
    return new SuggestionFieldValue<T2>(object, field, annotation, value);
  }

  public String getName() {
    if (!annotation.name().equals("")) {
      return annotation.name();
    }

    return field.getName();
  }

  public String getDisplayName() {
    if (!annotation.display().equals("")) {
      return annotation.display();
    }

    return Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1);
  }

  public T getValue() {
    return value;
  }

  public Class<T> getType() {
    return (Class<T>) field.getType();
  }

  public void setValue(T value) {
    this.value = value;

    try {
      field.set(object, value);
    } catch (IllegalAccessException iae) {
      iae.printStackTrace();
    }
  }

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
    }).visit(this);
  }
}
