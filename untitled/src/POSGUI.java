import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * POS系统GUI主窗口：整合所有功能面板，提供菜单切换
 */
public class POSGUI extends JFrame {
    // 核心业务对象（复用原有逻辑）
    private final Checkout checkout = new Checkout();
    // 功能面板
    private final SalePanel salePanel;
    private final ReturnPanel returnPanel;
    private final InventoryPanel inventoryPanel;
    private final ReceiptPreviewPanel receiptPreviewPanel;
    // 当前激活的面板
    private JPanel currentPanel;

    public POSGUI() {
        // 初始化面板（传入checkout，实现业务联动）
        salePanel = new SalePanel(checkout, this::showReceiptPreview);
        returnPanel = new ReturnPanel(checkout, this::showReceiptPreview);
        inventoryPanel = new InventoryPanel();
        receiptPreviewPanel = new ReceiptPreviewPanel();

        // 窗口基础配置
        setTitle("超市POS系统 - 图形化版");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 居中显示
        setLayout(new BorderLayout());

        // 添加菜单栏
        addMenuBar();

        // 初始显示销售面板
        currentPanel = salePanel;
        add(currentPanel, BorderLayout.CENTER);
    }

    /**
     * 添加菜单栏（功能切换入口）
     */
    private void addMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // 功能菜单
        JMenu functionMenu = new JMenu("功能");
        JMenuItem saleItem = new JMenuItem("销售");
        JMenuItem returnItem = new JMenuItem("退货");
        JMenuItem inventoryItem = new JMenuItem("库存查询");
        JMenuItem exitItem = new JMenuItem("退出");

        // 菜单事件绑定
        saleItem.addActionListener(e -> switchPanel(salePanel));
        returnItem.addActionListener(e -> switchPanel(returnPanel));
        inventoryItem.addActionListener(e -> switchPanel(inventoryPanel));
        exitItem.addActionListener(e -> System.exit(0));

        // 组装菜单
        functionMenu.add(saleItem);
        functionMenu.add(returnItem);
        functionMenu.add(inventoryItem);
        functionMenu.addSeparator();
        functionMenu.add(exitItem);
        menuBar.add(functionMenu);

        // 添加帮助菜单
        JMenu helpMenu = new JMenu("帮助");
        JMenuItem aboutItem = new JMenuItem("关于");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "超市POS系统 v1.0\n支持销售、退货、库存查询功能", "关于", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    /**
     * 切换功能面板
     */
    private void switchPanel(JPanel targetPanel) {
        remove(currentPanel); // 移除当前面板
        currentPanel = targetPanel; // 更新当前面板
        add(currentPanel, BorderLayout.CENTER); // 添加新面板
        revalidate(); // 刷新界面
        repaint();
    }

    /**
     * 显示收据预览（回调方法，供销售/退货面板调用）
     */
    private void showReceiptPreview(Receipt receipt) {
        receiptPreviewPanel.setReceipt(receipt); // 传入收据数据
        switchPanel(receiptPreviewPanel); // 切换到收据预览面板
    }

    // 程序入口
    public static void main(String[] args) {
        // 确保GUI线程安全
        SwingUtilities.invokeLater(() -> {
            POSGUI posGUI = new POSGUI();
            posGUI.setVisible(true); // 显示窗口
        });
    }
}