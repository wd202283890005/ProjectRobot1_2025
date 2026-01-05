import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

/**
 * Return panel: input product ID & quantity, confirm return, show refund amount
 */
public class ReturnPanel extends JPanel {
    private final Checkout checkout;
    private final Consumer<Receipt> receiptCallback;

    // Components
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

        // 1. Top: input area
        JPanel inputPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        inputPanel.add(new JLabel("Product ID (Return):", SwingConstants.CENTER));
        productIdField = new JTextField();
        SalePanel.setHintText(productIdField, "e.g. P001");
        inputPanel.add(productIdField);

        inputPanel.add(new JLabel("Quantity:", SwingConstants.CENTER));
        quantityField = new JTextField();
        SalePanel.setHintText(quantityField, "Enter positive integer");
        inputPanel.add(quantityField);

        JButton addBtn = new JButton("Add");
        addBtn.addActionListener(new AddReturnItemListener());
        inputPanel.add(addBtn);
        add(inputPanel, BorderLayout.NORTH);

        // 2. Middle: return list area
        returnTextArea = new JTextArea();
        returnTextArea.setEditable(false);
        returnTextArea.setFont(new Font("Monaco", Font.PLAIN, 12));
        JScrollPane returnScroll = new JScrollPane(returnTextArea);
        returnScroll.setBorder(BorderFactory.createTitledBorder("Return Items"));
        add(returnScroll, BorderLayout.CENTER);

        // 3. Bottom: refund & confirm area
        JPanel refundPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        refundPanel.add(new JLabel("Refund:", SwingConstants.CENTER));
        refundLabel = new JLabel("0.00 CNY", SwingConstants.CENTER);
        refundLabel.setFont(new Font("Arial", Font.BOLD, 14));
        refundLabel.setForeground(new Color(0, 128, 0));
        refundPanel.add(refundLabel);

        JButton confirmBtn = new JButton("Confirm");
        confirmBtn.addActionListener(new ConfirmReturnListener());
        refundPanel.add(confirmBtn);

        JButton resetBtn = new JButton("Reset");
        resetBtn.addActionListener(e -> resetReturn());
        refundPanel.add(resetBtn);
        add(refundPanel, BorderLayout.SOUTH);
    }

    /**
     * Listener for adding return items
     */
    private class AddReturnItemListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String productId = productIdField.getText().trim();
                int quantity = Integer.parseInt(quantityField.getText().trim());

                // Validation
                if (productId.isEmpty()) {
                    JOptionPane.showMessageDialog(ReturnPanel.this, "Product ID cannot be empty!", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(ReturnPanel.this, "Quantity must be a positive integer!", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Business logic: add return item (negative quantity)
                checkout.addItem(productId, -quantity);
                updateReturnDisplay();
                JOptionPane.showMessageDialog(ReturnPanel.this, "Return item added!", "Success", JOptionPane.INFORMATION_MESSAGE);

                // Clear fields
                productIdField.setText("");
                quantityField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(ReturnPanel.this, "Quantity format error! Please enter an integer", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(ReturnPanel.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Listener to confirm return
     */
    private class ConfirmReturnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                double totalRefund = Math.abs(checkout.calculateTotalAmount());
                if (totalRefund <= 0) {
                    JOptionPane.showMessageDialog(ReturnPanel.this, "No return items added!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Confirm dialog
                int confirm = JOptionPane.showConfirmDialog(ReturnPanel.this,
                        "Confirm return? Refund: " + totalRefund + " CNY", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;

                // Process return
                Receipt receipt = checkout.processReturn();
                JOptionPane.showMessageDialog(ReturnPanel.this, "Return successful! Showing receipt.", "Success", JOptionPane.INFORMATION_MESSAGE);
                receiptCallback.accept(receipt);
                resetReturn();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(ReturnPanel.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Update list and refund amount
     */
    private void updateReturnDisplay() {
        double totalRefund = Math.abs(checkout.calculateTotalAmount());
        refundLabel.setText(totalRefund + " CNY");

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-10s %-10s %-6s %-8s %-8s%n", "ID", "Name", "Price", "Qty", "Subtotal"));
        sb.append("----------------------------------------\n");
        sb.append(String.format("%-10s %-10s %-6s %-8s %-8.2f%n", "---", "Total", "---", "---", totalRefund));
        returnTextArea.setText(sb.toString());
    }

    /**
     * Reset return transaction
     */
    private void resetReturn() {
        checkout.cancelTransaction();
        returnTextArea.setText("");
        refundLabel.setText("0.00 CNY");
        productIdField.setText("");
        quantityField.setText("");
    }
}