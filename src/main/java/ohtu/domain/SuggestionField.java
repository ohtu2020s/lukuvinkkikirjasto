package ohtu.domain;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;

/**
 * Annotation denoting a field of {@link Suggestion}
 * which should be accessible to the storage and UI
 * components of the application.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface SuggestionField {
  /**
   * Non-user-facing name of the field.
   */
  String name() default "";

  /**
   * User-facing name of the field.
   */
  String display() default "";

  /**
   * Whether the field should be hidden from the user.
   */
  boolean hidden() default false;
}
