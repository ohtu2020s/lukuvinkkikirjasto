package ohtu;

import ohtu.io.ConsoleIO;
import ohtu.storage.JDBCSuggestionDao;
import ohtu.ui.TextUI;

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

        TextUI kayttoliittyma = new TextUI(new ConsoleIO(), dao);
        kayttoliittyma.launch();
    }
}
