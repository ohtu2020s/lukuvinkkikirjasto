package ohtu.domain;

/**
 * Class
 */
@SuggestionKind("BOOK")
public class BookSuggestion extends Suggestion {
  /**
   * International Standard Book Number of the suggested book.
   *
   * @see #setIsbn
   * @see #getIsbn
   */
  @SuggestionField(display = "ISBN")
  private String isbn;

  @Override
  public String toString() {
    return String.join(", ", super.getTitle(), getKind().toLowerCase(), super.getAuthor(), super.getComment(), super.getStatus());
  }

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
