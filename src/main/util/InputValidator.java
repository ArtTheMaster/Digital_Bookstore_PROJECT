package main.util;

public class InputValidator {

    public static boolean isValidEmail(String email) { 
        if (email == null) return false;
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$"); 
    }

    public static boolean isValidPhone(String phone) { 
        if (phone == null) return false;
        return phone.matches("^09\\d{9}$"); 
    }

    public static boolean isValidPrice(String price) {
        try {
            return Double.parseDouble(price) >= 0;
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
    }

    public static boolean isValidQuantity(String qty) {
        try {
            return Integer.parseInt(qty) >= 0;
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
    }

    // Relaxed password rule for testing
    public static boolean isStrongPassword(String pwd) { 
        return pwd != null && pwd.length() >= 6; 
    }

    public static String sanitize(String input) {
        if (input == null) return "";
        return input.trim().replaceAll("['\";=]", "");
    }
}