import javax.swing.*;
import java.awt.*;

/**
 * 库存查询面板：显示所有商品的ID、名称、单价、库存
 */
public class InventoryPanel extends JPanel {
    private final Inventory inventory = Inventory.getInstance(); // 单例库存

    public InventoryPanel() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 标题
        JLabel titleLabel = new JLabel("超市商品库存列表", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);

        // 库存表格
        JTextArea inventoryTextArea = new JTextArea();
        inventoryTextArea.setEditable(false);
        inventoryTextArea.setFont(new Font("Monaco", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(inventoryTextArea);
        add(scroll, BorderLayout.CENTER);

        // 构建库存文本
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-10s %-15s %-8s %-8s%n", "商品ID", "商品名称", "单价（元）", "库存数量"));
        sb.append("------------------------------------------------\n");

        // 遍历所有商品（需给Inventory添加getAllProducts()方法，扩展如下）
        for (Product product : inventory.getAllProducts()) {
            sb.append(String.format("%-10s %-15s %-8.2f %-8d%n",
                    product.getProductId(),
                    product.getProductName(),
                    product.getPrice(),
                    product.getStockQuantity()));
        }
        inventoryTextArea.setText(sb.toString());
    }

    /**
     * 扩展Inventory类：添加获取所有商品的方法（在原有Inventory类中新增）
     * 注：此处为了代码完整性，需同步修改Inventory类
     */
    // 在Inventory类中新增：
    // public Collection<Product> getAllProducts() {
    //     return productMap.values();
    // }
}