package main.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import main.model.Book;

public class BookDAO {

    public List<Book> getAll() {
        return searchBooks("SELECT * FROM books", new String[]{});
    }

    public Book getById(int id) {
        String query = "SELECT * FROM books WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractBookFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Book> searchByTitle(String title) {
        return searchBooks("SELECT * FROM books WHERE title LIKE ?", new String[]{"%" + title + "%"});
    }

    public List<Book> searchByAuthor(String author) {
        return searchBooks("SELECT * FROM books WHERE author LIKE ?", new String[]{"%" + author + "%"});
    }

    public List<Book> searchByGenre(String genre) {
        return searchBooks("SELECT * FROM books WHERE genre LIKE ?", new String[]{"%" + genre + "%"});
    }

    public boolean insert(Book book) {
        String query = "INSERT INTO books (title, author, genre, isbn, price, description) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
             
            setBookParameters(stmt, book);
            
            if (stmt.executeUpdate() > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    book.setId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Book book) {
        String query = "UPDATE books SET title=?, author=?, genre=?, isbn=?, price=?, description=? WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            setBookParameters(stmt, book);
            stmt.setInt(7, book.getId());
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- MISSING METHOD RESTORED ---
    public boolean delete(int id) {
        String query = "DELETE FROM books WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private List<Book> searchBooks(String query, String[] params) {
        List<Book> books = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            for (int i = 0; i < params.length; i++) {
                stmt.setString(i + 1, params[i]);
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                books.add(extractBookFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    private void setBookParameters(PreparedStatement stmt, Book book) throws SQLException {
        stmt.setString(1, book.getTitle());
        stmt.setString(2, book.getAuthor());
        stmt.setString(3, book.getGenre());
        stmt.setString(4, book.getIsbn());
        stmt.setDouble(5, book.getPrice());
        stmt.setString(6, book.getDescription());
    }

    private Book extractBookFromResultSet(ResultSet rs) throws SQLException {
        return new Book(
            rs.getInt("id"), 
            rs.getString("title"), 
            rs.getString("author"),
            rs.getString("genre"), 
            rs.getString("isbn"),
            rs.getDouble("price"), 
            rs.getString("description")
        );
    }
}