import java.util.HashMap;
import java.util.Map;

/**
 * 库存管理类：单例模式，统一管理所有商品库存
 */
public class Inventory {
    // 单例实例（确保全局唯一）
    private static Inventory instance;
    // 存储商品：key=商品ID，value=商品对象
    private Map<String, Product> productMap;

    // 私有构造方法（禁止外部实例化）
    private Inventory() {
        productMap = new HashMap<>();
        // 初始化测试商品（实际项目可从数据库加载）
        initTestProducts();
    }

    // 单例获取方法
    public static synchronized Inventory getInstance() {
        if (instance == null) {
            instance = new Inventory();
        }
        return instance;
    }

    // 初始化测试商品
    private void initTestProducts() {
        productMap.put("P001", new Product("P001", "可口可乐", 3.5, 100));
        productMap.put("P002", new Product("P002", "薯片", 5.0, 80));
        productMap.put("P003", new Product("P003", "笔记本", 15.9, 50));
    }

    // 根据商品ID查询商品
    public Product getProductById(String productId) {
        return productMap.get(productId);
    }

    // 添加新商品到库存
    public void addProduct(Product product) {
        productMap.put(product.getProductId(), product);
    }
}