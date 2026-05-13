package main.ui.admin;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import main.dao.SupplierDAO;
import main.model.Supplier;
import main.util.UITheme;

public class SupplierPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private final SupplierDAO supplierDAO = new SupplierDAO();

    public SupplierPanel() {
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

        JLabel lblTitle = new JLabel("Supplier & Vendor Management");
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
        txtSearch.setCaretColor(UITheme.TEXT_PRIMARY);
        txtSearch.setOpaque(true);
        txtSearch.setBorder(UITheme.createRoundedBorder(UITheme.TEXT_SECONDARY, 5, 1));
        
        JButton btnSearch = new JButton("Search");
        UITheme.styleFlatButton(btnSearch, UITheme.PRIMARY_BG, UITheme.HOVER_BG, UITheme.TEXT_PRIMARY);
        btnSearch.setPreferredSize(new Dimension(80, 35));
        
        searchBox.add(txtSearch);
        searchBox.add(Box.createHorizontalStrut(10));
        searchBox.add(btnSearch);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setBackground(UITheme.SECONDARY_BG);

        JButton btnAdd = new JButton("+ Add Supplier");
        UITheme.styleFlatButton(btnAdd, UITheme.SUCCESS, new Color(46, 204, 113), Color.WHITE);
        btnAdd.addActionListener(e -> showAddSupplierDialog());

        JButton btnDelete = new JButton("Delete");
        UITheme.styleFlatButton(btnDelete, UITheme.DANGER, new Color(231, 76, 60), Color.WHITE);
        btnDelete.addActionListener(e -> deleteSelectedSupplier());

        actions.add(btnAdd);
        actions.add(btnDelete);

        toolbar.add(searchBox, BorderLayout.WEST);
        toolbar.add(actions, BorderLayout.EAST);
        headerPanel.add(toolbar, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Updated columns to match your exact DB layout
        String[] columns = {"ID", "Name", "Contact Person", "Phone", "Email", "Address"};
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
                return c;
            }
        });
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Supplier> suppliers = supplierDAO.getAll();
        for (Supplier s : suppliers) {
            tableModel.addRow(new Object[]{
                s.getId(), s.getName(), s.getContactPerson(), s.getPhone(), s.getEmail(), s.getAddress()
            });
        }
    }

    private void showAddSupplierDialog() {
        JTextField txtName = new JTextField(15);
        JTextField txtContact = new JTextField(15);
        JTextField txtPhone = new JTextField(15);
        JTextField txtEmail = new JTextField(15);
        JTextField txtAddress = new JTextField(15);

        // Expanded to 5 rows to fit the address field
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBackground(UITheme.SECONDARY_BG);
        
        addStyledInput(panel, "Supplier Name:", txtName);
        addStyledInput(panel, "Contact Person:", txtContact);
        addStyledInput(panel, "Phone Number:", txtPhone);
        addStyledInput(panel, "Email Address:", txtEmail);
        addStyledInput(panel, "Company Address:", txtAddress);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Supplier", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            if (txtName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Supplier Name is required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Supplier s = new Supplier(0, txtName.getText(), txtContact.getText(), txtPhone.getText(), txtEmail.getText(), txtAddress.getText());
            if (supplierDAO.insert(s)) {
                JOptionPane.showMessageDialog(this, "Supplier added successfully!");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save to database.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedSupplier() {
        int viewRow = table.getSelectedRow();
        if (viewRow >= 0) {
            int modelRow = table.convertRowIndexToModel(viewRow);
            int id = (int) tableModel.getValueAt(modelRow, 0);
            String name = (String) tableModel.getValueAt(modelRow, 1);
            
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete " + name + "?", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                if (supplierDAO.delete(id)) {
                    loadData();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a supplier to delete.");
        }
    }

    private void addStyledInput(JPanel panel, String labelText, JTextField textField) {
        JLabel label = new JLabel(labelText);
        label.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(label);
        panel.add(textField);
    }
}