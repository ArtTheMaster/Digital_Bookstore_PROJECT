package main.service;

import main.dao.OrderDAO;
import main.model.Customer;
import main.model.Member;
import main.model.Order;
import main.model.OrderItem;

/**
 * Handles the business logic for sales transactions, including membership
 * discounts, stock deduction, and receipt generation.
 */
public class SalesService {
    private final OrderDAO orderDAO;
    private final InventoryService inventoryService;
    private final MembershipService membershipService;
    private final ReceiptService receiptService;

    public SalesService() {
        this.orderDAO = new OrderDAO();
        this.inventoryService = new InventoryService();
        this.membershipService = new MembershipService();
        this.receiptService = new ReceiptService();
    }

    /**
     * Processes a checkout transaction.
     * 1. Calculates totals and applies membership discounts.
     * 2. Deducts purchased items from inventory.
     * 3. Updates membership points if applicable.
     * 4. Saves the order and generates a receipt file.
     */
    public boolean processCheckout(Order order, Customer customer, double cashTendered) throws Exception {
        order.setCustomer(customer);
        double total = order.calculateTotal();
        
        // Apply discount polymorphically if the customer is a Member
        double discount = membershipService.calculateDiscount(customer, total);
        order.setDiscountApplied(discount);
        
        double finalAmount = total - discount;
        
        if (cashTendered < finalAmount) {
            throw new Exception("Insufficient cash tendered. Required: ₱" + String.format("%.2f", finalAmount));
        }

        // Deduct stock for each item in the order
        for (OrderItem item : order.getItems()) {
            inventoryService.deductStock(item.getBook().getId(), item.getQuantity());
        }

        // Save order to Database and log the transaction
        if (orderDAO.insertOrder(order)) {
            // Process loyalty points and potential tier upgrades for members
            if (customer instanceof Member) {
                membershipService.processPointsAndUpgrade(customer, finalAmount);
            }
            
            // Save formatted plain text receipt to the receipts/ folder
            receiptService.saveReceiptToFile(order, cashTendered);
            return true;
        }
        
        return false;
    }
}