Feature: kayttaja voi luoda uuden vinkin

    Scenario: käyttäjä voi luoda uuden kirjavinkin
        Given command new is selected
        When  user inputs a valid suggestion type "book"
        And   user inputs a new title "LOTR"
        And   user inputs a new author "TOLKIEN"
        And   user inputs a new isbn "9780544003415"
        Then  suggestion can be found from database with title "LOTR", author "TOLKIEN" and isbn "9780544003415"
  
    Scenario: käyttäjä valitsee olemattoman vinkkityypin
       Given command new is selected
       When  user inputs invalid suggestion type "suggestionThatDoesntExist"
       Then  system will respond with line containing "Unknown suggestion type: suggestionThatDoesntExist"
