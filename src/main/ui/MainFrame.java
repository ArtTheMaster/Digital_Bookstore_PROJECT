package main.ui;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import main.model.User;
import main.ui.admin.AdminDashboard;
import main.ui.admin.InventoryPanel;
import main.ui.admin.MembersPanel;
import main.ui.admin.SalesLogPanel;
import main.ui.cashier.CashierPanel;
import main.util.UITheme;

public class MainFrame extends JFrame {
    private final User currentUser;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private final java.util.List<JButton> navButtons = new ArrayList<>();

    public MainFrame(User user) {
        this.currentUser = user;
        setupFrame();
        initComponents();
    }

    private void setupFrame() {
        setTitle("The Book Nook POS - " + currentUser.getRole().name());
        setSize(1280, 800); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void initComponents() {
        // --- Sidebar ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UITheme.PRIMARY_BG);
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, UITheme.SECONDARY_BG), 
            BorderFactory.createEmptyBorder(30, 20, 30, 20)
        ));

        JLabel lblUser = new JLabel("Welcome, " + currentUser.getUsername());
        lblUser.setForeground(UITheme.ACCENT);
        lblUser.setFont(UITheme.FONT_TITLE.deriveFont(20f));
        
        JLabel lblRole = new JLabel("Role: " + currentUser.getRole().name());
        lblRole.setForeground(UITheme.TEXT_SECONDARY);
        lblRole.setFont(UITheme.FONT_SMALL);
        
        sidebar.add(lblUser);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(lblRole);
        sidebar.add(Box.createVerticalStrut(40));

        // --- Main Content Area ---
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UITheme.SECONDARY_BG);

        // Populate Navigation based on Role and WIRE ACTUAL PANELS
        if (currentUser.getRole() == User.Role.ADMIN || currentUser.getRole() == User.Role.MANAGER) {
            addNavButton("Dashboard", sidebar, "DASHBOARD");
            contentPanel.add(new AdminDashboard(), "DASHBOARD");

            addNavButton("Inventory", sidebar, "INVENTORY");
            contentPanel.add(new InventoryPanel(), "INVENTORY");

            addNavButton("Sales Log", sidebar, "SALES");
            contentPanel.add(new SalesLogPanel(), "SALES");
            
            addNavButton("Members", sidebar, "MEMBERS");
            contentPanel.add(new MembersPanel(), "MEMBERS");

            // Admin Only Access
            if (currentUser.getRole() == User.Role.ADMIN) {
                addNavButton("Suppliers", sidebar, "SUPPLIERS");
                contentPanel.add(new main.ui.admin.SupplierPanel(), "SUPPLIERS");
            }
        } // <--- THIS WAS THE MISSING BRACE!

        addNavButton("Point of Sale", sidebar, "POS");
        contentPanel.add(new CashierPanel(currentUser), "POS");

        sidebar.add(Box.createVerticalGlue());
        
        JButton btnLogout = new JButton("Logout");
        UITheme.styleFlatButton(btnLogout, UITheme.DANGER, new Color(200, 50, 40), Color.WHITE);
        btnLogout.setMaximumSize(new Dimension(220, 40));
        btnLogout.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            this.dispose();
        });
        sidebar.add(btnLogout);

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void addNavButton(String text, JPanel sidebar, String cardName) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(220, 45));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        UITheme.styleFlatButton(btn, UITheme.PRIMARY_BG, UITheme.HOVER_BG, UITheme.TEXT_PRIMARY);

        btn.addActionListener(e -> {
            cardLayout.show(contentPanel, cardName);
            setActiveButton(btn);
        });
        
        navButtons.add(btn);
        sidebar.add(btn);
        sidebar.add(Box.createVerticalStrut(10));
    }

    private void setActiveButton(JButton activeBtn) {
        for (JButton btn : navButtons) {
            btn.setForeground(UITheme.TEXT_SECONDARY);
        }
        activeBtn.setForeground(UITheme.ACCENT); 
    }
}