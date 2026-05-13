package main.util;

public class Constants {
    public static final int MAX_LOGIN_ATTEMPTS = 5;
    public static final int DEFAULT_LOW_STOCK_THRESHOLD = 5;
    
    public static final int SILVER_POINTS = 0;
    public static final int GOLD_POINTS = 500;
    public static final int PLATINUM_POINTS = 2000;
    
    public static final double SILVER_DISCOUNT = 0.05;
    public static final double GOLD_DISCOUNT = 0.10;
    public static final double PLATINUM_DISCOUNT = 0.15;
    
    public static final String DB_URL = "jdbc:mysql://localhost:3306/bookstore_pos";
    public static final String DB_USER = "root";
    public static final String DB_PASS = "";
}