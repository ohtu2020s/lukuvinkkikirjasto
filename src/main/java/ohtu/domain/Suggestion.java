package ohtu.domain;

import org.javatuples.Pair;
import java.lang.reflect.Method;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.lang.reflect.Field;
import java.util.Set;
import java.util.HashSet;
import org.reflections.Reflections;

/**
 * Abstract base class for different kinds of suggested content.
 *
 * Contains setters and getters for common fields shared between all
 * suggestion kinds.
 */
public abstract class Suggestion {
  /**
   * Unique identifier of the suggestion.
   * 
   * @see #setId
   * @see #getId
   */
  @SuggestionField
  private int identifier;

  /**
   * Title or name of the suggested content.
   *
   * @see #setTitle
   * @see #getTitle
   */
  @SuggestionField
  private String title;

  /**
   * Author or creator of the suggested content.
   *
   * @see #setAuthor
   * @see #getAuthor
   */
  @SuggestionField
  private String author;

  /**
   * Get the title.
   *
   * @return Title or name of the suggested content
   *
   * @see #title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets the title.
   *
   * @param title Title or name of the suggested content
   *
   * @see #title
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Gets the author.
   *
   * @return Name of the suggested content's author
   *
   * @see #author
   */
  public String getAuthor() {
    return author;
  }

  /**
   * Sets the author.
   *
   * @param author Name of the author
   *
   * @see #author
   */
  public void setAuthor(String author) {
    this.author = author;
  }

  /**
   * Get the unique ID of this suggestion.
   *
   * @return Opaque unique numerical identifier of the suggestion
   *
   * @see #identifier
   */
  public int getId() {
    return identifier;
  }

  /**
   * Set the unique ID of this suggestion.
   *
   * @param id Numerical identifier of the suggestion.
   *
   * @see #identifier
   */
  public void setId(int id) {
    this.identifier = id;
  }

  /**
   * Calls the appropriate methods for each field of this suggestion instance.
   *
   * Designed to be overridden by subclasses.
   *
   * @param visitor Visitor, whose methods are called for each field.
   */
  public void visit(SuggestionVisitor visitor) {
    Class<?> c = getClass();

    while (!c.equals(Object.class)) {
      Field[] fields = c.getDeclaredFields();

      for (Field field : fields) {
        field.setAccessible(true);

        if (!field.isAnnotationPresent(SuggestionField.class)) {
          continue;
        }

        SuggestionFieldValue<Object> value = SuggestionFieldValue.fromField(this, field);
        visitor.visit(value);
      }

      c = c.getSuperclass();
    }
  }

  /**
   * Populates the instance with data from a {@link SuggestionDataProvider}.
   *
   * @param dataProvider Source from which values for the fields are retrieved.
   */
  public void populate(SuggestionDataProvider dataProvider) {
    visit(new SuggestionVisitor() {
      @Override
      public void visit(SuggestionFieldValue<?> field) {
        field.populate(dataProvider);
      }
    });
  }

  public static String getKind(Class<? extends Suggestion> suggestionClass) {
      SuggestionKind annotation = suggestionClass.getAnnotation(SuggestionKind.class);

      if (annotation == null) {
        return null;
      }

      return annotation.value();
  } 

  public String getKind() {
    return Suggestion.getKind(getClass());
  }

  abstract public String toString();
}
