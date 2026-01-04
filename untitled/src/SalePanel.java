import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

/**
 * 销售功能面板：输入商品ID、数量，添加商品，支付，显示累计金额
 */
public class SalePanel extends JPanel {
    private final Checkout checkout;
    private final Consumer<Receipt> receiptCallback; // 收据回调（用于显示预览）

    // 组件
    private JTextField productIdField; // 商品ID输入框
    private JTextField quantityField; // 数量输入框
    private JTextArea cartTextArea; // 购物车显示区
    private JLabel totalAmountLabel; // 累计金额标签
    private JTextField cashField; // 支付金额输入框

    public SalePanel(Checkout checkout, Consumer<Receipt> receiptCallback) {
        this.checkout = checkout;
        this.receiptCallback = receiptCallback;
        initUI(); // 初始化界面
    }

    /**
     * 初始化销售面板UI
     */
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 1. 顶部：输入区域（商品ID + 数量 + 添加按钮）
        JPanel inputPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        inputPanel.add(new JLabel("商品ID：", SwingConstants.CENTER));
        productIdField = new JTextField();
        //productIdField.setText("输入商品ID（如P001）");
        inputPanel.add(productIdField);

        inputPanel.add(new JLabel("购买数量：", SwingConstants.CENTER));
        quantityField = new JTextField();
        quantityField.setText("输入正整数");
        inputPanel.add(quantityField);

        JButton addBtn = new JButton("添加商品");
        addBtn.addActionListener(new AddItemListener());
        inputPanel.add(addBtn);
        add(inputPanel, BorderLayout.NORTH);

        // 2. 中间：购物车显示区
        cartTextArea = new JTextArea();
        cartTextArea.setEditable(false);
        cartTextArea.setFont(new Font("Monaco", Font.PLAIN, 12));
        JScrollPane cartScroll = new JScrollPane(cartTextArea);
        cartScroll.setBorder(BorderFactory.createTitledBorder("当前购物车"));
        add(cartScroll, BorderLayout.CENTER);

        // 3. 底部：金额与支付区域
        JPanel payPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        payPanel.add(new JLabel("累计金额：", SwingConstants.CENTER));
        totalAmountLabel = new JLabel("0.00 元", SwingConstants.CENTER);
        totalAmountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalAmountLabel.setForeground(Color.RED);
        payPanel.add(totalAmountLabel);

        payPanel.add(new JLabel("支付金额：", SwingConstants.CENTER));
        cashField = new JTextField();
        //cashField.setText("输入支付现金金额");
        payPanel.add(cashField);

        JButton payBtn = new JButton("确认支付");
        payBtn.addActionListener(new PayListener());
        payPanel.add(payBtn);

        JButton resetBtn = new JButton("重置交易");
        resetBtn.addActionListener(e -> resetTransaction());
        payPanel.add(resetBtn);
        add(payPanel, BorderLayout.SOUTH);
    }

    /**
     * 添加商品按钮监听器
     */
    private class AddItemListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // 获取输入
                String productId = productIdField.getText().trim();
                int quantity = Integer.parseInt(quantityField.getText().trim());

                // 输入校验
                if (productId.isEmpty()) {
                    JOptionPane.showMessageDialog(SalePanel.this, "商品ID不能为空！", "输入错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(SalePanel.this, "购买数量必须为正整数！", "输入错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 调用业务逻辑添加商品
                checkout.addItem(productId, quantity);
                updateCartDisplay(); // 更新购物车显示
                JOptionPane.showMessageDialog(SalePanel.this, "商品添加成功！", "成功", JOptionPane.INFORMATION_MESSAGE);

                // 清空输入框
                productIdField.setText("");
                quantityField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(SalePanel.this, "数量格式错误！请输入整数", "错误", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(SalePanel.this, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * 支付按钮监听器
     */
    private class PayListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                double total = checkout.calculateTotalAmount();
                if (total <= 0) {
                    JOptionPane.showMessageDialog(SalePanel.this, "购物车为空，无法支付！", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 获取支付金额
                double cash = Double.parseDouble(cashField.getText().trim());
                if (cash < total) {
                    JOptionPane.showMessageDialog(SalePanel.this, "支付金额不足！应付：" + total + " 元", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 调用业务逻辑处理支付
                Receipt receipt = checkout.processPayment(cash);
                JOptionPane.showMessageDialog(SalePanel.this, "支付成功！即将显示收据", "成功", JOptionPane.INFORMATION_MESSAGE);
                receiptCallback.accept(receipt); // 回调显示收据预览
                resetTransaction(); // 重置交易
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(SalePanel.this, "支付金额格式错误！请输入数字", "错误", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(SalePanel.this, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * 更新购物车显示和累计金额
     */
    private void updateCartDisplay() {
        double total = checkout.calculateTotalAmount();
        totalAmountLabel.setText(total + " 元");

        // 构建购物车文本
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-10s %-10s %-6s %-6s %-6s%n", "商品ID", "商品名称", "单价", "数量", "小计"));
        sb.append("----------------------------------------\n");

        // （注：若需获取购物项列表，可给Checkout添加getCurrentItems()方法，此处省略，直接显示累计效果）
        // 实际项目中可扩展Checkout，暴露当前购物项供GUI展示
        sb.append(String.format("%-10s %-10s %-6s %-6s %-6.2f%n", "---", "累计", "---", "---", total));
        cartTextArea.setText(sb.toString());
    }

    /**
     * 重置当前交易
     */
    private void resetTransaction() {
        checkout.cancelTransaction();
        cartTextArea.setText("");
        totalAmountLabel.setText("0.00 元");
        productIdField.setText("");
        quantityField.setText("");
        cashField.setText("");
    }

    /**
     * 为JTextField添加占位提示文本（工具方法）
     */
    public static void setHintText(JTextField textField, String hint) {
        textField.putClientProperty("hint", hint);
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (textField.getText().equals(hint)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(hint);
                    textField.setForeground(Color.GRAY);
                }
            }
        });
        textField.setText(hint);
        textField.setForeground(Color.GRAY);
    }
}