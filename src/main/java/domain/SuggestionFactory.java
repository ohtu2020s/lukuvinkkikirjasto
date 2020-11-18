package ohtu.domain;

public class SuggestionFactory {
  public static Suggestion build(String kind, SuggestionDataProvider dataProvider) {
    Suggestion suggestion = null;

    if (kind.equals(BookSuggestion.KIND)) {
      suggestion = buildBookSuggestion(dataProvider);
    }

    suggestion.setTitle(dataProvider.getString("title"));
    suggestion.setAuthor(dataProvider.getString("author"));

    return suggestion;
  }

  private static BookSuggestion buildBookSuggestion(SuggestionDataProvider dataProvider) {
    BookSuggestion suggestion = new BookSuggestion();

    suggestion.setIsbn(dataProvider.getString("isbn"));

    return suggestion;
  }
}
