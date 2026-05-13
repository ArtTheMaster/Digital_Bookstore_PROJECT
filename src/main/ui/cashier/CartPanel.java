package main.ui.cashier;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import main.dao.CustomerDAO;
import main.model.Book;
import main.model.Customer;
import main.model.Member;
import main.model.Order;
import main.util.UITheme;

public class CartPanel extends JPanel {
    private Order currentOrder;
    private DefaultTableModel cartModel;
    private JLabel lblTotal;
    private JComboBox<Customer> cmbCustomer;
    private final Runnable onProceedToCheckout;
    private final CustomerDAO customerDAO = new CustomerDAO();

    public CartPanel(Order order, Runnable onProceedToCheckout) {
        this.currentOrder = order;
        this.onProceedToCheckout = onProceedToCheckout;
        setupPanel();
        initComponents();
    }

    private void setupPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.SECONDARY_BG);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    }

    private void initComponents() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(UITheme.SECONDARY_BG);
        
        JLabel lblTitle = new JLabel("Shopping Cart");
        lblTitle.setFont(UITheme.FONT_TITLE.deriveFont(18f));
        lblTitle.setForeground(UITheme.ACCENT);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        // --- FIXED: Initialize combo box and load dynamic data safely ---
        cmbCustomer = new JComboBox<>();
        loadCustomers();
        
        cmbCustomer.setBackground(UITheme.PRIMARY_BG);
        cmbCustomer.setForeground(UITheme.TEXT_PRIMARY);
        cmbCustomer.setFocusable(false);
        cmbCustomer.setOpaque(true); 
        
        cmbCustomer.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton("\u25BC"); 
                button.setBackground(UITheme.SECONDARY_BG);
                button.setForeground(UITheme.TEXT_PRIMARY);
                button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                button.setFocusPainted(false);
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                return button;
            }
            
            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                g.setColor(UITheme.PRIMARY_BG);
                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            }
        });

        cmbCustomer.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                setOpaque(true); 
                
                if (isSelected) { setBackground(UITheme.HOVER_BG); } 
                else { setBackground(UITheme.PRIMARY_BG); }

                if (index == -1) { setBackground(UITheme.PRIMARY_BG); }

                if (value instanceof Customer) {
                    Customer c = (Customer) value;
                    if (c instanceof Member) {
                        setText(c.getFullName() + " [" + ((Member) c).getMembership().getType() + "]");
                        setForeground(UITheme.ACCENT); 
                    } else {
                        setText(c.getFullName());
                        setForeground(UITheme.TEXT_PRIMARY); 
                    }
                }
                
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); 
                return this;
            }
        });

        cmbCustomer.addActionListener(e -> {
            Customer selected = (Customer) cmbCustomer.getSelectedItem();
            if (selected != null && selected.getId() != 0) {
                currentOrder.setCustomer(selected);
            } else {
                currentOrder.setCustomer(null);
            }
        });
        
        cmbCustomer.setPreferredSize(new Dimension(220, 35));
        headerPanel.add(cmbCustomer, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        String[] columns = {"Title", "Qty", "Price", "Subtotal"};
        cartModel = new DefaultTableModel(null, columns) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        
        JTable cartTable = new JTable(cartModel);
        styleTable(cartTable);
        JScrollPane scrollPane = new JScrollPane(cartTable);
        scrollPane.getViewport().setBackground(UITheme.PRIMARY_BG);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(UITheme.SECONDARY_BG);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        lblTotal = new JLabel("Total: ₱0.00");
        lblTotal.setFont(UITheme.FONT_TITLE.deriveFont(22f));
        lblTotal.setForeground(UITheme.SUCCESS);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(UITheme.SECONDARY_BG);

        JButton btnClear = new JButton("Clear");
        UITheme.styleFlatButton(btnClear, UITheme.HOVER_BG, UITheme.DANGER, UITheme.TEXT_PRIMARY);
        btnClear.setPreferredSize(new Dimension(80, 40));
        btnClear.addActionListener(e -> clearCart());

        JButton btnCheckout = new JButton("CHECKOUT");
        UITheme.styleFlatButton(btnCheckout, UITheme.ACCENT, UITheme.ACCENT_HOVER, UITheme.PRIMARY_BG);
        btnCheckout.setPreferredSize(new Dimension(140, 40));
        btnCheckout.addActionListener(e -> {
            if (currentOrder.getItems().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Your cart is empty!");
            } else {
                onProceedToCheckout.run();
            }
        });

        btnPanel.add(btnClear);
        btnPanel.add(btnCheckout);

        bottomPanel.add(lblTotal, BorderLayout.WEST);
        bottomPanel.add(btnPanel, BorderLayout.EAST);

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void styleTable(JTable table) {
        table.setBackground(UITheme.PRIMARY_BG);
        table.setForeground(UITheme.TEXT_PRIMARY);
        table.setRowHeight(35);
        table.setGridColor(UITheme.HOVER_BG);
        table.setShowVerticalLines(false);
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(UITheme.SECONDARY_BG);
        header.setForeground(UITheme.ACCENT);
        header.setFont(UITheme.FONT_BODY.deriveFont(Font.BOLD));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.ACCENT));
    }

    // --- NEW METHOD: Allows safe external refreshing of the dropdown ---
    public void loadCustomers() {
        Customer selected = (Customer) cmbCustomer.getSelectedItem();
        
        DefaultComboBoxModel<Customer> model = new DefaultComboBoxModel<>();
        model.addElement(new Customer(0, "Walk-in Customer", "", ""));
        
        List<Customer> customerList = customerDAO.getAll();
        for(Customer c : customerList) {
            model.addElement(c);
        }
        cmbCustomer.setModel(model);
        
        // Try to keep the same person selected after refresh if they still exist
        if (selected != null && selected.getId() != 0) {
            for (int i = 0; i < model.getSize(); i++) {
                if (model.getElementAt(i).getId() == selected.getId()) {
                    cmbCustomer.setSelectedIndex(i);
                    return;
                }
            }
        }
        cmbCustomer.setSelectedIndex(0);
    }

    public void addItemToCart(Book book) {
        String qtyStr = JOptionPane.showInputDialog(this, "Quantity for " + book.getTitle() + ":", "1");
        if (qtyStr != null && !qtyStr.isEmpty()) {
            try {
                int qty = Integer.parseInt(qtyStr);
                if (qty > 0) {
                    currentOrder.addItem(new main.model.OrderItem(book, qty));
                    refreshCart();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid quantity.");
            }
        }
    }

    public void refreshCart() {
        cartModel.setRowCount(0);
        for (main.model.OrderItem item : currentOrder.getItems()) {
            cartModel.addRow(new Object[]{
                item.getBook().getTitle(), item.getQuantity(), 
                String.format("₱%.2f", item.getUnitPrice()), 
                String.format("₱%.2f", item.getSubtotal())
            });
        }
        lblTotal.setText(String.format("Total: ₱%.2f", currentOrder.calculateTotal()));
    }

    public void clearCart() {
        currentOrder.setItems(new ArrayList<>());
        cmbCustomer.setSelectedIndex(0); 
        refreshCart();
    }
}