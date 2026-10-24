import java.time.Instant;

public class SweeperDemo {

    public static void main(String[] args) {
        long realNow = Instant.now().getEpochSecond();
        try (TransactionRepository repo = new TransactionRepository()) {
            TransactionService today = new TransactionService(repo);

            for (String id : new String[]{"sweep-1", "sweep-2"}) {
                today.createOrder(id, "buyer-1", "seller-1", 5000, 2000);
                today.confirmItem(id, "buyer-1", "transactions/x/item.jpg", "hash");
                today.pay(id, "buyer-1");
            }

            System.out.println("Sweeping today (nothing is overdue):");
            new RefundSweeper(repo, today).run();

            System.out.println("Sweeping 8 days later:");
            TransactionService later = new TransactionService(repo, () -> realNow + 8L * 24 * 60 * 60);
            new RefundSweeper(repo, later).run();

            for (String id : new String[]{"sweep-1", "sweep-2"}) {
                System.out.println("  " + id + " -> " + repo.load(id).getStatus());
            }
        }
    }
}