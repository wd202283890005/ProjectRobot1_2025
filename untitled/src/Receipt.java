import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Receipt class: generates sale/return receipts
 */
public class Receipt {
    private String receiptId;          // Unique receipt ID
    private List<ShoppingItem> items;  // Item list
    private double totalAmount;        // Total amount (sale = positive, return = negative)
    private Date transactionTime;      // Transaction time
    private String transactionType;    // Transaction type (SALE or RETURN)

    // Constructor (build receipt)
    public Receipt(List<ShoppingItem> items, String transactionType) {
        this.receiptId = generateReceiptId();
        this.items = items;
        this.transactionType = transactionType;
        this.transactionTime = new Date();
        this.totalAmount = calculateTotalAmount();
    }

    // Generate unique receipt ID (timestamp + random number)
    private String generateReceiptId() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timeStr = sdf.format(new Date());
        int randomNum = (int) (Math.random() * 1000);
        return timeStr + String.format("%03d", randomNum);
    }

    // Calculate total amount
    private double calculateTotalAmount() {
        return items.stream().mapToDouble(ShoppingItem::calculateItemTotal).sum();
    }

    // Getters for GUI usage
    public List<ShoppingItem> getItems() { return items; }
    public String getTransactionType() { return transactionType; }
    public Date getTransactionTime() { return transactionTime; }
    public String getReceiptId() { return receiptId; }
    public double getTotalAmount() { return totalAmount; }

    // Print receipt to console (could export to PDF in real project)
    public void printReceipt() {
        System.out.println("======================================");
        System.out.println("        Supermarket POS - Receipt");
        System.out.println("======================================");
        System.out.println("Receipt ID: " + receiptId);
        System.out.println("Type: " + (transactionType.equals("SALE") ? "Sale" : "Return"));
        System.out.println("Time: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(transactionTime));
        System.out.println("--------------------------------------");
        System.out.printf("%-10s %-10s %-6s %-6s%n", "ID", "Name", "Price", "Qty");
        System.out.println("--------------------------------------");
        for (ShoppingItem item : items) {
            Product p = item.getProduct();
            System.out.printf("%-10s %-10s %-6.2f %-6d%n",
                    p.getProductId(), p.getProductName(), p.getPrice(), item.getQuantity());
        }
        System.out.println("--------------------------------------");
        System.out.println("Total: " + String.format("%.2f", totalAmount) + " CNY");
        System.out.println("======================================");
        System.out.println("Thank you for shopping!");
    }
}