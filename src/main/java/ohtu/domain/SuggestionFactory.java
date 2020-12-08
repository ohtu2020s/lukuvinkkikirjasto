package ohtu.domain;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.reflections.Reflections;

/**
 * Class providing an interface for creating {@link Suggestion Suggestions}.
 *
 * Instances of this class can be used to create {@link Suggestion Suggestions}
 * of specific {@link SuggestionKind Kind}, but in most cases the static methods
 * {@link #create(String)} and {@link #create(String, SuggestionDataProvider)}
 * can be used.
 */
public class SuggestionFactory<T extends Suggestion> {
    /**
     * List of all subclasses of {@link Suggestion} with the {@link SuggestionKind}
     * annotation. Accessed using the {@link #getSuggestionClasses} method, which
     * populates the list on first invocation.
     */
    private static Map<String, Class<? extends Suggestion>> kinds = null;

    /**
     * The specific {@link Suggestion} subclass associated with this instance.
     */
    private Class<T> suggestionClass;

    /**
     * Creates an factory instance from the specified subclass of {@link Suggestion}.
     */
    private SuggestionFactory(Class<T> suggestionClass) {
        this.suggestionClass = suggestionClass;
    }

    /**
     * Traverses the {@code ohtu.domain} package and finds all subclasses of {@link Suggestion}
     * which have the {@link SuggestionKind} annotation. The result is cached in {@link #kinds}
     * so that the slow traversal is only neccessary once.
     */
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

    /**
     * Returns a factory instance for the specified suggestion kind.
     *
     * @param kind Textual suggestion kind identifier defined via the
     *   {@link SuggestionKind} annotation.
     *
     * @return A factory instance which can be used to construct instances of
     *   the {@link Suggestion} subclass associate with the given {@code kind}.
     */
    public static SuggestionFactory<?> getFactory(String kind) {
        Class<? extends Suggestion> suggestionClass = getSuggestionClasses().get(kind);
        return new SuggestionFactory<>(suggestionClass);
    }

    /**
     * Creates a list of factory instances containing a factory for every known suggestion kind.
     *
     * @return List containing a factory for each known suggestion kind.
     */
    public static List<SuggestionFactory<?>> getFactories() {
        return getSuggestionClasses()
            .values()
            .stream()
            .map(cls -> new SuggestionFactory<>(cls))
            .collect(Collectors.toList());
    }

    /**
     * Returns identifiers for all known suggestion kinds.
     */
    public static Set<String> getKinds() {
        return getSuggestionClasses().keySet();
    }

    /**
     * Returns the kind of suggestion created by this instance.
     */
    public String getKind() {
        return Suggestion.getKind(suggestionClass);
    }

    /**
     * Creates an {@link Suggestion} instance with all fields set to their
     * default values.
     *
     * @return A new {@link Suggestion} instance.
     */
    public T create() {
        try {
            return suggestionClass.getConstructor().newInstance();
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    /**
     * Creates an {@link Suggestion} instance and populates its fields with
     * value from an {@link SuggestionDataProvider}.
     *
     * @param data Data source for populating the instance's fields.
     *
     * @return A new {@link Suggestion} instance.
     */
    public T create(SuggestionDataProvider data) {
        T instance = create();
        instance.populate(data);
        return instance;
    }

    /**
     * Creates an {@link Suggestion} instance of the specified {@code kind}.
     *
     * @param kind Suggestion kind identifier defined using the {@link SuggestionKind} annotation.
     *
     * @return A new {@link Suggestion} instance.
     */
    public static Suggestion create(String kind) {
        return SuggestionFactory.getFactory(kind).create();
    }

    /**
     * Creates an {@link Suggestion} instance of the specified {@code kind} and
     * populates its fields using values from a {@link SuggestionDataProvider}.
     *
     * @param kind Suggestion kind identifier defined using the {@link SuggestionKind} annotation.
     * @param data Data source for populating the created instance's fields.
     *
     * @return A new {@link Suggestion} instance.
     */
    public static Suggestion create(String kind, SuggestionDataProvider data) {
        Suggestion instance = SuggestionFactory.create(kind);
        instance.populate(data);
        return instance;
    }
}
