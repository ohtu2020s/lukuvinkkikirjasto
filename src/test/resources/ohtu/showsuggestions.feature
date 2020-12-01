@broken
Feature: kayttaja voi hakea vinkkeja

    Scenario: käyttäjä voi listata olemassaolevat vinkit
        Given new book suggestion is created with title "title", author "author" and isbn "isbn"
        And   new book suggestion is created with title "anotherTitle", author "anotherAuthor" and isbn "anotherIsbn"
        And   command show is selected
        Then  system will respond with line containing "title, book, author"
        And   system will respond with line containing "anotherTitle, book, anotherAuthor"
