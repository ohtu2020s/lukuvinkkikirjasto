package ohtu.domain;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;

class SuggestionFieldValueTest {
  int fieldWithNoAnnotation;

  class AnotherType {
    @SuggestionField(name = "customname")
    int anotherField;

    @SuggestionField(display = "CustomDisplayName")
    int anotherFieldWithDisplayName;
  }

  @Test
  void tryingToCreateFromFieldWithoutAnnotationThrows() throws NoSuchFieldException {
    Object obj = new SuggestionFieldValueTest();
    Field field = obj.getClass().getDeclaredField("fieldWithNoAnnotation");

    assertThrows(IllegalArgumentException.class, () -> {
      SuggestionFieldValue.fromField(obj, field);
    });
  }

  @Test
  void tryingToCreateFromInvalidFieldThrows() throws NoSuchFieldException {
    Object obj = new SuggestionFieldValueTest();
    Field field = AnotherType.class.getDeclaredField("anotherField");

    assertThrows(IllegalArgumentException.class, () -> {
      SuggestionFieldValue.fromField(obj, field);
    });
  }

  @Test
  void defaultDisplayNameIsCapitalized() throws NoSuchFieldException {
    Object obj = new AnotherType();
    Field field = obj.getClass().getDeclaredField("anotherField");
    SuggestionFieldValue<Object> value = SuggestionFieldValue.fromField(obj, field);

    assertEquals("AnotherField", value.getDisplayName());
  }

  @Test
  void explicitlyDefinedFieldNameIsReturnedCorrectly() throws NoSuchFieldException {
    Object obj = new AnotherType();
    Field field = obj.getClass().getDeclaredField("anotherField");
    SuggestionFieldValue<Object> value = SuggestionFieldValue.fromField(obj, field);

    assertEquals("customname", value.getName());
  }

  @Test
  void explicitlyDefinedDisplayNameIsReturnedCorrectly() throws NoSuchFieldException {
    Object obj = new AnotherType();
    Field field = obj.getClass().getDeclaredField("anotherFieldWithDisplayName");
    SuggestionFieldValue<Object> value = SuggestionFieldValue.fromField(obj, field);

    assertEquals("CustomDisplayName", value.getDisplayName());
  }
}
