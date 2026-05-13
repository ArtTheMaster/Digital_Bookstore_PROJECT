package main.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import main.model.Customer;
import main.model.Member;
import main.model.Membership;
import main.util.Constants;

public class CustomerDAO {

    public List<Customer> getAll() {
        List<Customer> customers = new ArrayList<>();
        String query = "SELECT * FROM customers";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                customers.add(extractCustomer(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    public boolean insertCustomer(Customer c, boolean isMember, String tier, double points) {
        String query = "INSERT INTO customers (name, phone, is_member, points, tier) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
             
            stmt.setString(1, c.getFullName());
            stmt.setString(2, c.getPhone());
            stmt.setBoolean(3, isMember);
            stmt.setDouble(4, points);
            stmt.setString(5, tier);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateMembership(int id, boolean isMember, String tier, double points) {
        String query = "UPDATE customers SET is_member = ?, tier = ?, points = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setBoolean(1, isMember);
            stmt.setString(2, tier);
            stmt.setDouble(3, points);
            stmt.setInt(4, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateMembershipPoints(int customerId, int newPoints) {
        String query = "UPDATE customers SET points = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, newPoints);
            stmt.setInt(2, customerId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean upgradeTier(int customerId, String newTier, double newDiscount) {
        String query = "UPDATE customers SET tier = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newTier);
            stmt.setInt(2, customerId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private Customer extractCustomer(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String phone = rs.getString("phone");
        boolean isMember = rs.getBoolean("is_member");

        if (isMember) {
            String tier = rs.getString("tier");
            if (tier == null || tier.isEmpty()) tier = "SILVER";
            
            double discount = 0.0;
            if (tier.equals("SILVER")) discount = Constants.SILVER_DISCOUNT;
            else if (tier.equals("GOLD")) discount = Constants.GOLD_DISCOUNT;
            else if (tier.equals("PLATINUM")) discount = Constants.PLATINUM_DISCOUNT;

            Membership membership = new Membership(id, Membership.MembershipType.valueOf(tier), discount, (int) rs.getDouble("points"));
            return new Member(id, name, "", phone, membership);
        }
        return new Customer(id, name, "", phone);
    }
}