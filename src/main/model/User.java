package main.model;

public class User {
    public enum Role { ADMIN, MANAGER, CASHIER }
    
    private int id; 
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private Role role;

    public User(int id, String username, String passwordHash, String fullName, String email, Role role) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    public boolean hasPermission(String action) { 
        if (this.role == Role.ADMIN) {
            return true;
        }
        if (this.role == Role.MANAGER && (action.equals("VIEW_DASHBOARD") || action.equals("MANAGE_INVENTORY"))) {
            return true;
        }
        if (this.role == Role.CASHIER && action.equals("POS_ACCESS")) {
            return true;
        }
        return false;
    }

    public int getId() { 
        return id; 
    }
    
    public void setId(int id) { 
        this.id = id; 
    }

    public String getUsername() { 
        return username; 
    }
    
    public void setUsername(String username) { 
        this.username = username; 
    }

    public String getPasswordHash() { 
        return passwordHash; 
    }
    
    public void setPasswordHash(String passwordHash) { 
        this.passwordHash = passwordHash; 
    }

    public String getFullName() { 
        return fullName; 
    }
    
    public void setFullName(String fullName) { 
        this.fullName = fullName; 
    }

    public String getEmail() { 
        return email; 
    }
    
    public void setEmail(String email) { 
        this.email = email; 
    }

    public Role getRole() { 
        return role; 
    }
    
    public void setRole(Role role) { 
        this.role = role; 
    }
}