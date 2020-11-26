package ohtu.ui;

import ohtu.Main;
import ohtu.domain.BookSuggestion;
import ohtu.domain.Suggestion;
import ohtu.io.IO;
import ohtu.storage.SuggestionDao;

/**
 * User Interface
 *
 */
public class textUI {

    private final IO io;
    private final SuggestionDao dao;

    public textUI(IO io, SuggestionDao dao) {
        this.io = io;
        this.dao = dao;
    }

    public void launch() {
        while (true) {

            String input = io.readLine("Welcome to suggestion library. What would you like to do?"
                    + "\nCommands:"
                    + "\nnew    -   create a new suggestion"
                    + "\nshow   -   show saved suggestions"
                    + "\nquit   -   quit program");

            switch (input) {

                case "new":

                    io.print("Select suggestion type\n"
                            + "Commands:\n");
                    io.print("book"); //Cucumber ei suostunut yhteistyöhön, piti printata tämä erikseen
                    input = io.nextString();
                    
                    if (input.equalsIgnoreCase("book")) {
                        io.print("Fill in:\n"
                                + "Title:\n");
                        String title = io.nextString();
                        io.print("Author:");
                        String author = io.nextString();
                        io.print("ISBN:");
                        String ISBN = io.nextString();
                        BookSuggestion suggestion = new BookSuggestion();
                        suggestion.setIsbn(ISBN);
                        suggestion.setAuthor(author);
                        suggestion.setTitle(title);
                        dao.saveSuggestion(suggestion);
                        continue;
                    }

                case "show":
                    for (Suggestion item : dao.getSuggestions()) {
                        System.out.println(item.toString());
                    }

                    continue;
                case "quit":
                    break;

                default:
                    break;
            }
            break;
        }
    }
}
