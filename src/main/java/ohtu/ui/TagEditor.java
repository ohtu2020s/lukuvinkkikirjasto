package ohtu.ui;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import ohtu.io.IO;
import ohtu.domain.Suggestion;

public class TagEditor {
    private Set<String> set;
    private List<String> list;
    private int linesSinceStart = 0;
    private int paddingAfterList = 0;
    private int focusedIndex = 0;
    private IO io;
    private String indent = "";

    TagEditor(IO io, Set<String> tags) {
        this.io = io;
        this.set = tags;
        this.list = new ArrayList<>(tags);
    }

    private void println(String line) {
        linesSinceStart++;
        io.println(indent + line);
    }

    private void printList() {
        for (int i = 0; i < list.size(); i++) {
            String bullet = "•";

            if (i == focusedIndex) {
                bullet = "›";
            }

            println(bullet + " " + list.get(i));
        }
    }

    public void setIndent(int i) {
        StringBuilder sb = new StringBuilder();

        for (int j = 0; j < i; j++) {
            sb.append(" ");
        }

        indent = sb.toString();
    }

    private void printIndent() {
        io.print(indent);
    }

    private void clear() {
        if (linesSinceStart > 0) {
            io.print(String.format("\033[%dA\033[J", linesSinceStart));
            linesSinceStart = 0;
        }
    }

    private void handleAdd() throws InterruptedException {
        focusedIndex = list.size();
        clear();
        printList();
        printIndent();
        String value = io.prompt("» ");
        linesSinceStart++;
        list.add(value);
        set.add(value);
    }

    private void handleRemove() {
        set.remove(list.remove(focusedIndex));
    }

    private void handleUpArrow() {
        focusedIndex = Math.max(0, focusedIndex-1);
    }

    private void handleDownArrow() {
        focusedIndex = Math.min(set.size()-1, focusedIndex+1);
    }

    public void run() throws InterruptedException {
        while (true) {
            clear();
            printList();

            for (int i = 0; i < paddingAfterList; i++) {
                println("");
            }

            boolean gotWantedInput = false;
            while (!gotWantedInput) {
                char ch = io.nextChar();

                if (ch == '\033') {
                    io.nextChar();
                    char control = io.nextChar();

                    if (control == 'A') {
                        handleUpArrow();
                    } else if (control == 'B') {
                        handleDownArrow();
                    } else {
                        continue;
                    }
                } else if (ch == 'r' || ch == 'R') {
                    handleRemove();
                } else if (ch == 'a' || ch == 'A') {
                    handleAdd();
                } else if (ch == 'j' || ch == 'J') {
                    handleDownArrow();
                } else if (ch == 'k' || ch == 'K') {
                    handleUpArrow();
                } else if (ch == 'c' || ch == 'C') {
                    return;
                }

                gotWantedInput = true;
            }
        }
    }
}
