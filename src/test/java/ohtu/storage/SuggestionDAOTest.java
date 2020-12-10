package ohtu.storage;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import ohtu.domain.BookSuggestion;
import ohtu.domain.Suggestion;

public abstract class SuggestionDAOTest<T extends SuggestionDao> {

    SuggestionDao dao;

    abstract SuggestionDao createSuggestionDao();

    BookSuggestion createBookSuggestion() {
        BookSuggestion sugg = new BookSuggestion();
        sugg.setTitle("Infinite Jest");
        sugg.setAuthor("David Foster Wallace");
        sugg.setIsbn("9780316920049");
        sugg.setUrl("URL..//123");
        sugg.setComment("recommended by friend");
        sugg.setStatus("completed");
        sugg.addTag("tag1");
        sugg.addTag("tag2");
        return sugg;
    }

    @BeforeEach
    void initializeDao() {
        dao = createSuggestionDao();
        dao.setup();
    }

    @Test
    void suggestionCanBeRetrieved() {
        BookSuggestion sugg = createBookSuggestion();
        dao.saveSuggestion(sugg);

        List<Suggestion> suggestions = dao.getSuggestions();

        assertEquals(1, suggestions.size());
    }

    @Test
    void commonSuggestionFieldsAreRetrievedCorrectly() {
        BookSuggestion sugg = createBookSuggestion();
        dao.saveSuggestion(sugg);

        List<Suggestion> suggestions = dao.getSuggestions();
        Suggestion retrieved = suggestions.get(0);

        assertEquals(sugg.getTitle(), retrieved.getTitle());
        assertEquals(sugg.getAuthor(), retrieved.getAuthor());
        assertEquals(sugg.getTags(), retrieved.getTags());
    }

    @Test
    void retrievedSuggestionIsOfTheCorrectSubClass() {
        BookSuggestion sugg = createBookSuggestion();

        dao.saveSuggestion(sugg);

        List<Suggestion> suggestions = dao.getSuggestions();
        Suggestion retrieved = suggestions.get(0);

        assertTrue(retrieved instanceof BookSuggestion);
    }

    @Test
    void retrievedSuggestionHasCorrectSubclassFieldValues() {
        BookSuggestion sugg = createBookSuggestion();
        dao.saveSuggestion(sugg);

        List<Suggestion> suggestions = dao.getSuggestions();
        BookSuggestion retrieved = (BookSuggestion) suggestions.get(0);

        assertEquals(sugg.getIsbn(), retrieved.getIsbn());
    }

    @Test
    void suggestionCanBeRetrievedByItsId() {
        BookSuggestion sugg = createBookSuggestion();
        dao.saveSuggestion(sugg);

        Suggestion retrieved = dao.getSuggestionById(sugg.getId());
        assertTrue(retrieved != null);
    }

    @Test
    void suggestionRetrievedByItsIdHasCorrectKind() {
        BookSuggestion sugg = createBookSuggestion();
        dao.saveSuggestion(sugg);

        Suggestion retrieved = dao.getSuggestionById(sugg.getId());
        assertEquals(retrieved.getKind(), sugg.getKind());
    }

    @Test
    void suggestionRetrievedByItsIdHasCorrectFields() {
        BookSuggestion sugg = createBookSuggestion();
        dao.saveSuggestion(sugg);

        Suggestion retrieved = dao.getSuggestionById(sugg.getId());
        assertEquals(retrieved.getId(), sugg.getId());
        assertEquals(retrieved.getTitle(), sugg.getTitle());
        assertEquals(retrieved.getAuthor(), sugg.getAuthor());
    }

    @Test
    void tryingToRetrieveNonexistentSuggestionReturnsNull() {
        assertNull(dao.getSuggestionById(123));
    }

    @Test
    void tryingToUpdateNonexitentSuggestionThrows() {
        assertThrows(NoSuchSuggestionException.class, () -> {
            dao.updateSuggestion(createBookSuggestion());
        });
    }

    @Test
    void updatingAnExistingSuggestionChangesTheFields() throws NoSuchSuggestionException {
        BookSuggestion s = createBookSuggestion();
        dao.saveSuggestion(s);

        s.setTitle("New Title");
        s.setAuthor("New Author");
        s.setIsbn("New Isbn");

        dao.updateSuggestion(s);
        BookSuggestion s2 = (BookSuggestion) dao.getSuggestionById(s.getId());

        assertEquals("New Title", s2.getTitle());
        assertEquals("New Author", s2.getAuthor());
        assertEquals("New Isbn", s2.getIsbn());
    }

    @Test
    void existingSuggestionCanBeDeleted() throws NoSuchSuggestionException {
        BookSuggestion sugg = createBookSuggestion();
        dao.saveSuggestion(sugg);

        List<Suggestion> suggestions = dao.getSuggestions();
        assertEquals(1, suggestions.size());

        dao.deleteSuggestion(sugg);

        suggestions = dao.getSuggestions();
        assertEquals(0, suggestions.size());

    }

    @Test
    void theRightSuggestionIsDeleted() throws NoSuchSuggestionException {
        
        BookSuggestion suggToBeDeleted = createBookSuggestion();
        dao.saveSuggestion(suggToBeDeleted);
        
        BookSuggestion suggToStay = new BookSuggestion();
        suggToStay.setTitle("New Title");
        suggToStay.setAuthor("New Author");
        suggToStay.setIsbn("New Isbn");
        suggToStay.setUrl("New URL");
        suggToStay.setComment("New Comment");
        suggToStay.setComment("New Status");
        dao.saveSuggestion(suggToStay);
        
        dao.deleteSuggestion(suggToBeDeleted);
        
        assertTrue(dao.getSuggestionById(suggToBeDeleted.getId())==null);
        assertTrue(dao.getSuggestionById(suggToStay.getId())!=null);

    }
}
