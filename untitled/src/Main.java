import java.util.Scanner;

/**
 * Interactive POS main program: supports continuous running and manual user input, suitable for real-world usage scenarios
 */
public class Main {
    // Global Scanner (avoid repeated creation)
    private static final Scanner scanner = new Scanner(System.in);
    // Checkout instance (core business object)
    private static final Checkout checkout = new Checkout();

    public static void main(String[] args) {
        System.out.println("======================================");
        System.out.println("        Supermarket POS System - CLI");
        System.out.println("======================================");
        System.out.println("Features: 1. Sale  2. Return  3. Exit");
        System.out.println("======================================");

        // Continuous running: show menu until user chooses to exit
        while (true) {
            try {
                // 1. Show main menu and get user selection
                System.out.print("\nPlease enter the function number (1-3): ");
                int choice = Integer.parseInt(scanner.nextLine().trim());

                // 2. Execute corresponding function according to selection
                switch (choice) {
                    case 1:
                        handleSale(); // Process sale
                        break;
                    case 2:
                        handleReturn(); // Process return
                        break;
                    case 3:
                        exitSystem(); // Exit system
                        return; // Terminate program
                    default:
                        System.out.println("❌ Invalid input! Please enter a number between 1 and 3");
                }

                // 3. After operation, ask whether to continue
                if (!isContinue()) {
                    exitSystem();
                    return;
                }

            } catch (NumberFormatException e) {
                // Capture non-numeric input exception
                System.out.println("❌ Invalid format! Please enter a valid number");
            } catch (IllegalArgumentException e) {
                // Capture business exceptions (e.g. product not found, insufficient stock, etc.)
                System.out.println("❌ Operation failed: " + e.getMessage());
                checkout.cancelTransaction(); // Cancel current transaction
                // Ask whether to continue
                if (!isContinue()) {
                    exitSystem();
                    return;
                }
            } catch (Exception e) {
                // Capture other unknown exceptions
                System.out.println("❌ System error: " + e.getMessage());
                checkout.cancelTransaction();
                if (!isContinue()) {
                    exitSystem();
                    return;
                }
            }
        }
    }

    /**
     * Sale flow: user enters product ID and quantity, supports adding multiple products
     */
    private static void handleSale() {
        System.out.println("\n===== Entering [SALE MODE] =====");
        System.out.println("Tip: Enter product ID and purchase quantity, input '0' to finish adding products");

        while (true) {
            try {
                // Enter product ID
                System.out.print("Please enter product ID (enter '0' to finish): ");
                String productId = scanner.nextLine().trim();

                // Finish adding products
                if ("0".equals(productId)) {
                    if (checkout.calculateTotalAmount() <= 0) {
                        System.out.println("⚠️  No products added, returning to main menu");
                        return;
                    }
                    break;
                }

                // Enter purchase quantity (must be positive)
                System.out.print("Please enter purchase quantity (positive integer): ");
                int quantity = Integer.parseInt(scanner.nextLine().trim());
                if (quantity <= 0) {
                    System.out.println("❌ Purchase quantity must be greater than 0, please re-enter");
                    continue;
                }

                // Add product to transaction
                checkout.addItem(productId, quantity);
                System.out.println("✅ Product added successfully! Current total amount: " + checkout.calculateTotalAmount() + " CNY");

            } catch (NumberFormatException e) {
                System.out.println("❌ Quantity input error! Please enter a valid number");
            } catch (IllegalArgumentException e) {
                System.out.println("❌ Failed to add product: " + e.getMessage());
            }
        }

        // Calculate total amount
        double totalAmount = checkout.calculateTotalAmount();
        System.out.println("\n📊 Total amount this transaction: " + totalAmount + " CNY");

        // Enter payment amount
        double cashAmount;
        while (true) {
            try {
                System.out.print("Please enter payment amount (cash): ");
                cashAmount = Double.parseDouble(scanner.nextLine().trim());
                if (cashAmount >= totalAmount) {
                    break;
                } else {
                    System.out.println("❌ Insufficient payment! Amount due: " + totalAmount + " CNY, please re-enter");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Amount input error! Please enter a valid number");
            }
        }

        // Handle payment and generate receipt
        System.out.println("\n💳 Payment successful! Printing receipt...");
        Receipt saleReceipt = checkout.processPayment(cashAmount);
        saleReceipt.printReceipt();
        System.out.println("===== Sale completed =====");
    }

    /**
     * Return flow: user enters product ID and return quantity, supports multiple product returns
     */
    private static void handleReturn() {
        System.out.println("\n===== Entering [RETURN MODE] =====");
        System.out.println("Tip: Enter return product ID and quantity, input '0' to finish adding return items");

        while (true) {
            try {
                // Enter product ID
                System.out.print("Please enter return product ID (enter '0' to finish): ");
                String productId = scanner.nextLine().trim();

                // Finish adding return items
                if ("0".equals(productId)) {
                    double totalRefund = checkout.calculateTotalAmount();
                    if (totalRefund >= 0) {
                        System.out.println("⚠️  No return items added, returning to main menu");
                        checkout.cancelTransaction();
                        return;
                    }
                    break;
                }

                // Enter return quantity (must be positive, converted to negative internally)
                System.out.print("Please enter return quantity (positive integer): ");
                int quantity = Integer.parseInt(scanner.nextLine().trim());
                if (quantity <= 0) {
                    System.out.println("❌ Return quantity must be greater than 0, please re-enter");
                    continue;
                }

                // Add return item (negative quantity means return)
                checkout.addItem(productId, -quantity);
                double currentRefund = Math.abs(checkout.calculateTotalAmount());
                System.out.println("✅ Return item added successfully! Current refund amount: " + currentRefund + " CNY");

            } catch (NumberFormatException e) {
                System.out.println("❌ Quantity input error! Please enter a valid number");
            } catch (IllegalArgumentException e) {
                System.out.println("❌ Failed to add return item: " + e.getMessage());
            }
        }

        // Calculate refund amount
        double totalRefund = Math.abs(checkout.calculateTotalAmount());
        System.out.println("\n📊 Refund amount this time: " + totalRefund + " CNY");

        // Confirm return
        System.out.print("Confirm return? (Y/N): ");
        String confirm = scanner.nextLine().trim().toUpperCase();
        if ("Y".equals(confirm)) {
            // Handle return and generate receipt
            System.out.println("\n🔄 Processing return... Printing receipt...");
            Receipt returnReceipt = checkout.processReturn();
            returnReceipt.printReceipt();
            System.out.println("===== Return completed =====");
        } else {
            System.out.println("❌ Return cancelled");
            checkout.cancelTransaction();
        }
    }

    /**
     * Ask user whether to continue
     */
    private static boolean isContinue() {
        System.out.print("\nContinue using the POS system? (Y/N): ");
        String choice = scanner.nextLine().trim().toUpperCase();
        return "Y".equals(choice);
    }

    /**
     * Exit system
     */
    private static void exitSystem() {
        System.out.println("\n======================================");
        System.out.println("        Thank you for using the POS System!");
        System.out.println("            Have a nice day!");
        System.out.println("======================================");
        scanner.close(); // Close Scanner
    }
}