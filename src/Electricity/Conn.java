package Electricity;

import java.sql.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Conn {
    Connection c;
    Statement s;

    public Conn() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("src/db.properties")) {
            props.load(fis);
        } catch (IOException e) {
            System.err.println("Error reading db.properties file: " + e.getMessage());
            // Consider re-throwing a custom exception or exiting if the DB connection is critical
            return; 
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            c = DriverManager.getConnection(props.getProperty("db.url"), props.getProperty("db.user"), props.getProperty("db.password"));
            s = c.createStatement();

        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
            // Consider re-throwing a custom exception or exiting
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            // Consider re-throwing a custom exception or exiting
        }
    }
}