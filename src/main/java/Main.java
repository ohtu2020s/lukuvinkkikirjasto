
import io.ConsoleIO;
import io.IO;
import ui.textUI;

public class Main {

    public static void main(String[] args) {
        textUI kayttoliittyma = new textUI(new ConsoleIO());
        kayttoliittyma.launch();
    }
}
