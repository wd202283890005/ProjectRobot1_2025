/**
 * Product class: encapsulates core product information
 */
public class Product {
    // Product attributes (encapsulated)
    private String productId;    // Unique product ID
    private String productName;  // Product name
    private double price;        // Unit price
    private int stockQuantity;   // Stock quantity

    // Constructor (initialize product)
    public Product(String productId, String productName, double price, int stockQuantity) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    // Getter/Setter methods
    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public double getPrice() {
        return price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    // Update stock (increase/decrease)
    public void updateStock(int quantity) {
        if (this.stockQuantity + quantity < 0) {
            throw new IllegalArgumentException("Not enough stock to perform operation");
        }
        this.stockQuantity += quantity;
    }
}