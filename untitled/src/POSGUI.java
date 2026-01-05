import javax.swing.*;
import java.awt.*;

/**
 * POS GUI main window: integrates all function panels and provides menu navigation
 */
public class POSGUI extends JFrame {
    // Core business object
    private final Checkout checkout = new Checkout();
    // Panels
    private final SalePanel salePanel;
    private final ReturnPanel returnPanel;
    private final InventoryPanel inventoryPanel;
    private final ReceiptPreviewPanel receiptPreviewPanel;
    // Currently displayed panel
    private JPanel currentPanel;

    public POSGUI() {
        // Initialize panels
        salePanel = new SalePanel(checkout, this::showReceiptPreview);
        returnPanel = new ReturnPanel(checkout, this::showReceiptPreview);
        inventoryPanel = new InventoryPanel();
        receiptPreviewPanel = new ReceiptPreviewPanel();

        // Window config
        setTitle("Supermarket POS - GUI");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center
        setLayout(new BorderLayout());

        // Menu bar
        addMenuBar();

        // Default panel
        currentPanel = salePanel;
        add(currentPanel, BorderLayout.CENTER);
    }

    /**
     * Build menu bar
     */
    private void addMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Function menu
        JMenu functionMenu = new JMenu("Functions");
        JMenuItem saleItem = new JMenuItem("Sale");
        JMenuItem returnItem = new JMenuItem("Return");
        JMenuItem inventoryItem = new JMenuItem("Inventory");
        JMenuItem exitItem = new JMenuItem("Exit");

        saleItem.addActionListener(e -> switchPanel(salePanel));
        returnItem.addActionListener(e -> switchPanel(returnPanel));
        inventoryItem.addActionListener(e -> switchPanel(inventoryPanel));
        exitItem.addActionListener(e -> System.exit(0));

        functionMenu.add(saleItem);
        functionMenu.add(returnItem);
        functionMenu.add(inventoryItem);
        functionMenu.addSeparator();
        functionMenu.add(exitItem);
        menuBar.add(functionMenu);

        // Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Supermarket POS v1.0\nSupports sale, return, and inventory functions", "About", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    /**
     * Switch displayed panel
     */
    private void switchPanel(JPanel targetPanel) {
        remove(currentPanel);
        currentPanel = targetPanel;
        add(currentPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    /**
     * Show receipt preview panel
     */
    private void showReceiptPreview(Receipt receipt) {
        receiptPreviewPanel.setReceipt(receipt);
        switchPanel(receiptPreviewPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            POSGUI gui = new POSGUI();
            gui.setVisible(true);
        });
    }
}