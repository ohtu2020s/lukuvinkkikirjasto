Feature: kayttaja voi luoda uuden vinkin

    Scenario: käyttäjä voi luoda uuden kirjavinkin
        Given command new is selected
        When  user inputs a valid suggestion type "book"
        And   user inputs a new title "LOTR"
        And   user inputs a new author "TOLKIEN"
        And   user inputs a new comment "recommended by friend"
        And   user inputs a new status "in progress"
        And   user inputs a new isbn "9780544003415"
        And   user inputs character "C" to continue
        Then  suggestion can be found from database with title "LOTR", author "TOLKIEN", isbn "9780544003415", comment "recommended by friend" and status "in progress"
  
    Scenario: käyttäjä saa virheilmoituksen valitessaan olemattoman vinkkityypin
       Given command new is selected
       When  user inputs invalid suggestion type "suggestionThatDoesntExist"
       Then  system will respond with line containing "Unknown suggestion type: suggestionThatDoesntExist"
