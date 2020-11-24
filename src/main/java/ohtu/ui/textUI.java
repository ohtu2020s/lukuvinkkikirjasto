package ohtu.ui;

import ohtu.io.IO;

/**
 * User Interface
 *
 */
public class textUI {

    private final IO io;

    public textUI(IO io) {
        this.io = io;
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
                        //kirjan tallennus 
                        continue;
                    }

                case "show":

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
