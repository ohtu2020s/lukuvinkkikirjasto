package ohtu.io;

import java.util.function.Consumer;
import java.util.Optional;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class StubIOTester {
    Executor controllingThreadExecutor;
    Thread uiThread;
    StubIO io = new StubIO(); Optional<Throwable> uiThrowable = Optional.empty();
    static interface Executor {
        void execute(StubIO io) throws Throwable;
    }

    StubIOTester(Executor controlling, Executor ui) {
        controllingThreadExecutor = controlling;

        uiThread = new Thread(() -> {
            try {
                ui.execute(io);
            } catch (Throwable any) {
                uiThrowable = Optional.of(any);
            } finally {
                io.closeOutput();
            }
        });
    }

    public void test() throws Throwable {
        uiThread.start();

        try {
            controllingThreadExecutor.execute(io);
        } finally {
            io.closeInput();
        }

        if (uiThrowable.isPresent()) {
            throw uiThrowable.get();
        }
    }

    public static void test(Executor controlling, Executor ui) throws Throwable {
        new StubIOTester(controlling, ui).test();
    }
}

public class StubIOTest {
    @Test
    void executionTerminatesWithTrueWhenPredicateMatchesOutput() throws Throwable {
        StubIOTester.test(
            (io) -> {
                assertTrue(io.runUntil(line -> line.equals("match\n")));
            },
            (io) -> {
                io.println("Line #1");
                io.println("match");
                io.println("Line #3");
            }
        );
    }

    @Test
    void executionTerminatesWithFalseWhenNoOutputMatchesPredicate() throws Throwable {
        StubIOTester.test(
            (io) -> {
                assertFalse(io.runUntil(line -> line.equals("match\n")));
            },
            (io) -> {
                io.println("Line #1");
                io.println("Line #2");
            }
        );
    }

    @Test
    void queuedInputsAreReceivedInOrder() throws Throwable {
        StubIOTester.test(
            (io) -> {
                io.input("Line #1");
                io.input("Line #2");
                io.input("Line #3");
                io.run();
            },
            (io) -> {
                assertEquals("Line #1", io.nextString());
                assertEquals("Line #2", io.nextString());
                assertEquals("Line #3", io.nextString());
            }
        );
    }

    @Test
    void untriggeredConditionalInputIsNotReceived() throws Throwable {
        StubIOTester.test(
            (io) -> {
                io.input("Line #1");
                io.trigger("match", "Line #3");
                io.input("Line #2");
                io.run();
            },
            (io) -> {
                assertEquals("Line #1", io.nextString());
                assertEquals("Line #2", io.nextString());
                assertThrows(InterruptedException.class, () -> io.nextString());
            }
        );
    }

    @Test
    void triggeredConditionalInputIsReceived() throws Throwable {
        StubIOTester.test(
            (io) -> {
                io.input("Line #1");
                io.trigger("match", "Line #3");
                io.input("Line #2");
                io.run();
            },
            (io) -> {
                assertEquals("Line #1", io.nextString());
                io.println("match");
                assertEquals("Line #3", io.nextString());
                assertEquals("Line #2", io.nextString());
            }
        );
    }

    @Test
    void queuedDefaultInputIsReceivedAsTheDefaultInput() throws Throwable {
        StubIOTester.test(
            (io) -> {
                io.input();
                io.run();
            },
            (io) -> {
                String defaultInput = "Default Input";
                assertEquals(defaultInput, io.prompt("> ", defaultInput));
            }
        );
    }
}
