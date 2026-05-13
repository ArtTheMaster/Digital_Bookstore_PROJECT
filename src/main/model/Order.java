package main.model;

import java.time.LocalDateTime;
import java.util.ArrayList; 
import java.util.List;

public class Order {
    private int id; 
    private Customer customer; 
    private User cashier; 
    private List<OrderItem> items;
    private double totalAmount;
    private double discountApplied; 
    private String paymentMethod; 
    private LocalDateTime createdAt;

    public Order() {
        this.items = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
    }

    public void addItem(OrderItem item) { 
        this.items.add(item); 
    }

    public double calculateTotal() { 
        this.totalAmount = 0.0;
        for (OrderItem item : this.items) {
            this.totalAmount += item.getSubtotal();
        }
        return this.totalAmount;
    }

    public int getId() { 
        return id; 
    }
    
    public void setId(int id) { 
        this.id = id; 
    }

    public Customer getCustomer() { 
        return customer; 
    }
    
    public void setCustomer(Customer customer) { 
        this.customer = customer; 
    }

    public User getCashier() { 
        return cashier; 
    }
    
    public void setCashier(User cashier) { 
        this.cashier = cashier; 
    }

    public List<OrderItem> getItems() { 
        return items; 
    }
    
    public void setItems(List<OrderItem> items) { 
        this.items = items; 
    }

    public double getTotalAmount() { 
        return totalAmount; 
    }
    
    public void setTotalAmount(double totalAmount) { 
        this.totalAmount = totalAmount; 
    }

    public double getDiscountApplied() { 
        return discountApplied; 
    }
    
    public void setDiscountApplied(double discountApplied) { 
        this.discountApplied = discountApplied; 
    }

    public String getPaymentMethod() { 
        return paymentMethod; 
    }
    
    public void setPaymentMethod(String paymentMethod) { 
        this.paymentMethod = paymentMethod; 
    }

    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }
    
    public void setCreatedAt(LocalDateTime createdAt) { 
        this.createdAt = createdAt; 
    }
}