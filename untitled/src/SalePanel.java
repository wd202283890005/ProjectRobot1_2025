import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

/**
 * Sale panel: input product ID & quantity, add items, pay, show total amount
 */
public class SalePanel extends JPanel {
    private final Checkout checkout;
    private final Consumer<Receipt> receiptCallback; // Receipt callback (for preview)

    // Components
    private JTextField productIdField; // Product ID input
    private JTextField quantityField;  // Quantity input
    private JTextArea cartTextArea;    // Cart display area
    private JLabel totalAmountLabel;   // Total amount label
    private JTextField cashField;      // Cash input

    public SalePanel(Checkout checkout, Consumer<Receipt> receiptCallback) {
        this.checkout = checkout;
        this.receiptCallback = receiptCallback;
        initUI(); // Initialize UI
    }

    /**
     * Initialize UI components
     */
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 1. Top: input area (product ID + quantity + add button)
        JPanel inputPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        inputPanel.add(new JLabel("Product ID:", SwingConstants.CENTER));
        productIdField = new JTextField();
        inputPanel.add(productIdField);

        inputPanel.add(new JLabel("Quantity:", SwingConstants.CENTER));
        quantityField = new JTextField();
        quantityField.setText("Enter positive integer");
        inputPanel.add(quantityField);

        JButton addBtn = new JButton("Add Item");
        addBtn.addActionListener(new AddItemListener());
        inputPanel.add(addBtn);
        add(inputPanel, BorderLayout.NORTH);

        // 2. Middle: cart display
        cartTextArea = new JTextArea();
        cartTextArea.setEditable(false);
        cartTextArea.setFont(new Font("Monaco", Font.PLAIN, 12));
        JScrollPane cartScroll = new JScrollPane(cartTextArea);
        cartScroll.setBorder(BorderFactory.createTitledBorder("Current Cart"));
        add(cartScroll, BorderLayout.CENTER);

        // 3. Bottom: amount & payment area
        JPanel payPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        payPanel.add(new JLabel("Total:", SwingConstants.CENTER));
        totalAmountLabel = new JLabel("0.00 CNY", SwingConstants.CENTER);
        totalAmountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalAmountLabel.setForeground(Color.RED);
        payPanel.add(totalAmountLabel);

        payPanel.add(new JLabel("Payment:", SwingConstants.CENTER));
        cashField = new JTextField();
        payPanel.add(cashField);

        JButton payBtn = new JButton("Pay");
        payBtn.addActionListener(new PayListener());
        payPanel.add(payBtn);

        JButton resetBtn = new JButton("Reset");
        resetBtn.addActionListener(e -> resetTransaction());
        payPanel.add(resetBtn);
        add(payPanel, BorderLayout.SOUTH);
    }

    /**
     * Listener for "Add Item" button
     */
    private class AddItemListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // Retrieve input
                String productId = productIdField.getText().trim();
                int quantity = Integer.parseInt(quantityField.getText().trim());

                // Validate input
                if (productId.isEmpty()) {
                    JOptionPane.showMessageDialog(SalePanel.this, "Product ID cannot be empty!", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(SalePanel.this, "Quantity must be a positive integer!", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Business logic: add item
                checkout.addItem(productId, quantity);
                updateCartDisplay(); // Refresh cart display
                JOptionPane.showMessageDialog(SalePanel.this, "Item added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                // Clear input fields
                productIdField.setText("");
                quantityField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(SalePanel.this, "Quantity format error! Please enter an integer", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(SalePanel.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Listener for "Pay" button
     */
    private class PayListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                double total = checkout.calculateTotalAmount();
                if (total <= 0) {
                    JOptionPane.showMessageDialog(SalePanel.this, "Cart is empty, cannot pay!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Retrieve payment amount
                double cash = Double.parseDouble(cashField.getText().trim());
                if (cash < total) {
                    JOptionPane.showMessageDialog(SalePanel.this, "Insufficient payment! Due: " + total + " CNY", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Business logic: process payment
                Receipt receipt = checkout.processPayment(cash);
                JOptionPane.showMessageDialog(SalePanel.this, "Payment successful! Showing receipt.", "Success", JOptionPane.INFORMATION_MESSAGE);
                receiptCallback.accept(receipt); // Show receipt preview via callback
                resetTransaction(); // Reset transaction
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(SalePanel.this, "Payment amount format error! Please enter a number", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(SalePanel.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Update cart display area and total amount
     */
    private void updateCartDisplay() {
        double total = checkout.calculateTotalAmount();
        totalAmountLabel.setText(total + " CNY");

        // Build cart text
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-10s %-10s %-6s %-6s %-8s%n", "Product ID", "Name", "Price", "Qty", "Subtotal"));
        sb.append("----------------------------------------\n");

        // For simplicity, we only show the total here
        sb.append(String.format("%-10s %-10s %-6s %-6s %-8.2f%n", "---", "Total", "---", "---", total));
        cartTextArea.setText(sb.toString());
    }

    /**
     * Reset current transaction
     */
    private void resetTransaction() {
        checkout.cancelTransaction();
        cartTextArea.setText("");
        totalAmountLabel.setText("0.00 CNY");
        productIdField.setText("");
        quantityField.setText("");
        cashField.setText("");
    }

    /**
     * Utility: add placeholder text to JTextField
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