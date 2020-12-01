Feature: kayttaja voi poistaa vinkin

    Scenario: kayttajalta kysytaan varmistus ennen poistoa
        Given new book suggestion is created with title "title", author "author" and isbn "isbn"
        And   command delete is selected
        When  user inputs a valid suggestion id 0
        Then  system will respond with line containing "Are you sure?"

    Scenario: kayttaja voi poistaa olemassa olevan kirjavinkin
        Given new book suggestion is created with title "title", author "author" and isbn "isbn"
        And   command delete is selected
        When  user inputs a valid suggestion id 0
        And   user inputs a valid character "Y"
        Then  system will show the command prompt 
        And   suggestion with id 0 does not exist
        
    Scenario: kayttaja voi perua poiston vastaamalla N
        Given new book suggestion is created with title "title", author "author" and isbn "isbn"
        And   command delete is selected
        When  user inputs a valid suggestion id 0
        And   user inputs a valid character "N"
        Then  system will show the command prompt 
        And   suggestion with id 0 does exist

    Scenario: kayttaja ei voi valita poistettavaksi olematonta id:ta
        Given new book suggestion is created with title "title", author "author" and isbn "isbn"
        And   command delete is selected
        When  user inputs an invalid suggestion id 35
        Then  system will respond with line containing "Invalid ID"
        