package main.ui.admin;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import main.dao.OrderDAO;
import main.model.Order;
import main.util.UITheme;

public class SalesLogPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private final OrderDAO orderDAO = new OrderDAO();

    public SalesLogPanel() {
        setupPanel();
        initComponents();
        loadRealData(); // Load actual data on startup
    }

    private void setupPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.SECONDARY_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    private void initComponents() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        toolbar.setBackground(UITheme.SECONDARY_BG);
        
        JLabel lblTitle = new JLabel("Sales Log");
        lblTitle.setForeground(UITheme.ACCENT);
        lblTitle.setFont(UITheme.FONT_TITLE);
        
        // --- Added a Refresh Button for convenience ---
        JButton btnRefresh = new JButton("Refresh Log");
        UITheme.styleFlatButton(btnRefresh, UITheme.SUCCESS, new Color(46, 204, 113), Color.WHITE);
        btnRefresh.addActionListener(e -> loadRealData());

        JButton btnExport = new JButton("Export to TXT");
        UITheme.styleFlatButton(btnExport, UITheme.PRIMARY_BG, UITheme.HOVER_BG, UITheme.TEXT_PRIMARY);
        btnExport.addActionListener(e -> exportTableToTextFile());
        
        toolbar.add(lblTitle);
        toolbar.add(Box.createHorizontalStrut(20));
        toolbar.add(btnRefresh);
        toolbar.add(btnExport);
        add(toolbar, BorderLayout.NORTH);

        String[] columns = {"Order ID", "Date & Time", "Customer", "Cashier", "Gross Amt", "Discount", "Net Total", "Payment"};
        tableModel = new DefaultTableModel(null, columns) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        
        table = new JTable(tableModel);
        styleTable(table);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(UITheme.PRIMARY_BG);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        JPanel tableContainer = UITheme.createCardPanel();
        tableContainer.setLayout(new BorderLayout());
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        add(tableContainer, BorderLayout.CENTER);
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
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.ACCENT));
    }

    // --- NEW: FILLS THE TABLE WITH REAL DATABASE ROWS ---
    private void loadRealData() {
        tableModel.setRowCount(0); // Clear current table
        List<Order> orders = orderDAO.getAllOrders();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
        
        for (Order o : orders) {
            String customerName = o.getCustomer() != null ? o.getCustomer().getFullName() : "Walk-in";
            double netTotal = o.getTotalAmount() - o.getDiscountApplied();
            
            tableModel.addRow(new Object[]{
                o.getId(), 
                o.getCreatedAt() != null ? o.getCreatedAt().format(dtf) : "N/A", 
                customerName, 
                o.getCashier().getUsername(),
                String.format("₱%.2f", o.getTotalAmount()), 
                String.format("₱%.2f", o.getDiscountApplied()),
                String.format("₱%.2f", netTotal), 
                o.getPaymentMethod()
            });
        }
    }

    private void exportTableToTextFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Sales Log");
        fileChooser.setSelectedFile(new File("SalesLog_Export.txt"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (FileWriter bw = new FileWriter(fileToSave)) {
                for (int i = 0; i < table.getColumnCount(); i++) {
                    bw.write(table.getColumnName(i) + "\t|\t");
                }
                bw.write("\n----------------------------------------------------------------------------------------\n");
                for (int i = 0; i < table.getRowCount(); i++) {
                    for (int j = 0; j < table.getColumnCount(); j++) {
                        bw.write(table.getValueAt(i, j).toString() + "\t|\t");
                    }
                    bw.write("\n");
                }
                JOptionPane.showMessageDialog(this, "Export Successful!\nSaved to: " + fileToSave.getAbsolutePath());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error writing to file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}