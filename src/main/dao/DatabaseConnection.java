package main.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import main.util.Constants;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(Constants.DB_URL, Constants.DB_USER, Constants.DB_PASS);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Database Connection Failed! Error: " + e.getMessage());
        }
    }

    public static Connection getConnection() {
        try {
            if (instance == null || instance.connection.isClosed()) {
                instance = new DatabaseConnection();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check connection status.", e);
        }
        return instance.connection;
    }
}