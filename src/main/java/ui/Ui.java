package ui;

/**
 * User Interface
 *
 */
public class Ui {

    private IO io;

    public Ui(IO io) {
        this.io = io;
    }

    public void launch() {
        io.print("Type something:");
        String input = io.nextString();

        switch (input) {

            case "new":
                io.print("Hello World!");
                break;

            case "something something":
                io.print("!");
                break;

            default:
                break;
        }

    }
}
