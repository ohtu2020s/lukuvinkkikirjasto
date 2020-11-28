package ohtu.storage;

public class NoSuchSuggestionException extends Exception {
  public NoSuchSuggestionException(int id) {
    super(String.format("no suggestion with id %d exists", id));
  }
}
