package ohtu.io;

import java.util.Scanner;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.IOException;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.terminal.Attributes;

/**
 *
 *
 */
public class ConsoleIO implements IO {
    private LineReader lineReader;
    private Terminal terminal;

    public ConsoleIO() {
        try {
            terminal = TerminalBuilder.builder()
                    .system(true)
                    .build();

            lineReader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .build();
        } catch (IOException ioe) {
            lineReader = LineReaderBuilder.builder().build();
            terminal = lineReader.getTerminal();
        }
    }

    ConsoleIO(InputStream in, PrintStream out) throws IOException {
        terminal = TerminalBuilder.builder()
            .streams(in, out)
            .build();

        lineReader = LineReaderBuilder.builder()
            .terminal(terminal)
            .build();
    }

    @Override
    public boolean hasUtf8Support() {
      return !terminal.getType().startsWith("dumb");
    }

    public char nextChar() {
        Attributes attrs = terminal.enterRawMode();

        try {
            return (char) terminal.reader().read();
        } catch (IOException ioe) {
            return 0;
        } finally {
            terminal.setAttributes(attrs);
        }
    }

    public String nextString() {
        return lineReader.readLine();
    }

    public void print(String m) {
        terminal.writer().print(m);
        terminal.writer().flush();
    }

    public String prompt(String prompt) {
        return lineReader.readLine(prompt);
    }

    public String prompt(String prompt, String defaultValue) {
        return lineReader.readLine(prompt, null, defaultValue);
    }
}
