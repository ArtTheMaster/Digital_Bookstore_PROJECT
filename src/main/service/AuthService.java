package main.service;

import java.util.HashMap;
import java.util.Map;
import main.dao.UserDAO;
import main.model.User;
import main.util.Constants;
import main.util.InputValidator;
import main.util.PasswordHasher;

public class AuthService {
    private final UserDAO userDAO;
    private final Map<String, Integer> failedAttempts;

    public AuthService() {
        this.userDAO = new UserDAO();
        this.failedAttempts = new HashMap<>();
    }

   public User login(String username, String password) throws Exception {
        // ==========================================
        // 🚧 DEV TESTING BYPASS: REMOVE BEFORE FINALS
        // ==========================================
        if (username.equals("admin") && password.equals("admin")) {
            User adminUser = userDAO.findByUsername("admin");
            if (adminUser != null) {
                return adminUser; 
            }
        }
        // ==========================================

        int attempts = failedAttempts.getOrDefault(username, 0);
        
        if (attempts >= Constants.MAX_LOGIN_ATTEMPTS) {
            throw new Exception("Account locked due to too many failed attempts.");
        }

        User user = userDAO.findByUsername(username);
        
        if (user != null && PasswordHasher.verify(password, user.getPasswordHash())) {
            failedAttempts.remove(username); // Reset on success
            return user;
        } else {
            failedAttempts.put(username, attempts + 1);
            throw new Exception("Invalid username or password.");
        }
    }

    public boolean register(User user, String rawPassword) throws Exception {
        if (!InputValidator.isValidEmail(user.getEmail())) {
            throw new Exception("Invalid email format.");
        }
        
        if (!InputValidator.isStrongPassword(rawPassword)) {
            throw new Exception("Password does not meet strength requirements.");
        }
        
        if (userDAO.findByUsername(user.getUsername()) != null) {
            throw new Exception("Username already exists.");
        }

        user.setPasswordHash(PasswordHasher.hash(rawPassword));
        return userDAO.insert(user);
    }

    public boolean hasRole(User user, User.Role... allowedRoles) {
        if (user == null) {
            return false;
        }
        for (User.Role role : allowedRoles) {
            if (user.getRole() == role) {
                return true;
            }
        }
        return false;
    }
}