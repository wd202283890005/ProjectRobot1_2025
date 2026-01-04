/**
 * 购物项类：关联商品与购买/退货数量
 */
public class ShoppingItem {
    private Product product;  // 关联的商品
    private int quantity;     // 数量（正数=购买，负数=退货）

    public ShoppingItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    // Getter 方法
    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    // 计算当前购物项的金额（单价×数量）
    public double calculateItemTotal() {
        return product.getPrice() * quantity;
    }
}