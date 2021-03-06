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
    private String normalBullet = "•";
    private String focusedBullet = "›";
    private String editingBullet = "»";

    private static class Style {
      String normal;
      String focused;
      String editing;

      Style(String normal, String focused, String editing) {
        this.normal = normal;
        this.focused = focused;
        this.editing = editing;
      }
    }

    public static Style normalStyle = new Style("•", "›", "»");
    public static Style simpleStyle = new Style("*", ">", ":");
    private Style style = normalStyle;

    TagEditor(IO io, Set<String> tags) {
        this.io = io;
        this.set = tags;
        this.list = new ArrayList<>(tags);
    }

    public void setStyle(Style style) {
      this.style = style;
    }

    private void println(String line) {
        linesSinceStart++;
        io.println(indent + line);
    }

    private void printList() {
        for (int i = 0; i < list.size(); i++) {
            String bullet = i == focusedIndex ? style.focused : style.normal;
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
        String value = io.prompt(style.editing + " ");
        linesSinceStart++;

        if (!set.contains(value)) {
          list.add(value);
          set.add(value);
        } else {
          focusedIndex = list.indexOf(value);
        }
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
