package ohtu.domain;

@SuggestionKind("TEST")
public class TestSuggestion extends Suggestion {
    @SuggestionField
    private String testField;

    @SuggestionField(name = "AAAAAAAAAA")
    private String anotherField;

    @Override
    public String getKind() {
        return "TEST";
    }

    public String toString() {
        return "TEST";
    }
}
