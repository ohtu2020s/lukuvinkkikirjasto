package ohtu.domain;

public interface SuggestionVisitor {
  void visitString(String name, String value);
}
