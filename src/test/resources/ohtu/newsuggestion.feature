Feature: kayttaja voi luoda uuden vinkin

    Scenario: ohjelma antaa kayttajan valita 'uusi'
        Given command new is selected
        Then  system will respond with "bosok"