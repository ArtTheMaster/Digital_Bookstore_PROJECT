package main.model;

public class Book implements Comparable<Book> {
    private int id;
    private String title;
    private String author;
    private String genre;
    private String isbn;
    private double price;
    private String description;

    public Book() {
    }

    public Book(int id, String title, String author, String genre, String isbn, double price, String description) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.isbn = isbn;
        this.price = price;
        this.description = description;
    }

    @Override
    public int compareTo(Book other) {
        return this.title.compareToIgnoreCase(other.title);
    }

    @Override
    public String toString() {
        return this.title + " by " + this.author + " (₱" + this.price + ")";
    }

    public int getId() { 
        return id; 
    }
    
    public void setId(int id) { 
        this.id = id; 
    }

    public String getTitle() { 
        return title; 
    }
    
    public void setTitle(String title) { 
        this.title = title; 
    }

    public String getAuthor() { 
        return author; 
    }
    
    public void setAuthor(String author) { 
        this.author = author; 
    }

    public String getGenre() { 
        return genre; 
    }
    
    public void setGenre(String genre) { 
        this.genre = genre; 
    }

    public String getIsbn() { 
        return isbn; 
    }
    
    public void setIsbn(String isbn) { 
        this.isbn = isbn; 
    }

    public double getPrice() { 
        return price; 
    }
    
    public void setPrice(double price) { 
        this.price = price; 
    }

    public String getDescription() { 
        return description; 
    }
    
    public void setDescription(String description) { 
        this.description = description; 
    }
}