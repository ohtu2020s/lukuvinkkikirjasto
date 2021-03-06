package ohtu.ui;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import ohtu.Main;
import ohtu.domain.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.function.Consumer;
import ohtu.domain.PodcastSuggestion;

import ohtu.io.IO;
import ohtu.storage.NoSuchSuggestionException;
import ohtu.storage.SuggestionDao;
import ohtu.domain.SuggestionVisitor;
import ohtu.domain.SuggestionFieldValue;

class Command {

    String name;
    String description;
    Handler handler;

    static interface Handler {

        void handle() throws InterruptedException, IOException, URISyntaxException;
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

    private ArrayList<Command> commands = new ArrayList<>();

    private IO io;
    private SuggestionDao dao;
    private boolean terminated = false;

    public TextUI(IO io, SuggestionDao dao) {
        this.io = io;
        this.dao = dao;

        addCommand("new", "create a new suggestion", this::commandNew);
        addCommand("edit", "edit an existing suggestion", this::commandEdit);
        addCommand("show", "show saved suggestions", this::commandShow);
        addCommand("delete", "delete an existing suggestion", this::commandDelete);
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
        while (!terminated) {
            try {
                String input = io.prompt("> ");

                for (Command cmd : commands) {
                    if (cmd.name.equals(input)) {
                        cmd.handler.handle();
                        continue outer;
                    }
                }

                io.println("Unknown command: " + input);
            } catch (InterruptedException ie) {
                break;
            } catch (IOException ioe) {
                io.println("Could not open URL");
            } catch (URISyntaxException urie) {
                io.println("Could not open URL");
            }
        }
    }

    private void promptCommonFields(Suggestion suggestion) throws InterruptedException {
    }

    private interface InterruptableCallback {
        void call() throws InterruptedException;
    }

    private void showTagEditor(Set<String> tags) throws InterruptedException {
        TagEditor editor = new TagEditor(io, tags);

        if (!io.hasUtf8Support()) {
          editor.setStyle(TagEditor.simpleStyle);
        }

        editor.setIndent(4);
        editor.run();
    }

    private void wrapWithCommonFieldPrompts(Suggestion suggestion, InterruptableCallback cb) throws InterruptedException {
        io.println("Fill in:");
        String title = io.prompt("  Title: ");
        String author = io.prompt("  Author: ");
        String comment = io.prompt("  Comment: ");
        String status = io.prompt("  Status: ");
        suggestion.setAuthor(author);
        suggestion.setTitle(title);
        suggestion.setComment(comment);
        suggestion.setComment(status);

        cb.call();

        io.println("  Tags: ([A]dd, [R]emove, [C]ontinue, arrow keys navigate)");
        HashSet<String> tags = new HashSet<>();
        showTagEditor(tags);
        suggestion.setTags(tags);
    }

    private void commandNew() throws InterruptedException {
        String input = io.prompt("Select suggestion type ('book', 'podcast', 'article', 'blog' or 'video'): ");

        if (input.equalsIgnoreCase("book")) {
            BookSuggestion suggestion = new BookSuggestion();

            wrapWithCommonFieldPrompts(suggestion, () -> {
                String ISBN = io.prompt("  ISBN: ");
                suggestion.setIsbn(ISBN);
                suggestion.setUrl("https://isbnsearch.org/search?s=" + ISBN);
            });

            dao.saveSuggestion(suggestion);

        } else if (input.equalsIgnoreCase("podcast")) {
            PodcastSuggestion suggestion = new PodcastSuggestion();

            wrapWithCommonFieldPrompts(suggestion, () -> {
                String linkUrl = io.prompt("  URL: ");
                suggestion.setUrl(linkUrl);
            });

            dao.saveSuggestion(suggestion);
        } else if (input.equalsIgnoreCase("Article")) {
            ArticleSuggestion suggestion = new ArticleSuggestion();

            wrapWithCommonFieldPrompts(suggestion, () -> {
                String linkUrl = io.prompt("  URL: ");
                suggestion.setUrl(linkUrl);
            });

            dao.saveSuggestion(suggestion);
        } else if (input.equalsIgnoreCase("Blog")) {
            BlogSuggestion suggestion = new BlogSuggestion();

            wrapWithCommonFieldPrompts(suggestion, () -> {
                String linkUrl = io.prompt("  URL: ");
                suggestion.setUrl(linkUrl);
            });

            dao.saveSuggestion(suggestion);
        } else if (input.equalsIgnoreCase("Video")) {
            VideoSuggestion suggestion = new VideoSuggestion();

            wrapWithCommonFieldPrompts(suggestion, () -> {
                String linkUrl = io.prompt("  URL: ");
                suggestion.setUrl(linkUrl);
            });

            dao.saveSuggestion(suggestion);
        } else {
            io.println("Unknown suggestion type: " + input);
        }
    }

    private void printSuggestionListItem(Suggestion item) {
        String tags = item.getTags()
            .stream()
            .map(tag -> "[" + tag + "]")
            .collect(Collectors.joining(" "));

        io.println(String.format(
            "%3s %s %s",
            item.getId() + ":",
            item.toString(),
            tags
        ));
    } 

    private void printSuggestionList() {
        dao.getSuggestions()
            .stream()
            .forEach(this::printSuggestionListItem);
    }

    private void commandShow() throws InterruptedException, URISyntaxException, IOException {
        printSuggestionList();
        io.println();
        io.println("Sort the items by (n)ame, (s)earch, filter by (t)ag, (q)uit or (o)pen link");

        char input = 0;

        while ("sqnto".indexOf(input) == -1) {
            input = Character.toLowerCase(io.nextChar());
        }

        if (input == 'w') {
            return;
        }

        if (input == 'n') {
            dao.getSuggestions()
                    .stream()
                    .sorted(Comparator.comparing(Suggestion::getTitle))
                    .forEach(this::printSuggestionListItem);
        }

        if (input == 't') {
            String tag = io.prompt("Filter by tag: ");

            dao.getSuggestions()
                    .stream()
                    .filter(suggestion -> suggestion.getTags().contains(tag))
                    .forEach(this::printSuggestionListItem);
        }

        if (input == 's') {
            String searchterm = io.prompt("Search a suggestion by its name: ");

            dao.getSuggestions()
                    .stream()
                    .filter(suggestion -> suggestion.getTitle().contains(searchterm))
                    .forEach(this::printSuggestionListItem);
        }

        if (input == 'o') {
            String idString = io.prompt("Select a link to open by typing in it's ID: ");
            
            Integer id = Integer.valueOf(idString);

            if (id == null) {
                io.println("Invalid ID: " + input);
                return;
            }

            Suggestion suggestion = dao.getSuggestionById(id);

            if (suggestion.getUrl() == null) {
                io.println("Suggestion dosn't have a URL: " + input);
                return;
            }
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(suggestion.getUrl()));
            }
        }
    }

    private void commandDelete() throws InterruptedException {
        printSuggestionList();
        io.println();

        String input = io.prompt("Select an item from the above list to DELETE by typing in it's ID: ");

        Integer id = Integer.valueOf(input);

        if (id == null) {
            io.println("Invalid ID: " + input);
            return;
        }

        Suggestion suggestion = dao.getSuggestionById(id);

        if (suggestion == null) {
            io.println("Invalid ID: " + input);
            return;
        }

        input = io.prompt("Are you sure?: (Y/N) ");

        if (input.equalsIgnoreCase("y")) {
            dao.deleteSuggestion(suggestion);

        }

    }

    class SuggestionEditor implements SuggestionVisitor {

        private Optional<InterruptedException> interrupt = Optional.empty();

        @Override
        public void visitString(SuggestionFieldValue<String> field) {
            if (interrupt.isPresent()) {
                return;
            }

            try {
                String newValue = io.prompt("  " + field.getDisplayName() + ": ", field.getValue());
                field.setValue(newValue);
            } catch (InterruptedException ie) {
                interrupt = Optional.of(ie);
            }
        }

        @Override
        public void visitStringList(SuggestionFieldValue<List<String>> field) {
            if (interrupt.isPresent()) {
                return;
            }

            io.println("  " + field.getDisplayName() + ": ([A]dd, [R]emove, [C]ontinue, arrow keys navigate)");

            try {
                HashSet<String> set = new HashSet<>(field.getValue());
                showTagEditor(set);
                field.setValue(new ArrayList<>(set));
            } catch (InterruptedException ie) {
                interrupt = Optional.of(ie);
            }
        }

        public void finish() throws InterruptedException {
            if (interrupt.isPresent()) {
                throw interrupt.get();
            }
        }
    }

    private void commandEdit() throws InterruptedException {
        for (Suggestion item : dao.getSuggestions()) {
            io.println(String.format("%3s [%s] %s", item.getId() + ":", item.getKind(), item.getTitle()));
        }

        io.println();
        String input = io.prompt(
                "Select an item from the above list to edit by typing in it's ID: "
        );

        Integer id = Integer.valueOf(input);

        if (id == null) {
            io.println("Invalid ID: " + input);
            return;
        }

        Suggestion suggestion = dao.getSuggestionById(id);

        if (suggestion == null) {
            io.println("Invalid ID: " + input);
            return;
        }

        SuggestionEditor editor = new SuggestionEditor();
        suggestion.visit(editor);
        editor.finish();

        try {
            dao.updateSuggestion(suggestion);
        } catch (NoSuchSuggestionException snse) {
            io.println("Error while updating the suggestion!");
        }
    }

    private void commandQuit() {
        terminated = true;
    }
}
