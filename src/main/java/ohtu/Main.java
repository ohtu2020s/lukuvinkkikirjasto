package ohtu;

import ohtu.io.ConsoleIO;
import ohtu.ui.textUI;

public class Main {

    public static void main(String[] args) {
        textUI kayttoliittyma = new textUI(new ConsoleIO());
        kayttoliittyma.launch();
    }
}
