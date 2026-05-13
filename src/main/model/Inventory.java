package main.model;

public class Inventory {
    private int id;
    private Book book;
    private int quantity;
    private int lowStockThreshold;

    public Inventory() {
    }

    public Inventory(int id, Book book, int quantity, int lowStockThreshold) {
        this.id = id;
        this.book = book;
        this.quantity = quantity;
        this.lowStockThreshold = lowStockThreshold;
    }

    public boolean isLowStock() {
        return this.quantity <= this.lowStockThreshold;
    }

    public void restock(int amount) {
        if (amount > 0) {
            this.quantity += amount;
        } else {
            throw new IllegalArgumentException("Restock amount must be positive.");
        }
    }

    public int getId() { 
        return id; 
    }

    public void setId(int id) { 
        this.id = id; 
    }

    public Book getBook() { 
        return book; 
    }

    public void setBook(Book book) { 
        this.book = book; 
    }

    public int getQuantity() { 
        return quantity; 
    }

    public void setQuantity(int quantity) { 
        this.quantity = quantity; 
    }

    public int getLowStockThreshold() { 
        return lowStockThreshold; 
    }

    public void setLowStockThreshold(int lowStockThreshold) { 
        this.lowStockThreshold = lowStockThreshold; 
    }
}