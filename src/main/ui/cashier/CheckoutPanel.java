package main.ui.cashier;

import java.awt.*;
import javax.swing.*;
import main.model.Order;
import main.service.ReceiptService;
import main.service.SalesService;
import main.util.UITheme;

public class CheckoutPanel extends JDialog {
    private final Order order;
    private final SalesService salesService;
    private final Runnable onSuccess;
    private JTextField txtCash;

    public CheckoutPanel(JFrame parent, Order order, Runnable onSuccess) {
        super(parent, "Secure Checkout", true);
        this.order = order;
        this.salesService = new SalesService();
        this.onSuccess = onSuccess;
        setupDialog();
        initComponents();
    }

    private void setupDialog() {
        setSize(450, 400);
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(UITheme.PRIMARY_BG);
        setLayout(new BorderLayout());
    }

    private void initComponents() {
        JPanel mainPanel = UITheme.createCardPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("ORDER SUMMARY");
        lblTitle.setFont(UITheme.FONT_TITLE.deriveFont(20f));
        lblTitle.setForeground(UITheme.ACCENT);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTotal = new JLabel(String.format("₱%.2f", order.calculateTotal()));
        lblTotal.setFont(UITheme.FONT_TITLE.deriveFont(36f));
        lblTotal.setForeground(UITheme.SUCCESS);
        lblTotal.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblAmountDue = new JLabel("Amount Due");
        lblAmountDue.setForeground(UITheme.TEXT_SECONDARY);
        lblAmountDue.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        inputPanel.setBackground(UITheme.SECONDARY_BG);
        
        JLabel lblCashLabel = new JLabel("Cash Tendered: ₱");
        lblCashLabel.setForeground(UITheme.TEXT_PRIMARY); 
        lblCashLabel.setFont(UITheme.FONT_BODY);

        txtCash = new JTextField(10);
        txtCash.setBackground(UITheme.PRIMARY_BG);
        txtCash.setForeground(UITheme.TEXT_PRIMARY);
        txtCash.setCaretColor(UITheme.TEXT_PRIMARY);
        txtCash.setFont(UITheme.FONT_TITLE.deriveFont(18f));
        txtCash.setHorizontalAlignment(JTextField.CENTER);
        txtCash.setBorder(BorderFactory.createCompoundBorder(
            UITheme.createRoundedBorder(UITheme.HOVER_BG, 5, 2),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        inputPanel.add(lblCashLabel);
        inputPanel.add(txtCash);

        JButton btnConfirm = new JButton("CONFIRM PAYMENT");
        UITheme.styleFlatButton(btnConfirm, UITheme.ACCENT, UITheme.ACCENT_HOVER, UITheme.PRIMARY_BG);
        btnConfirm.setMaximumSize(new Dimension(300, 50));
        btnConfirm.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnConfirm.addActionListener(e -> processPayment());

        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(lblTitle);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(lblAmountDue);
        mainPanel.add(lblTotal);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(inputPanel);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(btnConfirm);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void processPayment() {
        try {
            double cash = Double.parseDouble(txtCash.getText().trim());
            order.setPaymentMethod("CASH");
            
            if (salesService.processCheckout(order, order.getCustomer(), cash)) {
                
                // POP UP THE RECEIPT!
                ReceiptService receiptService = new ReceiptService();
                receiptService.printReceiptDialog(order, cash, this);
                
                this.dispose();
                onSuccess.run();
                
            } else {
                // FIXED: Actually tell the user if the database save fails!
                JOptionPane.showMessageDialog(this, "System Error: Could not save the transaction to the database.", "Transaction Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid cash amount.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Checkout Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}