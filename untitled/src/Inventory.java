import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Inventory management class: Singleton pattern, centrally manages product stock
 */
public class Inventory {
    // Singleton instance (ensure globally unique)
    private static Inventory instance;
    // Store products: key = product ID, value = Product object
    private final Map<String, Product> productMap;

    // Private constructor (prevent external instantiation)
    private Inventory() {
        productMap = new HashMap<>();
        // Initialize test products (in real projects, load from DB)
        initTestProducts();
    }

    // Get all products (for inventory query panel)
    public Collection<Product> getAllProducts() {
        return productMap.values();
    }

    // Singleton getter
    public static synchronized Inventory getInstance() {
        if (instance == null) {
            instance = new Inventory();
        }
        return instance;
    }

    // Initialize test products
    private void initTestProducts() {
        productMap.put("P001", new Product("P001", "Coca-Cola", 3.5, 100));
        productMap.put("P002", new Product("P002", "Chips", 5.0, 80));
        productMap.put("P003", new Product("P003", "Notebook", 15.9, 50));
    }

    // Query product by ID
    public Product getProductById(String productId) {
        return productMap.get(productId);
    }

    // Add new product to inventory
    public void addProduct(Product product) {
        productMap.put(product.getProductId(), product);
    }
}