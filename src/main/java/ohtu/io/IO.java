package ohtu.io;

/**
 * 
 * 
 */
public interface IO {
    void print(String m);
    String nextString();

    default String prompt(String prompt) {
        print(prompt);
        return nextString();
    }

    default void println(String line) {
        print(line + "\n");
    }
}
