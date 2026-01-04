import java.util.ArrayList;
import java.util.List;

/**
 * 收银台类：核心业务逻辑（销售/退货处理）
 */
public class Checkout {
    private Inventory inventory;  // 依赖库存管理类
    private List<ShoppingItem> currentItems;  // 当前交易的购物项

    public Checkout() {
        this.inventory = Inventory.getInstance();  // 依赖注入（单例）
        this.currentItems = new ArrayList<>();
    }

    // 添加商品到当前交易（销售：数量为正；退货：数量为负）
    public void addItem(String productId, int quantity) {
        // 1. 验证商品是否存在
        Product product = inventory.getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("商品ID不存在：" + productId);
        }

        // 2. 验证库存（销售：库存≥购买数量；退货：退货数量≥0）
        if (quantity > 0) {  // 销售场景
            if (product.getStockQuantity() < quantity) {
                throw new IllegalArgumentException("商品《" + product.getProductName() + "》库存不足，当前库存：" + product.getStockQuantity());
            }
        } else if (quantity < 0) {  // 退货场景（数量为负，取绝对值）
            if (Math.abs(quantity) <= 0) {
                throw new IllegalArgumentException("退货数量必须大于0");
            }
        }

        // 3. 添加到购物项（若已存在该商品，更新数量）
        boolean isExists = false;
        for (ShoppingItem item : currentItems) {
            if (item.getProduct().getProductId().equals(productId)) {
                item = new ShoppingItem(product, item.getQuantity() + quantity);
                isExists = true;
                break;
            }
        }
        if (!isExists) {
            currentItems.add(new ShoppingItem(product, quantity));
        }
    }

    // 处理支付（仅支持现金，验证支付金额≥总金额）
    public Receipt processPayment(double cashAmount) {
        double total = calculateTotalAmount();
        if (cashAmount < total) {
            throw new IllegalArgumentException("支付金额不足！应付：" + total + " 元，实付：" + cashAmount + " 元");
        }

        // 1. 更新库存（销售：减少库存；退货：增加库存）
        for (ShoppingItem item : currentItems) {
            Product product = item.getProduct();
            product.updateStock(-item.getQuantity());  // 销售：库存-数量；退货：库存-（负数）=+数量
        }

        // 2. 生成销售收据
        Receipt receipt = new Receipt(currentItems, "SALE");
        // 3. 清空当前交易（准备下一笔）
        currentItems.clear();
        return receipt;
    }

    // 处理退货（直接生成退货收据，更新库存）
    public Receipt processReturn() {
        double totalRefund = calculateTotalAmount();  // 退货总金额（为负数，绝对值为退款金额）
        if (totalRefund >= 0) {
            throw new IllegalArgumentException("退货商品数量不能为正！");
        }

        // 1. 更新库存（退货：增加库存）
        for (ShoppingItem item : currentItems) {
            Product product = item.getProduct();
            product.updateStock(-item.getQuantity());  // 退货数量为负，-quantity=正数，库存增加
        }

        // 2. 生成退货收据
        Receipt receipt = new Receipt(currentItems, "RETURN");
        // 3. 清空当前交易
        currentItems.clear();
        return receipt;
    }

    // 计算当前交易总金额
    public double calculateTotalAmount() {
        return currentItems.stream()
                .mapToDouble(ShoppingItem::calculateItemTotal)
                .sum();
    }

    // 取消当前交易
    public void cancelTransaction() {
        currentItems.clear();
        System.out.println("交易已取消！");
    }
}