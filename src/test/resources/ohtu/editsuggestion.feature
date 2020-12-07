Feature: käyttäjä voi muokata olemassaolevaa vinkkiä

    Scenario: käyttäjä syöttää virheellisen vinkin numeron

        Given new book suggestion is created with title "title", author "author" and isbn "isbn"
        And   command edit is selected
        When  user inputs an invalid suggestion id 123
        Then  system will respond with line containing "Invalid ID: 123"

    Scenario: käyttäjä valitsee onnistuneesti vinkin ja syöttää uudet arvot

        Given new book suggestion is created with title "title", author "author" and isbn "isbn"
        And   command edit is selected
        When  user inputs a valid suggestion id 0
        And   user inputs a new title "new title"
        And   user inputs a new author "new author"
        And   user inputs a new isbn "new isbn"
        And   user inputs a new url "new url"
        Then  system will show the command prompt
        And   field "title" of suggestion 0 has value of "new title"
        And   field "author" of suggestion 0 has value of "new author"
        And   field "isbn" of suggestion 0 has value of "new isbn"

    Scenario: käyttäjä valitsee onnistuneesti vinkin mutta jättää arvot ennalleen

        Given new book suggestion is created with title "title", author "author" and isbn "isbn"
        And   command edit is selected
        When  user inputs a valid suggestion id 0
        And   user leaves the title unmodified
        And   user leaves the author unmodified
        And   user leaves the isbn unmodified
        And   user leaves the url unmodified
        Then  system will show the command prompt
        And   field "title" of suggestion 0 has value of "title"
        And   field "author" of suggestion 0 has value of "author"
        And   field "isbn" of suggestion 0 has value of "isbn"
