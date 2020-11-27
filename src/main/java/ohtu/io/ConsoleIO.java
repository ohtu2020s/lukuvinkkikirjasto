package ohtu.io;

import java.util.Scanner;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;

/**
 *
 *
 */
public class ConsoleIO implements IO {
    private Scanner lukija;
    private LineReader lineReader;

    public ConsoleIO() {
        lukija = new Scanner(System.in);
        lineReader = LineReaderBuilder.builder().build();
    }

    public String nextString() {
        return lukija.nextLine();
    }

    public void print(String m) {
        System.out.print(m);
    }

    public String prompt(String prompt) {
        return lineReader.readLine(prompt);
    }

    public String prompt(String prompt, String defaultValue) {
        return lineReader.readLine(prompt, null, defaultValue);
    }
}
