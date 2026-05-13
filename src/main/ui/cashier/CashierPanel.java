package main.ui.cashier;

import java.awt.*;
import javax.swing.*;
import main.model.Order;
import main.model.User;
import main.ui.shared.BookSearchPanel;
import main.util.UITheme;

public class CashierPanel extends JPanel {

    private final User cashier;
    private Order currentOrder;
    private CartPanel cartPanel;
    private BookSearchPanel searchPanel; // Lifted to class level so we can access it

    public CashierPanel(User cashier) {
        this.cashier = cashier;
        this.currentOrder = new Order();
        this.currentOrder.setCashier(cashier);
        setupPanel();
        initComponents();
    }

    private void setupPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.PRIMARY_BG);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    }

    private void initComponents() {
        // Left side: Book Search
        searchPanel = new BookSearchPanel(book -> {
            cartPanel.addItemToCart(book);
        });
        
        // Right side: Cart
        cartPanel = new CartPanel(currentOrder, () -> {
            CheckoutPanel checkout = new CheckoutPanel(
                (JFrame) SwingUtilities.getWindowAncestor(this), 
                currentOrder, 
                this::resetTransaction
            );
            checkout.setVisible(true);
        });

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, searchPanel, cartPanel);
        splitPane.setResizeWeight(0.6); // 60% left, 40% right
        splitPane.setDividerSize(5);
        splitPane.setBorder(null);

        add(splitPane, BorderLayout.CENTER);

        // --- FIXED: The Magic Listener that Auto-Refreshes the POS ---
        // Every time this tab is clicked and shown, it grabs the newest data from the DB
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                searchPanel.loadAllBooks();
                cartPanel.loadCustomers();
            }
        });
    }

    private void resetTransaction() {
        currentOrder = new Order();
        currentOrder.setCashier(cashier);
        cartPanel.clearCart();
    }
}