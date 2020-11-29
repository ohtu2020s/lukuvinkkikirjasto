package ohtu.io;

import java.util.Scanner;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.IOException;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

/**
 *
 *
 */
public class ConsoleIO implements IO {
    private Scanner lukija;
    private LineReader lineReader;

    private PrintStream out;

    public ConsoleIO() {
        lukija = new Scanner(System.in);
        out = System.out;

        lineReader = LineReaderBuilder.builder().build();
    }

    ConsoleIO(InputStream in, PrintStream out) throws IOException {
        lukija = new Scanner(in);
        this.out = out;

        Terminal terminal = TerminalBuilder.builder()
            .streams(in, out)
            .build();

        lineReader = LineReaderBuilder.builder()
            .terminal(terminal)
            .build();
    }

    public String nextString() {
        return lukija.nextLine();
    }

    public void print(String m) {
        out.print(m);
    }

    public String prompt(String prompt) {
        return lineReader.readLine(prompt);
    }

    public String prompt(String prompt, String defaultValue) {
        return lineReader.readLine(prompt, null, defaultValue);
    }
}
