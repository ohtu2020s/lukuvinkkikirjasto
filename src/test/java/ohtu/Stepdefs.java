package ohtu;

import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import static org.junit.Assert.*;
import ohtu.ui.TextUI;

import java.util.ArrayList;
import java.util.List;

import ohtu.io.StubIO;
import ohtu.storage.InMemorySuggestionDao;
import ohtu.domain.BookSuggestion;
import ohtu.domain.Suggestion;
import ohtu.domain.SuggestionVisitor;
import ohtu.domain.SuggestionFieldValue;

public class Stepdefs {
    volatile StubIO io;
    InMemorySuggestionDao memoryDao;
    TextUI ui;
    Thread uiThread;

    @Before
    public void setup() {
        memoryDao = new InMemorySuggestionDao();
        io = new StubIO();
        ui = new TextUI(io, memoryDao);
        uiThread = new Thread(ui::launch);
        uiThread.start();
    }

    @Given("command new is selected$")
    public void commandNewSelected() {
        io.input("new");
        io.runUntil(line -> line.equals("> "));
    }

    @Given("command edit is selected")
    public void commandEditSelected() {
        io.input("edit");
        io.runUntil(line -> line.equals("> "));
    }

    @Given("new book suggestion is created with title {string}, author {string} and isbn {string}")
    public void newBookSuggestionIsCreated(String title, String author, String isbn) {
        BookSuggestion s = new BookSuggestion();
        s.setTitle(title);
        s.setAuthor(author);
        s.setIsbn(isbn);
        memoryDao.saveSuggestion(s);
    }

    @When("user inputs an invalid suggestion id {int}")
    public void userInputsAnInvalidSuggestionId(int id) {
        io.input(String.valueOf(id));
    }

    @When("user inputs a valid suggestion id {int}")
    public void userInputsAValidSuggestionId(int id) {
        io.input(String.valueOf(id));
    }

    @When("user inputs a new title {string}")
    public void userInputsANewTitle(String title) {
        io.trigger("Title:", title);
    }

    @When("user inputs a new author {string}")
    public void userInputsANewAuthor(String author) {
        io.trigger("Author:", author);
    }

    @When("user inputs a new isbn {string}")
    public void userInputsANewISBN(String isbn) {
        io.trigger("ISBN:", isbn);
    }

    @When("user leaves the title unmodified")
    public void userLeavesTheTitleUnmodified() {
        io.trigger("Title:", null);
    }

    @When("user leaves the author unmodified")
    public void userLeavesTheAuthorUnmodified() {
        io.trigger("Author:", null);
    }

    @When("user leaves the isbn unmodified")
    public void userLeavesTheISBNUnmodified() {
        io.trigger("ISBN:", null);
    }
    
    @Then("system will respond with line {string}")
    public void systemWillRespondWith(String expectedOutput) {
        assertTrue(io.runUntil(expectedOutput::equals));
    }

    @Then("system will respond with line containing {string}")
    public void systemWillRespondWithLineContaining(String segment) {
        assertTrue(io.runUntil(line -> line.contains(segment)));
    }

    @Then("system will show the command prompt")
    public void systemWillShowTheCommandPrompt() {
        assertTrue(io.runUntil(line -> line.equals("> ")));
    }

    @Then("field {string} of suggestion {int} has value of {string}")
    public void fieldOfSuggestionHasValueOf(String fieldName, int id, String value) {
        Suggestion suggestion = memoryDao.getSuggestionById(id);

        assertFalse(suggestion == null);

        boolean[] visited = { false };

        suggestion.visit(new SuggestionVisitor() {
            @Override
            public void visitString(SuggestionFieldValue<String> field) {
                if (field.getName().equals(fieldName)) {
                    assertEquals(value, field.getValue());
                    visited[0] = true;
                }
            }
        });

        assertTrue(visited[0]);
    }
}
