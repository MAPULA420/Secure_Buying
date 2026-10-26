public class OrderFlowDemo {

    private static final long DAY = 24L * 60 * 60;

    public static void main(String[] args) {
        long[] now = {1_000_000L};   // fake clock, in seconds
        try (TransactionRepository repo = new TransactionRepository()) {
            TransactionService service = new TransactionService(repo, () -> now[0]);

            System.out.println("--- Scenario 1: happy path, tracked step by step ---");
            show(service.createOrder("demo-happy", "buyer-1", "seller-1", 12050, 3500));
            show(service.confirmItem("demo-happy", "buyer-1", "transactions/1/item.jpg", "hash123"));
            show(service.pay("demo-happy", "buyer-1"));
            expectFailure("delivery before the seller ships", () -> service.confirmDelivery("demo-happy", "buyer-1"));
            expectFailure("buyer marking the order shipped", () -> service.markShipped("demo-happy", "buyer-1"));
            show(service.markShipped("demo-happy", "seller-1"));
            expectFailure("handover to the wrong person", () -> service.confirmDelivery("demo-happy", "someone-else"));
            show(service.confirmDelivery("demo-happy", "buyer-1"));
            expectFailure("release payment too early", () -> service.releasePayment("demo-happy"));
            now[0] += DAY + 1;
            show(service.releasePayment("demo-happy"));
            expectFailure("damage report after payout", () -> service.reportDamage("demo-happy", "buyer-1", "x.jpg"));

            System.out.println("--- Scenario 2: shipped but never delivered ---");
            service.createOrder("demo-late", "buyer-1", "seller-1", 5000, 2000);
            service.confirmItem("demo-late", "buyer-1", "transactions/2/item.jpg", "hash456");
            service.pay("demo-late", "buyer-1");
            show(service.markShipped("demo-late", "seller-1"));
            expectFailure("refund too early", () -> service.refundNonDelivery("demo-late"));
            now[0] += 7 * DAY + 1;
            expectFailure("delivery after the deadline", () -> service.confirmDelivery("demo-late", "buyer-1"));
            show(service.refundNonDelivery("demo-late"));

            System.out.println("--- Scenario 3: damaged item ---");
            service.createOrder("demo-damage", "buyer-1", "seller-1", 8000, 2500);
            service.confirmItem("demo-damage", "buyer-1", "transactions/3/item.jpg", "hash789");
            service.pay("demo-damage", "buyer-1");
            service.markShipped("demo-damage", "seller-1");
            service.confirmDelivery("demo-damage", "buyer-1");
            service.reportDamage("demo-damage", "buyer-1", "transactions/3/damage.jpg");
            expectFailure("buyer confirming the return", () -> service.confirmReturn("demo-damage", "buyer-1"));
            show(service.confirmReturn("demo-damage", "seller-1"));
        }
    }

    private static void show(Transaction t) {
        System.out.println("  " + t.getTransactionId() + " -> " + t.getStatus());
    }

    private static void expectFailure(String label, Runnable action) {
        try {
            action.run();
            System.out.println("  UNEXPECTED: '" + label + "' was allowed");
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("  blocked (" + label + "): " + e.getMessage());
        }
    }
}