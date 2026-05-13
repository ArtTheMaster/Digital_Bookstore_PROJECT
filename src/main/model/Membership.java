package main.model;

public class Membership {
    public enum MembershipType { SILVER, GOLD, PLATINUM }
    
    private int id; 
    private MembershipType type; 
    private double discountPercent; 
    private int points;

    public Membership() {
    }

    public Membership(int id, MembershipType type, double discountPercent, int points) {
        this.id = id;
        this.type = type;
        this.discountPercent = discountPercent;
        this.points = points;
    }

    public void addPoints(double amount) { 
        this.points += (int) (amount / 100); 
    }

    public int getId() { 
        return id; 
    }
    
    public void setId(int id) { 
        this.id = id; 
    }

    public MembershipType getType() { 
        return type; 
    }
    
    public void setType(MembershipType type) { 
        this.type = type; 
    }

    public double getDiscountPercent() { 
        return discountPercent; 
    }
    
    public void setDiscountPercent(double discountPercent) { 
        this.discountPercent = discountPercent; 
    }

    public int getPoints() { 
        return points; 
    }
    
    public void setPoints(int points) { 
        this.points = points; 
    }
}