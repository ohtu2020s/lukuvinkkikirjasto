package ohtu.domain;

/**
 * Class
 */
public class BookSuggestion extends Suggestion {
  /**
   * International Standard Book Number of the suggested book.
   *
   * @see #setIsbn
   * @see #getIsbn
   */
  private String isbn;

  /**
   * Gets the ISBN.
   *
   * @return ISBN of the book
   *
   * @see #isbn
   */
  public String getIsbn() {
    return isbn;
  }

  /**
   * Sets the ISBN.
   *
   * @param isbn ISBN of the book
   *
   * @see #isbn
   */
  public void setIsbn(String isbn) {
    this.isbn = isbn;
  }
}
