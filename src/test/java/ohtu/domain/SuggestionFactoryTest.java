package ohtu.domain;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import java.util.stream.Collectors;

public class SuggestionFactoryTest {
  @Test
  void getFactoriesAndGetKindsAreMutuallyConsistent() {
    Set<String> kinds = SuggestionFactory.getFactories()
      .stream()
      .map(SuggestionFactory::getKind)
      .collect(Collectors.toSet());

    for (String kind : kinds) {
      assertTrue(SuggestionFactory.getKinds().contains(kind));
    }
  }
}
