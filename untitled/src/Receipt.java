import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 收据类：生成销售/退货小票
 */
public class Receipt {
    private String receiptId;          // 收据唯一ID
    private List<ShoppingItem> items;  // 购物项列表
    private double totalAmount;        // 总金额（销售=正数，退货=负数）
    private Date transactionTime;      // 交易时间
    private String transactionType;    // 交易类型（SALE=销售，RETURN=退货）

    // 构造方法（生成收据）
    public Receipt(List<ShoppingItem> items, String transactionType) {
        this.receiptId = generateReceiptId();
        this.items = items;
        this.transactionType = transactionType;
        this.transactionTime = new Date();
        this.totalAmount = calculateTotalAmount();
    }

    // 生成唯一收据ID（时间戳+随机数）
    private String generateReceiptId() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timeStr = sdf.format(new Date());
        int randomNum = (int) (Math.random() * 1000);
        return timeStr + String.format("%03d", randomNum);
    }

    // 计算总金额
    private double calculateTotalAmount() {
        return items.stream()
                .mapToDouble(ShoppingItem::calculateItemTotal)
                .sum();
    }

    // 打印收据（控制台输出，实际项目可导出为PDF）
    public void printReceipt() {
        System.out.println("======================================");
        System.out.println("          超市POS系统 - 收据");
        System.out.println("======================================");
        System.out.println("收据ID: " + receiptId);
        System.out.println("交易类型: " + (transactionType.equals("SALE") ? "销售" : "退货"));
        System.out.println("交易时间: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(transactionTime));
        System.out.println("--------------------------------------");
        System.out.printf("%-10s %-10s %-6s %-6s%n", "商品ID", "商品名称", "单价", "数量");
        System.out.println("--------------------------------------");
        for (ShoppingItem item : items) {
            Product p = item.getProduct();
            System.out.printf("%-10s %-10s %-6.2f %-6d%n",
                    p.getProductId(),
                    p.getProductName(),
                    p.getPrice(),
                    item.getQuantity());
        }
        System.out.println("--------------------------------------");
        System.out.println("总金额: " + String.format("%.2f", totalAmount) + " 元");
        System.out.println("======================================");
        System.out.println("感谢您的光临！");
    }

    // Getter 方法（供报告/日志使用）
    public String getReceiptId() {
        return receiptId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }
}