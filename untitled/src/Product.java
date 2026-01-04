/**
 * 商品类：封装商品核心信息
 */
public class Product {
    // 商品属性（私有封装）
    private String productId;    // 商品唯一ID
    private String productName;  // 商品名称
    private double price;        // 商品单价
    private int stockQuantity;   // 库存数量

    // 构造方法（初始化商品）
    public Product(String productId, String productName, double price, int stockQuantity) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    // Getter/Setter 方法（属性访问控制）
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

    // 库存更新方法（增加/减少库存）
    public void updateStock(int quantity) {
        if (this.stockQuantity + quantity < 0) {
            throw new IllegalArgumentException("库存不足，无法操作");
        }
        this.stockQuantity += quantity;
    }
}