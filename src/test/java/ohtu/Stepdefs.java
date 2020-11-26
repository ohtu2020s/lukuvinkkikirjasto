package ohtu;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import java.util.ArrayList;
import java.util.List;

import ohtu.io.StubIO;
import ohtu.storage.InMemorySuggestionDao;
import static org.junit.Assert.*;
import ohtu.ui.textUI;

public class Stepdefs {

    StubIO io;
    List<String> inputLines;
    InMemorySuggestionDao memoryDao;

    @Before
    public void setup() {

        memoryDao = new InMemorySuggestionDao();
        inputLines = new ArrayList<>();

    }

    @Given("command new is selected$")
    public void commandNewSelected() throws Throwable {
        inputLines.add("new");

        io = new StubIO(inputLines);
        textUI kayttoliittyma = new textUI(io, memoryDao);
        kayttoliittyma.launch();
    }
    
    @Then("system will respond with {string}")
    public void systemWillRespondWith(String expectedOutput) {
        assertTrue(io.getPrints().contains(expectedOutput));
    }

}
