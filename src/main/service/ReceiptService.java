package main.service;

import java.awt.Component;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import main.model.Member;
import main.model.Order;
import main.model.OrderItem;

public class ReceiptService {

    public String generateReceiptText(Order order, double cashTendered) {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
        double finalAmount = order.getTotalAmount() - order.getDiscountApplied();

        sb.append("========================================\n");
        sb.append("             THE BOOK NOOK              \n");
        sb.append("         Laguna Branch - POS Reg        \n");
        sb.append("========================================\n");
        sb.append(String.format("Receipt No  : %08d\n", order.getId()));
        
        // Failsafe for Date in case it's a fresh order not yet pulled from DB
        String dateStr = (order.getCreatedAt() != null) ? order.getCreatedAt().format(dtf) : java.time.LocalDateTime.now().format(dtf);
        sb.append(String.format("Date        : %s\n", dateStr));
        sb.append(String.format("Cashier     : %s\n", order.getCashier().getUsername()));
        
        // --- FIXED: Explicitly display Customer and Membership Tier ---
        if (order.getCustomer() != null) {
            sb.append(String.format("Customer    : %s\n", order.getCustomer().getFullName()));
            
            // OOP Check: If the customer is a Member, cast it and get the tier!
            if (order.getCustomer() instanceof Member) {
                Member member = (Member) order.getCustomer();
                sb.append(String.format("Membership  : %s\n", member.getMembership().getType().name()));
            }
        }
        
        sb.append("----------------------------------------\n");
        sb.append(String.format("%-22s %-5s %10s\n", "ITEM", "QTY", "AMOUNT"));
        sb.append("----------------------------------------\n");
        
        for (OrderItem item : order.getItems()) {
            String title = item.getBook().getTitle();
            if (title.length() > 20) title = title.substring(0, 19) + ".";
            sb.append(String.format("%-22s %-5d %10.2f\n", title, item.getQuantity(), item.getSubtotal()));
        }
        
        sb.append("----------------------------------------\n");
        sb.append(String.format("%-28s %10.2f\n", "SUBTOTAL:", order.getTotalAmount()));
        
        // --- FIXED: Formatting the discount to show a clear negative value ---
        if (order.getDiscountApplied() > 0) {
            sb.append(String.format("%-28s -%9.2f\n", "DISCOUNT:", order.getDiscountApplied()));
        } else {
            sb.append(String.format("%-28s %10.2f\n", "DISCOUNT:", 0.00));
        }
        
        sb.append("========================================\n");
        sb.append(String.format("%-28s %10.2f\n", "AMOUNT DUE:", finalAmount));
        sb.append("========================================\n");
        sb.append(String.format("%-28s %10.2f\n", "CASH TENDERED:", cashTendered));
        sb.append(String.format("%-28s %10.2f\n", "CHANGE:", (cashTendered - finalAmount)));
        sb.append("\n");
        sb.append("       Official Receipt / Invoice       \n");
        sb.append("        Thank you for shopping!         \n");
        sb.append("========================================\n");

        return sb.toString();
    }

    public void saveReceiptToFile(Order order, double cashTendered) {
        File dir = new File("receipts");
        if (!dir.exists()) dir.mkdirs();

        String filename = "receipts/Receipt_" + String.format("%08d", order.getId()) + ".txt";
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(generateReceiptText(order, cashTendered));
        } catch (IOException e) {
            System.err.println("Failed to save receipt: " + e.getMessage());
        }
    }

    public void printReceiptDialog(Order order, double cashTendered, Component parent) {
        String receipt = generateReceiptText(order, cashTendered);
        JTextArea textArea = new JTextArea(receipt);
        textArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.BOLD, 14));
        textArea.setBackground(new java.awt.Color(250, 250, 250)); 
        textArea.setForeground(java.awt.Color.BLACK);
        textArea.setEditable(false);
        textArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new java.awt.Dimension(380, 500));
        scrollPane.setBorder(BorderFactory.createLineBorder(java.awt.Color.LIGHT_GRAY, 1));

        JOptionPane.showMessageDialog(parent, scrollPane, "Transaction Complete", JOptionPane.PLAIN_MESSAGE);
    }
}