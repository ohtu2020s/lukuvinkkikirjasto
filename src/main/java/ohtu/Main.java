package ohtu;

import ohtu.domain.Suggestion;
import ohtu.io.ConsoleIO;
import ohtu.storage.JDBCSuggestionDao;
import ohtu.ui.textUI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {

    private static JDBCSuggestionDao dao;

    public static void main(String[] args) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:suggestions.db");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        dao = new JDBCSuggestionDao(connection);

        textUI kayttoliittyma = new textUI(new ConsoleIO());
        kayttoliittyma.launch();
    }

    public static void saveSuggestion(Suggestion suggestion) {
        dao.saveSuggestion(suggestion);
    }
}
