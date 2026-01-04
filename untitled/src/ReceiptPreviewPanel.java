import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;

/**
 * 收据预览面板：显示销售/退货收据，支持打印和返回
 */
public class ReceiptPreviewPanel extends JPanel {
    private JTextArea receiptTextArea;
    private Receipt currentReceipt;

    public ReceiptPreviewPanel() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 收据标题
        JLabel titleLabel = new JLabel("收据预览", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);

        // 收据内容显示区
        receiptTextArea = new JTextArea();
        receiptTextArea.setEditable(false);
        receiptTextArea.setFont(new Font("Monaco", Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(receiptTextArea);
        add(scroll, BorderLayout.CENTER);

        // 底部按钮区
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton printBtn = new JButton("打印收据");
        printBtn.addActionListener(new PrintListener());
        btnPanel.add(printBtn);

        JButton backBtn = new JButton("返回主菜单");
        backBtn.addActionListener(e -> {
            // 返回销售面板（可根据需求修改为返回上一个面板）
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(ReceiptPreviewPanel.this);
            frame.getContentPane().remove(ReceiptPreviewPanel.this);
            frame.add(new SalePanel(new Checkout(), receipt -> {}));
            frame.revalidate();
            frame.repaint();
        });
        btnPanel.add(backBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    /**
     * 设置收据数据并更新显示
     */
    public void setReceipt(Receipt receipt) {
        this.currentReceipt = receipt;
        // 构建收据文本（复用Receipt的print逻辑，改为字符串）
        StringBuilder sb = new StringBuilder();
        sb.append("======================================\n");
        sb.append("          超市POS系统 - 收据\n");
        sb.append("======================================\n");
        sb.append("收据ID: ").append(receipt.getReceiptId()).append("\n");
        sb.append("交易类型: ").append(receipt.getTransactionType().equals("SALE") ? "销售" : "退货").append("\n");
        sb.append("交易时间: ").append(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(receipt.getTransactionTime())).append("\n");
        sb.append("--------------------------------------\n");
        sb.append(String.format("%-10s %-10s %-6s %-6s%n", "商品ID", "商品名称", "单价", "数量"));
        sb.append("--------------------------------------\n");
        for (ShoppingItem item : receipt.getItems()) {
            Product p = item.getProduct();
            sb.append(String.format("%-10s %-10s %-6.2f %-6d%n",
                    p.getProductId(), p.getProductName(), p.getPrice(), item.getQuantity()));
        }
        sb.append("--------------------------------------\n");
        sb.append("总金额: ").append(String.format("%.2f", receipt.getTotalAmount())).append(" 元\n");
        sb.append("======================================\n");
        sb.append("感谢您的光临！\n");
        receiptTextArea.setText(sb.toString());
    }

    /**
     * 打印按钮监听器
     */
    private class PrintListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // 调用Swing打印功能
                boolean success = receiptTextArea.print();
                if (success) {
                    JOptionPane.showMessageDialog(ReceiptPreviewPanel.this, "打印成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(ReceiptPreviewPanel.this, "打印失败！", "错误", JOptionPane.ERROR_MESSAGE);
                }
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(ReceiptPreviewPanel.this, "打印异常：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 为Receipt类添加getter方法（原有Receipt类中新增，用于GUI获取数据）
    // public List<ShoppingItem> getItems() { return items; }
    // public String getTransactionType() { return transactionType; }
    // public Date getTransactionTime() { return transactionTime; }
}