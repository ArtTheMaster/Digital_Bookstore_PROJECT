package main.ui.admin;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import main.dao.BookDAO;
import main.dao.DatabaseConnection;
import main.dao.InventoryDAO;
import main.model.Book;
import main.model.Inventory;
import main.util.InputValidator;
import main.util.UITheme;

public class InventoryPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter; // NEW: Added Sorter
    private final BookDAO bookDAO = new BookDAO();
    private final InventoryDAO inventoryDAO = new InventoryDAO();

    public InventoryPanel() {
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

        JLabel lblTitle = new JLabel("Inventory Management");
        lblTitle.setForeground(UITheme.ACCENT);
        lblTitle.setFont(UITheme.FONT_TITLE);
        headerPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(UITheme.SECONDARY_BG);
        toolbar.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));

        // Search Bar
        JPanel searchBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchBox.setBackground(UITheme.SECONDARY_BG);
        JTextField txtSearch = new JTextField(20);
        txtSearch.setPreferredSize(new Dimension(250, 35));
        txtSearch.setBackground(UITheme.PRIMARY_BG);
        txtSearch.setForeground(UITheme.TEXT_PRIMARY);
        txtSearch.setCaretColor(UITheme.TEXT_PRIMARY);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            UITheme.createRoundedBorder(UITheme.TEXT_SECONDARY, 5, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        JButton btnSearch = new JButton("Search");
        UITheme.styleFlatButton(btnSearch, UITheme.PRIMARY_BG, UITheme.HOVER_BG, UITheme.TEXT_PRIMARY);
        btnSearch.setPreferredSize(new Dimension(80, 35));
        
        searchBox.add(txtSearch);
        searchBox.add(Box.createHorizontalStrut(10));
        searchBox.add(btnSearch);

        // Actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setBackground(UITheme.SECONDARY_BG);

        JButton btnAdd = new JButton("+ Add Book");
        UITheme.styleFlatButton(btnAdd, UITheme.SUCCESS, new Color(46, 204, 113), Color.WHITE);
        btnAdd.addActionListener(e -> showAddBookDialog());

        JButton btnRestock = new JButton("Restock Selected");
        UITheme.styleFlatButton(btnRestock, UITheme.ACCENT, UITheme.ACCENT_HOVER, UITheme.PRIMARY_BG);
        btnRestock.addActionListener(e -> restockSelectedBook());

        JButton btnDelete = new JButton("Delete");
        UITheme.styleFlatButton(btnDelete, UITheme.DANGER, new Color(231, 76, 60), Color.WHITE);
        btnDelete.addActionListener(e -> deleteSelectedBook());

        actions.add(btnAdd);
        actions.add(btnRestock);
        actions.add(btnDelete);

        toolbar.add(searchBox, BorderLayout.WEST);
        toolbar.add(actions, BorderLayout.EAST);
        headerPanel.add(toolbar, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Table Initialization
        String[] columns = {"ID", "Title", "Author", "Genre", "Price", "Stock", "Threshold"};
        tableModel = new DefaultTableModel(null, columns) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(tableModel);
        styleTable(table);

        // --- NEW: Wire up the Search Filter ---
        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        DocumentListener searchListener = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { executeSearch(txtSearch.getText()); }
            @Override public void removeUpdate(DocumentEvent e) { executeSearch(txtSearch.getText()); }
            @Override public void changedUpdate(DocumentEvent e) { executeSearch(txtSearch.getText()); }
        };
        txtSearch.getDocument().addDocumentListener(searchListener);
        btnSearch.addActionListener(e -> executeSearch(txtSearch.getText()));
        // --------------------------------------

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(UITheme.PRIMARY_BG);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel tableWrapper = UITheme.createCardPanel();
        tableWrapper.setLayout(new BorderLayout());
        tableWrapper.add(scrollPane, BorderLayout.CENTER);

        add(tableWrapper, BorderLayout.CENTER);
    }

    // --- NEW: Execute Search Method ---
    private void executeSearch(String query) {
        if (query.trim().isEmpty()) {
            rowSorter.setRowFilter(null);
            table.clearSelection();
        } else {
            // Case-insensitive regex filter
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + query.trim()));
            if (table.getRowCount() > 0) {
                table.setRowSelectionInterval(0, 0); // Highlight top result automatically
            }
        }
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
                
                int stock = Integer.parseInt(table.getModel().getValueAt(row, 5).toString());
                int threshold = Integer.parseInt(table.getModel().getValueAt(row, 6).toString());
                if (!isSelected) {
                    c.setForeground(stock <= threshold ? UITheme.DANGER : UITheme.TEXT_PRIMARY);
                }
                return c;
            }
        });
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Inventory> items = inventoryDAO.getAll();
        for (Inventory inv : items) {
            Book b = inv.getBook();
            tableModel.addRow(new Object[]{
                b.getId(), b.getTitle(), b.getAuthor(), b.getGenre(), 
                String.format("₱%.2f", b.getPrice()), inv.getQuantity(), inv.getLowStockThreshold()
            });
        }
    }

    private void restockSelectedBook() {
        int viewRow = table.getSelectedRow();
        if (viewRow >= 0) {
            // FIXED: Convert visual row to actual database row to prevent errors when filtering!
            int modelRow = table.convertRowIndexToModel(viewRow);
            
            int bookId = (int) tableModel.getValueAt(modelRow, 0);
            String title = (String) tableModel.getValueAt(modelRow, 1);
            int currentStock = (int) tableModel.getValueAt(modelRow, 5);

            String input = JOptionPane.showInputDialog(this, 
                "Restock quantity for: " + title + "\nCurrent Stock: " + currentStock + "\n\nEnter amount to add:", 
                "Restock Inventory", 
                JOptionPane.QUESTION_MESSAGE);
            
            if (input != null && !input.trim().isEmpty()) {
                try {
                    int addedQty = Integer.parseInt(input.trim());
                    if (addedQty <= 0) {
                        JOptionPane.showMessageDialog(this, "Quantity must be greater than 0.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    if (inventoryDAO.updateStock(bookId, currentStock + addedQty)) {
                        JOptionPane.showMessageDialog(this, "Successfully added " + addedQty + " units to " + title + ".");
                        loadData(); 
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to update database.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid whole number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a book from the table to restock.", "Selection Required", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showAddBookDialog() {
        JTextField txtTitle = new JTextField(15);
        JTextField txtAuthor = new JTextField(15);
        JTextField txtGenre = new JTextField(15);
        JTextField txtPrice = new JTextField(15);
        JTextField txtStock = new JTextField("10", 15);
        JTextField txtThreshold = new JTextField("5", 15);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBackground(UITheme.SECONDARY_BG);
        
        addStyledInput(panel, "Title:", txtTitle);
        addStyledInput(panel, "Author:", txtAuthor);
        addStyledInput(panel, "Genre:", txtGenre);
        addStyledInput(panel, "Price (₱):", txtPrice);
        addStyledInput(panel, "Initial Stock:", txtStock);
        addStyledInput(panel, "Low Alert Threshold:", txtThreshold);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Book", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                if (!InputValidator.isValidPrice(txtPrice.getText()) || !InputValidator.isValidQuantity(txtStock.getText())) {
                    throw new Exception("Invalid price or stock values.");
                }
                
                Book newBook = new Book(0, txtTitle.getText(), txtAuthor.getText(), txtGenre.getText(), "N/A", Double.parseDouble(txtPrice.getText()), "");
                
                if (bookDAO.insert(newBook)) {
                    try (Connection conn = DatabaseConnection.getConnection();
                         PreparedStatement stmt = conn.prepareStatement("INSERT INTO inventory (book_id, quantity, low_stock_threshold) VALUES (?, ?, ?)")) {
                        stmt.setInt(1, newBook.getId());
                        stmt.setInt(2, Integer.parseInt(txtStock.getText()));
                        stmt.setInt(3, Integer.parseInt(txtThreshold.getText()));
                        stmt.executeUpdate();
                    }
                    JOptionPane.showMessageDialog(this, "Book added successfully!");
                    loadData();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedBook() {
        int viewRow = table.getSelectedRow();
        if (viewRow >= 0) {
            // FIXED: Convert visual row to actual database row
            int modelRow = table.convertRowIndexToModel(viewRow);
            int bookId = (int) tableModel.getValueAt(modelRow, 0);
            
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this book? This will remove it from inventory.", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (bookDAO.delete(bookId)) {
                    loadData();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a book to delete.");
        }
    }

    private void addStyledInput(JPanel panel, String labelText, JTextField textField) {
        JLabel label = new JLabel(labelText);
        label.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(label);
        panel.add(textField);
    }
}