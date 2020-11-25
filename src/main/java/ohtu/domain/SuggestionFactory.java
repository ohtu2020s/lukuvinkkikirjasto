package ohtu.domain;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.reflections.Reflections;

public class SuggestionFactory {
  private static Map<String, Class<? extends Suggestion>> kinds = null;
  private Class<? extends Suggestion> suggestionClass;

  private SuggestionFactory(Class<? extends Suggestion> suggestionClass) {
    this.suggestionClass = suggestionClass;
  }

  private static Map<String, Class<? extends Suggestion>> getSuggestionClasses() {
    if (kinds == null) {
      kinds = new HashMap<>();

      Set<Class<? extends Suggestion>> subtypes = new Reflections("ohtu.domain")
        .getSubTypesOf(Suggestion.class);

      for (Class<? extends Suggestion> subtype : subtypes) {
        String kindId = Suggestion.getKind(subtype);

        if (kindId != null) {
          kinds.put(kindId, subtype);
        }
      }
    }

    return kinds;
  }

  public static SuggestionFactory getFactory(String kind) {
    Class<? extends Suggestion> suggestionClass = getSuggestionClasses().get(kind);
    return new SuggestionFactory(suggestionClass);
  }

  public static List<SuggestionFactory> getFactories() {
    return getSuggestionClasses()
      .values()
      .stream()
      .map(cls -> new SuggestionFactory(cls))
      .collect(Collectors.toList());
  }

  public static Set<String> getKinds() {
    return getSuggestionClasses().keySet();
  }

  public String getKind() {
    return Suggestion.getKind(suggestionClass);
  }

  public Suggestion create() {
    try {
      return suggestionClass.getConstructor().newInstance();
    } catch (Exception exception) {
      exception.printStackTrace();
      return null;
    }
  }

  public Suggestion create(SuggestionDataProvider data) {
    Suggestion instance = create();
    instance.populate(data);
    return instance;
  }

  public static Suggestion create(String kind) {
    return SuggestionFactory.getFactory(kind).create();
  }

  public static Suggestion create(String kind, SuggestionDataProvider data) {
    Suggestion instance = SuggestionFactory.create(kind);
    instance.populate(data);
    return instance;
  }
}
