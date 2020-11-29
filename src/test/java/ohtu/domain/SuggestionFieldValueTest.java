package ohtu.domain;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;

class SuggestionFieldValueTest {
  static class TestSuggestion extends Suggestion {
    int fieldWithNoAnnotation;

    @Override
    public String toString() {
      return "test";
    }
  }
  static class AnotherTestSuggestion extends Suggestion {
    @SuggestionField(name = "customname")
    int anotherField;

    @SuggestionField(display = "CustomDisplayName")
    int anotherFieldWithDisplayName;

    @Override
    public String toString() {
      return "test2";
    }
  }

  @Test
  void tryingToCreateFromFieldWithoutAnnotationThrows() throws NoSuchFieldException {
    Suggestion obj = new TestSuggestion();
    Field field = obj.getClass().getDeclaredField("fieldWithNoAnnotation");

    assertThrows(IllegalArgumentException.class, () -> {
      SuggestionFieldValue.fromField(obj, field);
    });
  }

  @Test
  void tryingToCreateFromInvalidFieldThrows() throws NoSuchFieldException {
    Suggestion obj = new TestSuggestion();
    Field field = AnotherTestSuggestion.class.getDeclaredField("anotherField");

    assertThrows(IllegalArgumentException.class, () -> {
      SuggestionFieldValue.fromField(obj, field);
    });
  }

  @Test
  void defaultDisplayNameIsCapitalized() throws NoSuchFieldException {
    Suggestion obj = new AnotherTestSuggestion();
    Field field = obj.getClass().getDeclaredField("anotherField");
    SuggestionFieldValue<Object> value = SuggestionFieldValue.fromField(obj, field);

    assertEquals("AnotherField", value.getDisplayName());
  }

  @Test
  void explicitlyDefinedFieldNameIsReturnedCorrectly() throws NoSuchFieldException {
    Suggestion obj = new AnotherTestSuggestion();
    Field field = obj.getClass().getDeclaredField("anotherField");
    SuggestionFieldValue<Object> value = SuggestionFieldValue.fromField(obj, field);

    assertEquals("customname", value.getName());
  }

  @Test
  void explicitlyDefinedDisplayNameIsReturnedCorrectly() throws NoSuchFieldException {
    Suggestion obj = new AnotherTestSuggestion();
    Field field = obj.getClass().getDeclaredField("anotherFieldWithDisplayName");
    SuggestionFieldValue<Object> value = SuggestionFieldValue.fromField(obj, field);

    assertEquals("CustomDisplayName", value.getDisplayName());
  }
}
