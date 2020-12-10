package ohtu.domain;

@SuggestionKind("VIDEO")
public class VideoSuggestion extends Suggestion{

    @Override
    public String toString() {
        return String.join(", ", super.getTitle(), getKind().toLowerCase(), super.getAuthor(), super.getComment(), super.getStatus());
    }
}
