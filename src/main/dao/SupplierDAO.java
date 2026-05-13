package main.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import main.model.Supplier;

public class SupplierDAO {

    public List<Supplier> getAll() {
        List<Supplier> suppliers = new ArrayList<>();
        String query = "SELECT * FROM suppliers ORDER BY name ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                suppliers.add(new Supplier(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("contact_person"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("address")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suppliers;
    }

    public boolean insert(Supplier s) {
        String query = "INSERT INTO suppliers (name, contact_person, phone, email, address) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setString(1, s.getName());
            stmt.setString(2, s.getContactPerson());
            stmt.setString(3, s.getPhone());
            stmt.setString(4, s.getEmail());
            stmt.setString(5, s.getAddress());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String query = "DELETE FROM suppliers WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}