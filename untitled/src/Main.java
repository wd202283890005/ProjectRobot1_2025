/**
 * 主程序：测试销售和退货流程
 */
public class Main {
    public static void main(String[] args) {
        // 初始化收银台
        Checkout checkout = new Checkout();
        System.out.println("===== 测试【销售流程】=====");
        try {
            // 1. 添加商品（P001：可口可乐×2，P002：薯片×1）
            checkout.addItem("P001", 2);
            checkout.addItem("P002", 1);

            // 2. 计算总金额
            double total = checkout.calculateTotalAmount();
            System.out.println("应付金额：" + total + " 元");

            // 3. 顾客支付15元（现金）
            Receipt saleReceipt = checkout.processPayment(15.0);

            // 4. 打印收据
            saleReceipt.printReceipt();
        } catch (IllegalArgumentException e) {
            System.out.println("销售失败：" + e.getMessage());
            checkout.cancelTransaction();
        }

        System.out.println("\n===== 测试【退货流程】=====");
        try {
            // 1. 添加退货商品（P001：可口可乐×1，数量为负表示退货）
            checkout.addItem("P001", -1);

            // 2. 计算退款金额（总金额为负数）
            double refund = checkout.calculateTotalAmount();
            System.out.println("退款金额：" + Math.abs(refund) + " 元");

            // 3. 处理退货，生成收据
            Receipt returnReceipt = checkout.processReturn();

            // 4. 打印退货收据
            returnReceipt.printReceipt();
        } catch (IllegalArgumentException e) {
            System.out.println("退货失败：" + e.getMessage());
            checkout.cancelTransaction();
        }
    }
}