package ui;

import ui.IO;
import java.util.Scanner;

/**
 *
 *
 */
public class ConsoleIO implements IO {

    private Scanner lukija;

    public ConsoleIO() {
        lukija = new Scanner(System.in);
    }

    public String nextString() {
        return lukija.nextLine();
    }

    public void print(String m) {
        System.out.println(m);
    }

}
