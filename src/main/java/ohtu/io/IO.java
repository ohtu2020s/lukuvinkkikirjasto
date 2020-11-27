package ohtu.io;

/**
 * 
 * 
 */
public interface IO {
    void print(String m);
    String nextString();

    String prompt(String prompt);
    String prompt(String prompt, String defaultValue);

    default void println(String line) {
        print(line + "\n");
    }

    default void println() {
        print("\n");
    }
}
