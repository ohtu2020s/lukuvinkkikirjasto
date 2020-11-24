package ohtu.storage;

public class InMemorySuggestionDaoTest extends SuggestionDAOTest<InMemorySuggestionDao> {
  @Override
  SuggestionDao createSuggestionDao() {
    return new InMemorySuggestionDao();
  }
}
