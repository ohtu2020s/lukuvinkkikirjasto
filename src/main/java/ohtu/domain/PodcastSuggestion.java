package ohtu.domain;


@SuggestionKind("PODCAST")
public class PodcastSuggestion extends Suggestion {
  
  @Override
  public String toString() {
    return String.join(", ", super.getTitle(), getKind().toLowerCase(), super.getAuthor());
  }
  
}
