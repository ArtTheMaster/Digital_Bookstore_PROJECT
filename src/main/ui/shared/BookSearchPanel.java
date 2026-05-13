package main.ui.shared;

import java.awt.*;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import main.dao.BookDAO;
import main.model.Book;
import main.util.UITheme;

public class BookSearchPanel extends JPanel {
    private final BookDAO bookDAO = new BookDAO();
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private final Consumer<Book> onAddBookCallback;

    public BookSearchPanel(Consumer<Book> onAddBookCallback) {
        this.onAddBookCallback = onAddBookCallback;
        setupPanel();
        initComponents();
        loadAllBooks();
    }

    private void setupPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.SECONDARY_BG);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void initComponents() {
        JPanel searchBarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchBarPanel.setBackground(UITheme.SECONDARY_BG);

        JLabel lblQuickSearch = new JLabel("Quick Search:");
        lblQuickSearch.setForeground(UITheme.TEXT_PRIMARY);

        JTextField txtSearch = new JTextField(15);
        txtSearch.setPreferredSize(new Dimension(200, 35));
        txtSearch.setBackground(UITheme.PRIMARY_BG);
        txtSearch.setForeground(UITheme.TEXT_PRIMARY);
        txtSearch.setCaretColor(UITheme.TEXT_PRIMARY);
        txtSearch.setOpaque(true); 
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            UITheme.createRoundedBorder(UITheme.TEXT_SECONDARY, 5, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JButton btnSearch = new JButton("Search");
        UITheme.styleFlatButton(btnSearch, UITheme.HOVER_BG, UITheme.PRIMARY_BG, UITheme.TEXT_PRIMARY);
        btnSearch.setPreferredSize(new Dimension(80, 35));

        searchBarPanel.add(lblQuickSearch); 
        searchBarPanel.add(txtSearch);
        searchBarPanel.add(btnSearch);

        String[] columns = {"ID", "Title", "Author", "Price"};
        tableModel = new DefaultTableModel(null, columns) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        resultTable = new JTable(tableModel);
        styleTable(resultTable);
        
        rowSorter = new TableRowSorter<>(tableModel);
        resultTable.setRowSorter(rowSorter);

        DocumentListener searchListener = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { executeSearch(txtSearch.getText()); }
            @Override public void removeUpdate(DocumentEvent e) { executeSearch(txtSearch.getText()); }
            @Override public void changedUpdate(DocumentEvent e) { executeSearch(txtSearch.getText()); }
        };
        txtSearch.getDocument().addDocumentListener(searchListener);
        btnSearch.addActionListener(e -> executeSearch(txtSearch.getText()));

        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.getViewport().setBackground(UITheme.PRIMARY_BG);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JButton btnAdd = new JButton("ADD TO CART");
        UITheme.styleFlatButton(btnAdd, UITheme.SUCCESS, new Color(46, 204, 113), Color.WHITE);
        btnAdd.setPreferredSize(new Dimension(150, 40));
        btnAdd.addActionListener(e -> {
            int viewRow = resultTable.getSelectedRow();
            if (viewRow != -1) {
                int modelRow = resultTable.convertRowIndexToModel(viewRow);
                int id = (int) tableModel.getValueAt(modelRow, 0);
                onAddBookCallback.accept(bookDAO.getById(id));
            } else {
                JOptionPane.showMessageDialog(this, "Please select a book to add to the cart.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            }
        });

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(UITheme.SECONDARY_BG);
        footer.add(btnAdd);

        add(searchBarPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }

    private void executeSearch(String query) {
        if (query.trim().isEmpty()) {
            rowSorter.setRowFilter(null);
            resultTable.clearSelection();
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + query.trim()));
            if (resultTable.getRowCount() > 0) {
                resultTable.setRowSelectionInterval(0, 0);
            }
        }
    }

    private void styleTable(JTable table) {
        table.setBackground(UITheme.PRIMARY_BG);
        table.setForeground(UITheme.TEXT_PRIMARY);
        table.setRowHeight(35);
        table.setGridColor(UITheme.HOVER_BG);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(UITheme.HOVER_BG);
        table.setSelectionForeground(UITheme.ACCENT);
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(UITheme.SECONDARY_BG);
        header.setForeground(UITheme.ACCENT);
        header.setFont(UITheme.FONT_BODY.deriveFont(Font.BOLD));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.ACCENT));
    }

    // FIXED: Made public so the Cashier Panel can trigger a refresh!
    public void loadAllBooks() {
        tableModel.setRowCount(0);
        for (Book b : bookDAO.getAll()) {
            tableModel.addRow(new Object[]{b.getId(), b.getTitle(), b.getAuthor(), String.format("₱%.2f", b.getPrice())});
        }
    }
}