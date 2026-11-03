// ============================================================
// DatabaseHelper.java — Database Connection Helper
// This class reads db.properties and creates a connection to MySQL
// ============================================================

import java.sql.Connection;
import java.sql.DriverManager;
import java.io.FileInputStream;
import java.util.Properties;

public class DatabaseHelper {

    // Returns a new connection to the MySQL database
    // Reads connection details from db.properties file
    public static Connection getConnection() throws Exception {

        // Read database settings from db.properties file
        Properties props = new Properties();
        props.load(new FileInputStream("db.properties"));

        String url = props.getProperty("db.url");
        String username = props.getProperty("db.username");
        String password = props.getProperty("db.password");
        String driver = props.getProperty("db.driver");

        // Load the MySQL JDBC driver
        Class.forName(driver);

        // Create and return a database connection
        Connection conn = DriverManager.getConnection(url, username, password);
        return conn;
    }
}
