import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

/**
 * 退货功能面板：输入退货商品ID、数量，确认退货，显示应退金额
 */
public class ReturnPanel extends JPanel {
    private final Checkout checkout;
    private final Consumer<Receipt> receiptCallback;

    // 组件
    private JTextField productIdField;
    private JTextField quantityField;
    private JTextArea returnTextArea;
    private JLabel refundLabel;

    public ReturnPanel(Checkout checkout, Consumer<Receipt> receiptCallback) {
        this.checkout = checkout;
        this.receiptCallback = receiptCallback;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 1. 顶部：输入区域
        JPanel inputPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        inputPanel.add(new JLabel("退货商品ID：", SwingConstants.CENTER));
        productIdField = new JTextField();
        SalePanel.setHintText(productIdField, "输入商品ID（如P001）");
        inputPanel.add(productIdField);

        inputPanel.add(new JLabel("退货数量：", SwingConstants.CENTER));
        quantityField = new JTextField();
        SalePanel.setHintText(quantityField, "输入正整数");
        inputPanel.add(quantityField);

        JButton addBtn = new JButton("添加退货商品");
        addBtn.addActionListener(new AddReturnItemListener());
        inputPanel.add(addBtn);
        add(inputPanel, BorderLayout.NORTH);

        // 2. 中间：退货列表显示区
        returnTextArea = new JTextArea();
        returnTextArea.setEditable(false);
        returnTextArea.setFont(new Font("Monaco", Font.PLAIN, 12));
        JScrollPane returnScroll = new JScrollPane(returnTextArea);
        returnScroll.setBorder(BorderFactory.createTitledBorder("当前退货商品"));
        add(returnScroll, BorderLayout.CENTER);

        // 3. 底部：退款金额与确认区域
        JPanel refundPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        refundPanel.add(new JLabel("应退金额：", SwingConstants.CENTER));
        refundLabel = new JLabel("0.00 元", SwingConstants.CENTER);
        refundLabel.setFont(new Font("Arial", Font.BOLD, 14));
        refundLabel.setForeground(new Color(0, 128, 0));
        refundPanel.add(refundLabel);

        JButton confirmBtn = new JButton("确认退货");
        confirmBtn.addActionListener(new ConfirmReturnListener());
        refundPanel.add(confirmBtn);

        JButton resetBtn = new JButton("重置退货");
        resetBtn.addActionListener(e -> resetReturn());
        refundPanel.add(resetBtn);
        add(refundPanel, BorderLayout.SOUTH);
    }

    /**
     * 添加退货商品监听器
     */
    private class AddReturnItemListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String productId = productIdField.getText().trim();
                int quantity = Integer.parseInt(quantityField.getText().trim());

                // 输入校验
                if (productId.isEmpty()) {
                    JOptionPane.showMessageDialog(ReturnPanel.this, "商品ID不能为空！", "输入错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(ReturnPanel.this, "退货数量必须为正整数！", "输入错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 调用业务逻辑添加退货商品（数量传负数）
                checkout.addItem(productId, -quantity);
                updateReturnDisplay();
                JOptionPane.showMessageDialog(ReturnPanel.this, "退货商品添加成功！", "成功", JOptionPane.INFORMATION_MESSAGE);

                // 清空输入框
                productIdField.setText("");
                quantityField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(ReturnPanel.this, "数量格式错误！请输入整数", "错误", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(ReturnPanel.this, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * 确认退货监听器
     */
    private class ConfirmReturnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                double totalRefund = Math.abs(checkout.calculateTotalAmount());
                if (totalRefund <= 0) {
                    JOptionPane.showMessageDialog(ReturnPanel.this, "未添加任何退货商品！", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 确认弹窗
                int confirm = JOptionPane.showConfirmDialog(ReturnPanel.this,
                        "确认退货？应退金额：" + totalRefund + " 元", "确认", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;

                // 调用业务逻辑处理退货
                Receipt receipt = checkout.processReturn();
                JOptionPane.showMessageDialog(ReturnPanel.this, "退货成功！即将显示收据", "成功", JOptionPane.INFORMATION_MESSAGE);
                receiptCallback.accept(receipt);
                resetReturn();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(ReturnPanel.this, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * 更新退货列表和应退金额
     */
    private void updateReturnDisplay() {
        double totalRefund = Math.abs(checkout.calculateTotalAmount());
        refundLabel.setText(totalRefund + " 元");

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-10s %-10s %-6s %-6s %-6s%n", "商品ID", "商品名称", "单价", "退货数量", "应退小计"));
        sb.append("----------------------------------------\n");
        sb.append(String.format("%-10s %-10s %-6s %-6s %-6.2f%n", "---", "累计", "---", "---", totalRefund));
        returnTextArea.setText(sb.toString());
    }

    /**
     * 重置退货交易
     */
    private void resetReturn() {
        checkout.cancelTransaction();
        returnTextArea.setText("");
        refundLabel.setText("0.00 元");
        productIdField.setText("");
        quantityField.setText("");
    }
}