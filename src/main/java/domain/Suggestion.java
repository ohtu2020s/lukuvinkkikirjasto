package ohtu.domain;

import org.javatuples.Pair;
import java.lang.reflect.Method;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

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
  private int identifier;

  /**
   * Title or name of the suggested content.
   *
   * @see #setTitle
   * @see #getTitle
   */
  private String title;

  /**
   * Author or creator of the suggested content.
   *
   * @see #setAuthor
   * @see #getAuthor
   */
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
    visitor.visitString("title", getTitle());
    visitor.visitString("author", getAuthor());
  }

  /**
   * Populates the instance with data from a {@link SuggestionDataProvider}.
   *
   * @param dataProvider Source from which values for the fields are retrieved.
   */
  public void populate(SuggestionDataProvider dataProvider) {
    dataProvider
      .getString("title")
      .ifPresent(title -> setTitle(title));

    dataProvider
      .getString("author")
      .ifPresent(author -> setAuthor(author));
  }

  /**
   * Creates a new suggestion instance.
   *
   * @param kind Kind of the suggestion. An instance of the correct
   *    subclass is created based on this value.
   * @param dataProvider Instance is populated with data from this source.
   *    Provide a {@code null} value if you want to create a bare instance.
   *
   * @see #populate(SuggestionDataProvider)
   */
  static Suggestion create(String kind, SuggestionDataProvider dataProvider) {
    Suggestion suggestion = null;

    if (kind.equals(BookSuggestion.KIND)) {
      suggestion = new BookSuggestion();
    }

    if (suggestion != null && dataProvider != null) {
      suggestion.populate(dataProvider);
    }

    return suggestion;
  }
}
