package ohtu;


import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.CucumberOptions.SnippetType;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    tags = "not @broken",
    plugin = "pretty", 
    features = "src/test/resources/ohtu",
    snippets = SnippetType.CAMELCASE 
)

public class RunCucumberTest {}
