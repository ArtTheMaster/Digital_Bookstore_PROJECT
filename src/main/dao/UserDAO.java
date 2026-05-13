package main.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import main.model.User;

public class UserDAO {

    public User findByUsername(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(User user) {
        String query = "INSERT INTO users (username, password_hash, full_name, email, role) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getRole().name());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
             
            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("id"),
            rs.getString("username"),
            rs.getString("password_hash"),
            rs.getString("full_name"),
            rs.getString("email"),
            User.Role.valueOf(rs.getString("role"))
        );
    }
}