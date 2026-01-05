import java.util.ArrayList;
import java.util.List;

/**
 * Checkout class: core business logic (handles sales/returns)
 */
public class Checkout {
    private Inventory inventory;  // Depends on inventory management class
    private List<ShoppingItem> currentItems;  // Shopping items in current transaction

    public Checkout() {
        this.inventory = Inventory.getInstance();  // Dependency injection (singleton)
        this.currentItems = new ArrayList<>();
    }

    // Add product to current transaction (sale: quantity positive; return: quantity negative)
    public void addItem(String productId, int quantity) {
        // 1. Validate product exists
        Product product = inventory.getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product ID not found: " + productId);
        }

        // 2. Validate stock (sale: stock ≥ purchase qty; return: qty ≥ 0)
        if (quantity > 0) {  // Sale scenario
            if (product.getStockQuantity() < quantity) {
                throw new IllegalArgumentException("Product \u300c" + product.getProductName() + "\u300d out of stock, current stock: " + product.getStockQuantity());
            }
        } else if (quantity < 0) {  // Return scenario (quantity negative, take absolute value)
            if (Math.abs(quantity) <= 0) {
                throw new IllegalArgumentException("Return quantity must be greater than 0");
            }
        }

        // 3. Add to shopping list (update quantity if product already exists)
        boolean exists = false;
        for (int i = 0; i < currentItems.size(); i++) {
            ShoppingItem item = currentItems.get(i);
            if (item.getProduct().getProductId().equals(productId)) {
                currentItems.set(i, new ShoppingItem(product, item.getQuantity() + quantity));
                exists = true;
                break;
            }
        }
        if (!exists) {
            currentItems.add(new ShoppingItem(product, quantity));
        }
    }

    // Process payment (cash only, validate cashAmount ≥ total)
    public Receipt processPayment(double cashAmount) {
        double total = calculateTotalAmount();
        if (cashAmount < total) {
            throw new IllegalArgumentException("Insufficient payment! Due: " + total + " CNY, paid: " + cashAmount + " CNY");
        }

        // 1. Update stock (sale: decrease; return: increase)
        for (ShoppingItem item : currentItems) {
            Product product = item.getProduct();
            product.updateStock(-item.getQuantity());  // Sale: stock - qty; Return: stock - (negative) = +qty
        }

        // 2. Generate sale receipt
        Receipt receipt = new Receipt(currentItems, "SALE");
        // 3. Clear current transaction
        currentItems.clear();
        return receipt;
    }

    // Process return (generate return receipt and update stock)
    public Receipt processReturn() {
        double totalRefund = calculateTotalAmount();  // Negative value, abs() is refund amount
        if (totalRefund >= 0) {
            throw new IllegalArgumentException("Return item quantity must be negative!");
        }

        // 1. Update stock (return: increase)
        for (ShoppingItem item : currentItems) {
            Product product = item.getProduct();
            product.updateStock(-item.getQuantity());  // Quantity negative, -quantity positive => stock increases
        }

        // 2. Generate return receipt
        Receipt receipt = new Receipt(currentItems, "RETURN");
        // 3. Clear current transaction
        currentItems.clear();
        return receipt;
    }

    // Calculate total amount of current transaction
    public double calculateTotalAmount() {
        return currentItems.stream()
                .mapToDouble(ShoppingItem::calculateItemTotal)
                .sum();
    }
    // Get shopping items (for GUI to display item details)
    public List<ShoppingItem> getCurrentItems() {
        return currentItems;
    }

    // Cancel current transaction
    public void cancelTransaction() {
        currentItems.clear();
        System.out.println("Transaction cancelled!");
    }
}