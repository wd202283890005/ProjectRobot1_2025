import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;

/**
 * Receipt preview panel: shows receipt for sale/return, supports printing & back navigation
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

        // Title
        JLabel titleLabel = new JLabel("Receipt Preview", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);

        // Receipt area
        receiptTextArea = new JTextArea();
        receiptTextArea.setEditable(false);
        receiptTextArea.setFont(new Font("Monaco", Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(receiptTextArea);
        add(scroll, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton printBtn = new JButton("Print");
        printBtn.addActionListener(new PrintListener());
        btnPanel.add(printBtn);

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(ReceiptPreviewPanel.this);
            frame.getContentPane().remove(ReceiptPreviewPanel.this);
            frame.add(new SalePanel(new Checkout(), r -> {}));
            frame.revalidate();
            frame.repaint();
        });
        btnPanel.add(backBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    /**
     * Set receipt data
     */
    public void setReceipt(Receipt receipt) {
        this.currentReceipt = receipt;
        StringBuilder sb = new StringBuilder();
        sb.append("======================================\n");
        sb.append("        Supermarket POS - Receipt\n");
        sb.append("======================================\n");
        sb.append("Receipt ID: ").append(receipt.getReceiptId()).append("\n");
        sb.append("Type: ").append(receipt.getTransactionType().equals("SALE") ? "Sale" : "Return").append("\n");
        sb.append("Time: ").append(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(receipt.getTransactionTime())).append("\n");
        sb.append("--------------------------------------\n");
        sb.append(String.format("%-10s %-10s %-6s %-6s%n", "ID", "Name", "Price", "Qty"));
        sb.append("--------------------------------------\n");
        for (ShoppingItem item : receipt.getItems()) {
            Product p = item.getProduct();
            sb.append(String.format("%-10s %-10s %-6.2f %-6d%n",
                    p.getProductId(), p.getProductName(), p.getPrice(), item.getQuantity()));
        }
        sb.append("--------------------------------------\n");
        sb.append("Total: ").append(String.format("%.2f", receipt.getTotalAmount())).append(" CNY\n");
        sb.append("======================================\n");
        sb.append("Thank you for shopping!\n");
        receiptTextArea.setText(sb.toString());
    }

    /**
     * Print button listener
     */
    private class PrintListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                boolean success = receiptTextArea.print();
                if (success) {
                    JOptionPane.showMessageDialog(ReceiptPreviewPanel.this, "Printed!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(ReceiptPreviewPanel.this, "Print failed!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(ReceiptPreviewPanel.this, "Print exception: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}