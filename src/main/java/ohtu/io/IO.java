package ohtu.io;

/**
 * 
 * 
 */
public interface IO {
    void print(String m);

    String nextString() throws InterruptedException;
    String prompt(String prompt) throws InterruptedException;
    String prompt(String prompt, String defaultValue) throws InterruptedException;

    default void println(String line) {
        print(line + "\n");
    }

    default void println() {
        print("\n");
    }
}
