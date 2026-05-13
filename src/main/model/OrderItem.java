package main.model;

public class OrderItem {
    private int id;
    private Book book; 
    private int quantity; 
    private double unitPrice;

    public OrderItem(Book book, int quantity) {
        this.book = book;
        this.quantity = quantity;
        this.unitPrice = book.getPrice();
    }

    public double getSubtotal() { 
        return this.quantity * this.unitPrice; 
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

    public double getUnitPrice() { 
        return unitPrice; 
    }
    
    public void setUnitPrice(double unitPrice) { 
        this.unitPrice = unitPrice; 
    }
}