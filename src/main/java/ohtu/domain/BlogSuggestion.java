package ohtu.domain;

@SuggestionKind("BLOG")
public class BlogSuggestion extends Suggestion{

    @Override
    public String toString() {
        return String.join(", ", super.getTitle(), getKind().toLowerCase(), super.getAuthor(), super.getComment(), super.getStatus());
    }
}
