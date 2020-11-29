package ohtu.domain;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;

/**
 * Declared an subclass of {@link Suggestion} to be
 * a complete implementation of a suggestion kind.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SuggestionKind {
    /**
     * The suggestion kind identifier used to distinguish between kinds
     * at runtime.
     */
    String value();
}
