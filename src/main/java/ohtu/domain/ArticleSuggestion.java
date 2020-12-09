package ohtu.domain;


@SuggestionKind("ARTICLE")
public class ArticleSuggestion extends Suggestion {
    
  @Override
  public String toString() {
    return String.join(", ", super.getTitle(), getKind().toLowerCase(), super.getAuthor(), super.getComment());
  }
  
}
