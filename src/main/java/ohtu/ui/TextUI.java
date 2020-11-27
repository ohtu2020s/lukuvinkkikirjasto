package ohtu.ui;

import ohtu.Main;
import ohtu.domain.BookSuggestion;
import ohtu.domain.Suggestion;
import java.util.ArrayList;

import ohtu.io.IO;
import ohtu.storage.SuggestionDao;

class Command {
    String name;
    String description;
    Handler handler;

    static interface Handler {
        void handle();
    }

    Command(String name, String description, Handler handler) {
        this.name = name;
        this.description = description;
        this.handler = handler;
    }
}

/**
 * User Interface
 *
 */
public class TextUI {
    private static ArrayList<Command> commands = new ArrayList<>();

    private IO io;
    private SuggestionDao dao;
    private boolean terminate = false;

    public TextUI(IO io, SuggestionDao dao) {
        this.io = io;
        this.dao = dao;

        addCommand("new", "create a new suggestion", this::commandNew);
        addCommand("show", "show saved suggestions", this::commandShow);
        addCommand("quit", "quit the program", this::commandQuit);
    }

    private void addCommand(String name, String description, Command.Handler handler) {
        commands.add(new Command(name, description, handler));
    }

    public void launch() {
        io.println("Welcome to suggestion library. What would you like to do?");

        io.println("Commands:");

        for (Command cmd : commands) {
            io.println(String.format("  %-7s - %s", cmd.name, cmd.description));
        }

outer:
        while (!terminate) {
            String input = io.prompt("> ");

            for (Command cmd : commands) {
                if (cmd.name.equals(input)) {
                    cmd.handler.handle();
                    continue outer;
                }
            }

            io.println("Unknown command: " + input);
        }
    }

    private void commandNew() {
        String input = io.prompt("Select suggestion type (only 'book' for now): ");
        
        if (input.equalsIgnoreCase("book")) {
            io.println("Fill in:");

            String title = io.prompt("  Title: ");
            String author = io.prompt("  Author: ");
            String ISBN = io.prompt("  ISBN: ");

            BookSuggestion suggestion = new BookSuggestion();
            suggestion.setIsbn(ISBN);
            suggestion.setAuthor(author);
            suggestion.setTitle(title);
            dao.saveSuggestion(suggestion);
        } else {
            io.println("Unknown suggestion type: " + input);
        }
    }

    private void commandShow() {
        for (Suggestion item : dao.getSuggestions()) {
            System.out.println(item.toString());
        }
    }

    private void commandQuit() {
        terminate = true;
    }
}
