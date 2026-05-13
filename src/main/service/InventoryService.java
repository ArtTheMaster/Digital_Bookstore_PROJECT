package main.service;

import java.util.List;
import main.dao.InventoryDAO;
import main.model.Inventory;

public class InventoryService {
    private final InventoryDAO inventoryDAO;

    public InventoryService() {
        this.inventoryDAO = new InventoryDAO();
    }

    public List<Inventory> getLowStockAlerts() {
        return inventoryDAO.getLowStockItems();
    }

    public void restockItem(int bookId, int amountAdded) throws Exception {
        if (amountAdded <= 0) {
            throw new Exception("Restock amount must be positive.");
        }
        
        Inventory item = inventoryDAO.getByBookId(bookId);
        if (item != null) {
            inventoryDAO.updateStock(bookId, item.getQuantity() + amountAdded);
        } else {
            throw new Exception("Book inventory record not found.");
        }
    }

    public void deductStock(int bookId, int amountPurchased) throws Exception {
        Inventory item = inventoryDAO.getByBookId(bookId);
        
        if (item != null) {
            if (item.getQuantity() < amountPurchased) {
                throw new Exception("Insufficient stock for book ID: " + bookId);
            }
            inventoryDAO.updateStock(bookId, item.getQuantity() - amountPurchased);
        } else {
            throw new Exception("Inventory record missing for Book ID: " + bookId);
        }
    }
}