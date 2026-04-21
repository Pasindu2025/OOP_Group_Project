package Main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3307/elephantdb"; // your DB URL
    private static final String USER = "root";     // your DB username
    private static final String PASS = "";         // your DB password

    // Get a connection to the database
    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Connect to the database
            conn = DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "MySQL Driver not found!\n" + e.getMessage());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection failed!\n" + e.getMessage());
        }
        return conn;
    }
    
    // Optional main for testing connection
    public static void main(String[] args) {
        Connection testConn = getConnection();
        if (testConn != null) {
            JOptionPane.showMessageDialog(null, "Database connected successfully!");
        } else {
            JOptionPane.showMessageDialog(null, "Failed to connect to database.");
        }
    }
}

