import javax.swing.*;
import java.awt.*;

/**
 * Inventory panel: displays all product IDs, names, prices, and stock quantities
 */
public class InventoryPanel extends JPanel {
    private final Inventory inventory = Inventory.getInstance(); // Singleton inventory

    public InventoryPanel() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Title
        JLabel titleLabel = new JLabel("Product Inventory List", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);

        // Inventory table (simple text area)
        JTextArea inventoryTextArea = new JTextArea();
        inventoryTextArea.setEditable(false);
        inventoryTextArea.setFont(new Font("Monaco", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(inventoryTextArea);
        add(scroll, BorderLayout.CENTER);

        // Build inventory text
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-10s %-15s %-12s %-12s%n", "ID", "Name", "Price (CNY)", "Stock"));
        sb.append("------------------------------------------------\n");

        // Iterate all products
        for (Product product : inventory.getAllProducts()) {
            sb.append(String.format("%-10s %-15s %-12.2f %-12d%n",
                    product.getProductId(),
                    product.getProductName(),
                    product.getPrice(),
                    product.getStockQuantity()));
        }
        inventoryTextArea.setText(sb.toString());
    }
}