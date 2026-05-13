package main.ui.admin;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import main.dao.CustomerDAO;
import main.model.Customer;
import main.model.Member;
import main.util.InputValidator;
import main.util.UITheme;

public class MembersPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private final CustomerDAO customerDAO = new CustomerDAO();

    public MembersPanel() {
        setupPanel();
        initComponents();
        loadData();
    }

    private void setupPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.SECONDARY_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    private void initComponents() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.SECONDARY_BG);

        JLabel lblTitle = new JLabel("Membership Management");
        lblTitle.setForeground(UITheme.ACCENT);
        lblTitle.setFont(UITheme.FONT_TITLE);
        headerPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(UITheme.SECONDARY_BG);
        toolbar.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));

        JPanel searchBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchBox.setBackground(UITheme.SECONDARY_BG);
        JTextField txtSearch = new JTextField(20);
        txtSearch.setPreferredSize(new Dimension(250, 35));
        txtSearch.setBackground(UITheme.PRIMARY_BG);
        txtSearch.setForeground(UITheme.TEXT_PRIMARY);
        txtSearch.setBorder(UITheme.createRoundedBorder(UITheme.TEXT_SECONDARY, 5, 1));
        
        JButton btnSearch = new JButton("Search");
        UITheme.styleFlatButton(btnSearch, UITheme.PRIMARY_BG, UITheme.HOVER_BG, UITheme.TEXT_PRIMARY);
        btnSearch.setPreferredSize(new Dimension(80, 35));
        
        searchBox.add(txtSearch);
        searchBox.add(Box.createHorizontalStrut(10));
        searchBox.add(btnSearch);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setBackground(UITheme.SECONDARY_BG);

        JButton btnAdd = new JButton("+ Add Customer");
        UITheme.styleFlatButton(btnAdd, UITheme.SUCCESS, new Color(46, 204, 113), Color.WHITE);
        btnAdd.addActionListener(e -> showAddCustomerDialog());

        JButton btnEdit = new JButton("Manage Membership");
        UITheme.styleFlatButton(btnEdit, UITheme.ACCENT, UITheme.ACCENT_HOVER, UITheme.PRIMARY_BG);
        btnEdit.addActionListener(e -> showEditMembershipDialog());

        actions.add(btnAdd);
        actions.add(btnEdit);

        toolbar.add(searchBox, BorderLayout.WEST);
        toolbar.add(actions, BorderLayout.EAST);
        headerPanel.add(toolbar, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Name", "Phone", "Status", "Tier", "Points"};
        tableModel = new DefaultTableModel(null, columns) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(tableModel);
        styleTable(table);

        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { search(txtSearch.getText()); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { search(txtSearch.getText()); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { search(txtSearch.getText()); }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(UITheme.PRIMARY_BG);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel tableWrapper = UITheme.createCardPanel();
        tableWrapper.setLayout(new BorderLayout());
        tableWrapper.add(scrollPane, BorderLayout.CENTER);
        add(tableWrapper, BorderLayout.CENTER);
    }

    private void search(String str) {
        if (str.trim().isEmpty()) rowSorter.setRowFilter(null);
        else rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + str));
    }

    private void styleTable(JTable table) {
        table.setBackground(UITheme.PRIMARY_BG);
        table.setForeground(UITheme.TEXT_PRIMARY);
        table.setRowHeight(40);
        table.setGridColor(UITheme.HOVER_BG);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(UITheme.HOVER_BG);
        table.setSelectionForeground(UITheme.ACCENT);

        JTableHeader header = table.getTableHeader();
        header.setBackground(UITheme.SECONDARY_BG);
        header.setForeground(UITheme.ACCENT);
        header.setFont(UITheme.FONT_BODY.deriveFont(Font.BOLD));
        header.setPreferredSize(new Dimension(100, 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.ACCENT));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                
                String status = (String) table.getModel().getValueAt(row, 3);
                if (!isSelected) {
                    c.setForeground(status.equals("MEMBER") ? UITheme.SUCCESS : UITheme.TEXT_PRIMARY);
                }
                return c;
            }
        });
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Customer> customers = customerDAO.getAll();
        for (Customer c : customers) {
            if (c instanceof Member) {
                Member m = (Member) c;
                tableModel.addRow(new Object[]{m.getId(), m.getFullName(), m.getPhone(), "MEMBER", m.getMembership().getType().name(), m.getMembership().getPoints()});
            } else {
                tableModel.addRow(new Object[]{c.getId(), c.getFullName(), c.getPhone(), "WALK-IN", "-", "-"});
            }
        }
    }

    private void showAddCustomerDialog() {
        JTextField txtName = new JTextField(15);
        JTextField txtPhone = new JTextField(15);
        JCheckBox chkMember = new JCheckBox("Enroll as Member?");
        chkMember.setBackground(UITheme.SECONDARY_BG);
        chkMember.setForeground(UITheme.TEXT_PRIMARY);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBackground(UITheme.SECONDARY_BG);
        
        addStyledInput(panel, "Full Name:", txtName);
        addStyledInput(panel, "Phone (09...):", txtPhone);
        panel.add(new JLabel(""));
        panel.add(chkMember);

        int result = JOptionPane.showConfirmDialog(this, panel, "Register New Customer", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            if (!InputValidator.isValidPhone(txtPhone.getText())) {
                JOptionPane.showMessageDialog(this, "Invalid phone format.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Customer c = new Customer(0, txtName.getText(), "", txtPhone.getText());
            if (customerDAO.insertCustomer(c, chkMember.isSelected(), chkMember.isSelected() ? "SILVER" : null, 0)) {
                JOptionPane.showMessageDialog(this, "Customer added successfully!");
                loadData();
            }
        }
    }

    private void showEditMembershipDialog() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Select a customer to manage.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);
        int id = (int) tableModel.getValueAt(modelRow, 0);
        String name = (String) tableModel.getValueAt(modelRow, 1);
        String status = (String) tableModel.getValueAt(modelRow, 3);
        String tier = (String) tableModel.getValueAt(modelRow, 4);
        String pointsStr = tableModel.getValueAt(modelRow, 5).toString();

        JCheckBox chkMember = new JCheckBox("Is Member?", status.equals("MEMBER"));
        chkMember.setBackground(UITheme.SECONDARY_BG);
        chkMember.setForeground(UITheme.TEXT_PRIMARY);

        JComboBox<String> comboTier = new JComboBox<>(new String[]{"SILVER", "GOLD", "PLATINUM"});
        comboTier.setSelectedItem(tier.equals("-") ? "SILVER" : tier);

        JTextField txtPoints = new JTextField(pointsStr.equals("-") ? "0" : pointsStr, 10);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBackground(UITheme.SECONDARY_BG);
        panel.add(new JLabel("Customer:"));
        JLabel nameLbl = new JLabel(name);
        nameLbl.setForeground(UITheme.ACCENT);
        panel.add(nameLbl);
        panel.add(new JLabel(""));
        panel.add(chkMember);
        addStyledCombo(panel, "Tier:", comboTier);
        addStyledInput(panel, "Points:", txtPoints);

        int result = JOptionPane.showConfirmDialog(this, panel, "Manage Membership", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                boolean isMem = chkMember.isSelected();
                String selTier = (String) comboTier.getSelectedItem();
                double pts = Double.parseDouble(txtPoints.getText().trim());

                if (!isMem && (pts > 0 || !selTier.equals("SILVER"))) {
                    JOptionPane.showMessageDialog(this, "You must check the 'Is Member?' box to assign a tier or points.", "Membership Required", JOptionPane.WARNING_MESSAGE);
                    return; 
                }

                if (!isMem) {
                    selTier = null;
                    pts = 0;
                }

                // --- FIXED: ADDED ELSE BLOCK TO CATCH DB ERRORS ---
                if (customerDAO.updateMembership(id, isMem, selTier, pts)) {
                    loadData();
                    JOptionPane.showMessageDialog(this, "Membership updated successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Database Error! Could not save changes. Make sure you ran the SQL command to allow PLATINUM tiers.", "Update Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Points must be a valid number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addStyledInput(JPanel panel, String labelText, JTextField textField) {
        JLabel label = new JLabel(labelText);
        label.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(label);
        panel.add(textField);
    }

    private void addStyledCombo(JPanel panel, String labelText, JComboBox<String> combo) {
        JLabel label = new JLabel(labelText);
        label.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(label);
        panel.add(combo);
    }
}