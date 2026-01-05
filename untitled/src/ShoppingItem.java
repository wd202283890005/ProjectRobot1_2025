/**
 * ShoppingItem class: associates a product with a purchase/return quantity
 */
public class ShoppingItem {
    private Product product;  // Associated product
    private int quantity;     // Quantity (positive = purchase, negative = return)

    public ShoppingItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    // Getter methods
    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    // Calculate subtotal for this item (unit price × quantity)
    public double calculateItemTotal() {
        return product.getPrice() * quantity;
    }
}