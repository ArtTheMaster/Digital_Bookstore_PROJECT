package main.model;

public class Member extends Customer {
    private Membership membership;

    public Member(int id, String name, String email, String phone, Membership membership) {
        super(id, name, email, phone); 
        this.membership = membership;
    }

    @Override 
    public double applyDiscount(double total) { 
        return total * this.membership.getDiscountPercent(); 
    }

    public Membership getMembership() { 
        return membership; 
    }
    
    public void setMembership(Membership membership) { 
        this.membership = membership; 
    }
}