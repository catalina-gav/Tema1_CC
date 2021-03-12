

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static Connection connection = null;
private static Database dbInstance = null;

    private Database() {
    }

    public static Database getInstance() {
        if (dbInstance == null) {
            dbInstance = new Database();
        }
        return dbInstance;
    }

    public Connection getConnection() {
        final String username = "postgres";
        final String password = "admin";
        final String url = "jdbc:postgresql://localhost:5432/tema_cc";
        if (connection == null) {
            try {
               // System.out.println("in try");
                Class.forName("org.postgresql.Driver");
            } catch (Exception e1) {
                System.out.println("class not found exception");
                e1.printStackTrace();
            }
            try {
                connection = DriverManager.getConnection(url, username, password);
                System.out.println("Database connection succesfull");

            } catch (SQLException e) {
                System.out.println("Connection failure");
                e.printStackTrace();
            }

        }
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Am inchis conexiunea cu baza de date");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

