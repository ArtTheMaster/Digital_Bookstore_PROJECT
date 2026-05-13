package main.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import main.model.Customer;
import main.model.Order;
import main.model.OrderItem;
import main.model.User;

public class OrderDAO {

    public boolean insertOrder(Order order) {
        // Ensures the insert matches the exact columns in your database
        String orderQuery = "INSERT INTO orders (customer_id, user_id, total_amount, discount_applied, payment_method) VALUES (?, ?, ?, ?, ?)";
        String itemQuery = "INSERT INTO order_items (order_id, book_id, quantity, unit_price, subtotal) VALUES (?, ?, ?, ?, ?)";
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start Transaction

            try (PreparedStatement orderStmt = conn.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS)) {
                
                if (order.getCustomer() != null) {
                    orderStmt.setInt(1, order.getCustomer().getId());
                } else {
                    orderStmt.setNull(1, Types.INTEGER);
                }
                
                orderStmt.setInt(2, order.getCashier().getId());
                orderStmt.setDouble(3, order.getTotalAmount());
                orderStmt.setDouble(4, order.getDiscountApplied());
                orderStmt.setString(5, order.getPaymentMethod());
                
                orderStmt.executeUpdate();
                
                ResultSet rs = orderStmt.getGeneratedKeys();
                if (!rs.next()) {
                    throw new SQLException("Failed to retrieve Order ID.");
                }
                int orderId = rs.getInt(1);
                order.setId(orderId);

                // Insert Items in a batch
                try (PreparedStatement itemStmt = conn.prepareStatement(itemQuery)) {
                    for (OrderItem item : order.getItems()) {
                        itemStmt.setInt(1, orderId);
                        itemStmt.setInt(2, item.getBook().getId());
                        itemStmt.setInt(3, item.getQuantity());
                        itemStmt.setDouble(4, item.getUnitPrice());
                        itemStmt.setDouble(5, item.getSubtotal());
                        itemStmt.addBatch();
                    }
                    itemStmt.executeBatch();
                }
            }
            
            conn.commit(); // Commit Transaction
            return true;
            
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    // --- NEW METHOD: FETCHES ALL SALES HISTORY ---
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        // Query joins orders, users (cashier), and customers to get all details
        String query = "SELECT o.id, o.order_date, o.total_amount, o.discount_applied, o.payment_method, " +
                       "u.username, c.name as customer_name " +
                       "FROM orders o " +
                       "JOIN users u ON o.user_id = u.id " +
                       "LEFT JOIN customers c ON o.customer_id = c.id " +
                       "ORDER BY o.order_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setTotalAmount(rs.getDouble("total_amount"));
                order.setDiscountApplied(rs.getDouble("discount_applied"));
                order.setPaymentMethod(rs.getString("payment_method"));
                
                // Safely convert SQL Timestamp to LocalDateTime
                Timestamp ts = rs.getTimestamp("order_date");
                if (ts != null) {
                    order.setCreatedAt(ts.toLocalDateTime());
                }

                // Reconstruct the Cashier object
                User cashier = new User(0, rs.getString("username"), "", "", "", User.Role.CASHIER);
                order.setCashier(cashier);

                // Reconstruct the Customer object (if there is one)
                String customerName = rs.getString("customer_name");
                if (customerName != null) {
                    order.setCustomer(new Customer(0, customerName, "", ""));
                }
                
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
}