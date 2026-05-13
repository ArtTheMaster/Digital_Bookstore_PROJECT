package main.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import main.model.Book;
import main.model.Inventory;

public class InventoryDAO {

    public List<Inventory> getAll() {
        String query = "SELECT i.*, b.title, b.author, b.genre, b.isbn, b.price, b.description " +
                       "FROM inventory i JOIN books b ON i.book_id = b.id";
        return fetchInventory(query);
    }

    public List<Inventory> getLowStockItems() {
        String query = "SELECT i.*, b.title, b.author, b.genre, b.isbn, b.price, b.description " +
                       "FROM inventory i JOIN books b ON i.book_id = b.id " +
                       "WHERE i.quantity <= i.low_stock_threshold";
        return fetchInventory(query);
    }

    public Inventory getByBookId(int bookId) {
        String query = "SELECT i.*, b.title, b.author, b.genre, b.isbn, b.price, b.description " +
                       "FROM inventory i JOIN books b ON i.book_id = b.id WHERE i.book_id = " + bookId;
        List<Inventory> results = fetchInventory(query);
        return results.isEmpty() ? null : results.get(0);
    }

    public boolean updateStock(int bookId, int newQty) {
        String query = "UPDATE inventory SET quantity = ? WHERE book_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setInt(1, newQty);
            stmt.setInt(2, bookId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateThreshold(int bookId, int newThreshold) {
        String query = "UPDATE inventory SET low_stock_threshold = ? WHERE book_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setInt(1, newThreshold);
            stmt.setInt(2, bookId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private List<Inventory> fetchInventory(String query) {
        List<Inventory> items = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Book book = new Book(
                    rs.getInt("book_id"), rs.getString("title"), rs.getString("author"),
                    rs.getString("genre"), rs.getString("isbn"), rs.getDouble("price"), 
                    rs.getString("description")
                );
                items.add(new Inventory(
                    rs.getInt("book_id"), // FIXED: Was looking for "id" instead of "book_id"
                    book, rs.getInt("quantity"), rs.getInt("low_stock_threshold")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
}