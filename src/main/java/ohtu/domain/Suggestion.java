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
   * URL web address of the resource.
   *
   * @see #setUrl
   * @see #getUrl
   */
  @SuggestionField
  private String url;

  @SuggestionField
  private ArrayList<String> tags = new ArrayList<>();

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
   * Gets the URL.
   *
   * @return URL address of the suggestion
   *
   * @see #url
   */
  
  public String getUrl() {
      return url;
  }
  
   /**
   * Sets the URL.
   *
   * 
   * @param url address of the suggestion
   *
   * @see #url
   */
  public void setUrl(String url) {
      this.url = url;
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
   * Gets a set containing all tags associated with this suggestion.
   *
   * Returns a <i>new</i> set containing the tags.
   * Operations on this returned set do not affect the internal list of tags.
   *
   * @return A new set containing the tags.
   */
  public Set<String> getTags() {
    return new HashSet<>(tags);
  }

  /**
   * Associates a new tag with this suggestion.
   *
   * Does nothing if the given tag is already associated with this suggestion.
   */
  public void addTag(String tag) {
    if (!tags.contains(tag)) {
      tags.add(tag);
    }
  }

  /**
   * Removes a tag from being associated with this suggestion.
   */
  public void removeTag(String tag) {
    tags.remove(tag);
  }

  public void setTags(Set<String> ptags) {
    tags = new ArrayList<String>(ptags);
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

  /**
   * Gets the kind identifier of the specified subclass.
   */
  public static String getKind(Class<? extends Suggestion> suggestionClass) {
      SuggestionKind annotation = suggestionClass.getAnnotation(SuggestionKind.class);

      if (annotation == null) {
        return null;
      }

      return annotation.value();
  } 

  /**
   * Gets the kind of this instance.
   */
  public String getKind() {
    return Suggestion.getKind(getClass());
  }

  abstract public String toString();
}
