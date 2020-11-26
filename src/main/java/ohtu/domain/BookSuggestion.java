package ohtu.domain;

/**
 * Class
 */
public class BookSuggestion extends Suggestion {
  public static String KIND = "BOOK";

  /**
   * International Standard Book Number of the suggested book.
   *
   * @see #setIsbn
   * @see #getIsbn
   */
  private String isbn;

  public String getKind() {
    return KIND;
  }

  @Override
  public String toString() {
    return String.join(", ", super.getTitle(), KIND.toLowerCase(), super.getAuthor());
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

  /** {@inheritDoc} */
  @Override
  public void visit(SuggestionVisitor visitor) {
    super.visit(visitor);
    visitor.visitString("isbn", isbn);
  }

  /** {@inheritDoc} */
  @Override
  public void populate(SuggestionDataProvider dataProvider) {
    super.populate(dataProvider);

    dataProvider
      .getString("isbn")
      .ifPresent(isbn -> setIsbn(isbn));
  }
}
