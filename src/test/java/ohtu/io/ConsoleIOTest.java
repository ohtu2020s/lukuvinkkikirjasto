package ohtu.io;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.PrintStream;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;

import java.util.ArrayDeque;

public class ConsoleIOTest {
    // NOTE(dogamak):
    //   For some reason, the output stream seems to start with a whole bunch of null bytes.
    //   This is why the test do not use assertEquals, but instead only check that the wanted
    //   output is contained /somewhere/ in the output buffer.
    ByteArrayOutputStream os;
    PrintStream out;
    AppendableByteArrayInputStream in;
    ConsoleIO io;

    static class AppendableByteArrayInputStream extends InputStream {
        private ArrayDeque<Byte> array = new ArrayDeque<>();

        @Override
        public int read() {
            if (array.isEmpty())
                return 0;

            byte b = array.removeFirst();
            return b;
        }

        public void appendString(String str) {
            for (byte b : str.getBytes())
                array.add(b);
        }
    }

    @BeforeEach
    void setupIO() throws IOException {
        os = new ByteArrayOutputStream();
        out = new PrintStream(os);
        in = new AppendableByteArrayInputStream();

        io = new ConsoleIO(in, out);
    }

    @Test
    void printPrintsTheLine() {
        io.print("Line!");
        assertTrue(os.toString().contains("Line!"));
        assertFalse(os.toString().contains("Line!\n"));
    }

    @Test
    void printlnPrintsTheLine() {
        io.println("Line!");
        assertTrue(os.toString().contains("Line!\n"));
    }

    @Test
    void promptPrintsThePrompt() {
        in.appendString("Input\n");
        String line = io.prompt("Prompt: ");
        assertTrue(os.toString().contains("Prompt: "));
        assertFalse(os.toString().contains("Prompt: \n"));
        assertEquals("Input", line);
    }

    @Test
    void promptWithDefaultValuePrintsTheDefaultValue() {
        in.appendString("Input\n");
        io.prompt("Prompt: ", "Default");
        assertTrue(os.toString().contains("Prompt: "));
        assertTrue(os.toString().contains("Default"));
        assertFalse(os.toString().contains("Prompt: \n"));
    }

    @Test
    void promptWithDefaultValueReturnsTheDefaultValueOnEnter() {
        in.appendString("\n");
        String line = io.prompt("Prompt: ", "Default");
        assertTrue(os.toString().contains("Prompt: "));
        assertTrue(os.toString().contains("Default"));
        assertFalse(os.toString().contains("Prompt: \n"));
        assertEquals(line, "Default");
    }

    @Test
    void promptWithDefaultValueCanBeEdited() {
        in.appendString("\u0008\u000842\n"); // \u0008 represents backspace
        String line = io.prompt("Prompt: ", "Item 12");
        assertTrue(os.toString().contains("Prompt: "));
        assertTrue(os.toString().contains("Item 12"));
        assertFalse(os.toString().contains("Prompt: \n"));
        assertEquals(line, "Item 42");
    }

    @Test
    void nextStringReturnsTheNextLine() {
        in.appendString("Line\nAnother\n");
        assertEquals("Line", io.nextString());
    }
}
