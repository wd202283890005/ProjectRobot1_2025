import java.util.Scanner;

/**
 * 交互式POS主程序：支持持续运行、用户手动输入操作，适配实际使用场景
 */
public class Main {
    // 全局Scanner（避免重复创建）
    private static final Scanner scanner = new Scanner(System.in);
    // 收银台实例（核心业务对象）
    private static final Checkout checkout = new Checkout();

    public static void main(String[] args) {
        System.out.println("======================================");
        System.out.println("        超市POS系统 - 交互式版本");
        System.out.println("======================================");
        System.out.println("支持功能：1. 处理销售  2. 处理退货  3. 退出系统");
        System.out.println("======================================");

        // 持续运行：循环显示菜单，直到用户选择退出
        while (true) {
            try {
                // 1. 显示主菜单，获取用户选择
                System.out.print("\n请输入功能编号（1-3）：");
                int choice = Integer.parseInt(scanner.nextLine().trim());

                // 2. 根据选择执行对应功能
                switch (choice) {
                    case 1:
                        handleSale(); // 处理销售
                        break;
                    case 2:
                        handleReturn(); // 处理退货
                        break;
                    case 3:
                        exitSystem(); // 退出系统
                        return; // 终止程序
                    default:
                        System.out.println("❌ 输入错误！请输入1-3之间的数字");
                }

                // 3. 操作完成后，询问是否继续
                if (!isContinue()) {
                    exitSystem();
                    return;
                }

            } catch (NumberFormatException e) {
                // 捕获非数字输入异常
                System.out.println("❌ 输入格式错误！请输入有效数字");
            } catch (IllegalArgumentException e) {
                // 捕获业务异常（如商品不存在、库存不足等）
                System.out.println("❌ 操作失败：" + e.getMessage());
                checkout.cancelTransaction(); // 取消当前交易
                // 询问是否继续
                if (!isContinue()) {
                    exitSystem();
                    return;
                }
            } catch (Exception e) {
                // 捕获其他未知异常
                System.out.println("❌ 系统异常：" + e.getMessage());
                checkout.cancelTransaction();
                if (!isContinue()) {
                    exitSystem();
                    return;
                }
            }
        }
    }

    /**
     * 处理销售流程：用户输入商品ID、数量，支持多商品添加
     */
    private static void handleSale() {
        System.out.println("\n===== 进入【销售模式】=====");
        System.out.println("提示：输入商品ID和购买数量，输入'0'结束添加商品");

        while (true) {
            try {
                // 输入商品ID
                System.out.print("请输入商品ID（输入'0'结束添加）：");
                String productId = scanner.nextLine().trim();

                // 结束添加商品
                if ("0".equals(productId)) {
                    if (checkout.calculateTotalAmount() <= 0) {
                        System.out.println("⚠️  未添加任何商品，返回主菜单");
                        return;
                    }
                    break;
                }

                // 输入购买数量（必须为正数）
                System.out.print("请输入购买数量（正数）：");
                int quantity = Integer.parseInt(scanner.nextLine().trim());
                if (quantity <= 0) {
                    System.out.println("❌ 购买数量必须大于0，请重新输入");
                    continue;
                }

                // 添加商品到交易
                checkout.addItem(productId, quantity);
                System.out.println("✅ 商品添加成功！当前累计金额：" + checkout.calculateTotalAmount() + " 元");

            } catch (NumberFormatException e) {
                System.out.println("❌ 数量输入错误！请输入有效数字");
            } catch (IllegalArgumentException e) {
                System.out.println("❌ 商品添加失败：" + e.getMessage());
            }
        }

        // 计算总金额
        double totalAmount = checkout.calculateTotalAmount();
        System.out.println("\n📊 本次交易总金额：" + totalAmount + " 元");

        // 输入支付金额
        double cashAmount;
        while (true) {
            try {
                System.out.print("请输入支付金额（现金）：");
                cashAmount = Double.parseDouble(scanner.nextLine().trim());
                if (cashAmount >= totalAmount) {
                    break;
                } else {
                    System.out.println("❌ 支付金额不足！应付：" + totalAmount + " 元，请重新输入");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ 金额输入错误！请输入有效数字");
            }
        }

        // 处理支付并生成收据
        System.out.println("\n💳 支付成功！正在打印收据...");
        Receipt saleReceipt = checkout.processPayment(cashAmount);
        saleReceipt.printReceipt();
        System.out.println("===== 销售流程结束 =====");
    }

    /**
     * 处理退货流程：用户输入商品ID、退货数量，支持多商品退货
     */
    private static void handleReturn() {
        System.out.println("\n===== 进入【退货模式】=====");
        System.out.println("提示：输入退货商品ID和数量，输入'0'结束添加退货商品");

        while (true) {
            try {
                // 输入商品ID
                System.out.print("请输入退货商品ID（输入'0'结束添加）：");
                String productId = scanner.nextLine().trim();

                // 结束添加退货商品
                if ("0".equals(productId)) {
                    double totalRefund = checkout.calculateTotalAmount();
                    if (totalRefund >= 0) {
                        System.out.println("⚠️  未添加任何退货商品，返回主菜单");
                        checkout.cancelTransaction();
                        return;
                    }
                    break;
                }

                // 输入退货数量（必须为正数，程序内部转为负数）
                System.out.print("请输入退货数量（正数）：");
                int quantity = Integer.parseInt(scanner.nextLine().trim());
                if (quantity <= 0) {
                    System.out.println("❌ 退货数量必须大于0，请重新输入");
                    continue;
                }

                // 添加退货商品（数量传负数表示退货）
                checkout.addItem(productId, -quantity);
                double currentRefund = Math.abs(checkout.calculateTotalAmount());
                System.out.println("✅ 退货商品添加成功！当前应退金额：" + currentRefund + " 元");

            } catch (NumberFormatException e) {
                System.out.println("❌ 数量输入错误！请输入有效数字");
            } catch (IllegalArgumentException e) {
                System.out.println("❌ 退货商品添加失败：" + e.getMessage());
            }
        }

        // 计算退款金额
        double totalRefund = Math.abs(checkout.calculateTotalAmount());
        System.out.println("\n📊 本次应退金额：" + totalRefund + " 元");

        // 确认退货
        System.out.print("是否确认退货？（Y/N）：");
        String confirm = scanner.nextLine().trim().toUpperCase();
        if ("Y".equals(confirm)) {
            // 处理退货并生成收据
            System.out.println("\n🔄 退货处理中...正在打印退货收据...");
            Receipt returnReceipt = checkout.processReturn();
            returnReceipt.printReceipt();
            System.out.println("===== 退货流程结束 =====");
        } else {
            System.out.println("❌ 已取消退货");
            checkout.cancelTransaction();
        }
    }

    /**
     * 询问用户是否继续操作
     */
    private static boolean isContinue() {
        System.out.print("\n是否继续使用POS系统？（Y/N）：");
        String choice = scanner.nextLine().trim().toUpperCase();
        return "Y".equals(choice);
    }

    /**
     * 退出系统
     */
    private static void exitSystem() {
        System.out.println("\n======================================");
        System.out.println("        感谢使用超市POS系统！");
        System.out.println("           祝您工作顺利！");
        System.out.println("======================================");
        scanner.close(); // 关闭Scanner
    }
}