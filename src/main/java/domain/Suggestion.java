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

  public void visit(SuggestionVisitor visitor) {
    visitor.visitString("title", getTitle());
    visitor.visitString("author", getAuthor());
  }

  public void populate(SuggestionDataProvider dataProvider) {
    dataProvider
      .getString("title")
      .ifPresent(title -> setTitle(title));

    dataProvider
      .getString("author")
      .ifPresent(author -> setAuthor(author));
  }

  static Suggestion create(String kind, SuggestionDataProvider dataProvider) {
    Suggestion suggestion = null;

    if (kind.equals(BookSuggestion.KIND)) {
      suggestion = new BookSuggestion();
    }

    if (suggestion != null) {
      suggestion.populate(dataProvider);
    }

    return suggestion;
  }
}
