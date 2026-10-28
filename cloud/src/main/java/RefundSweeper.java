import java.util.ArrayList;
import java.util.List;

public class RefundSweeper {

    private final TransactionRepository repo;
    private final TransactionService service;

    public RefundSweeper(TransactionRepository repo, TransactionService service) {
        this.repo = repo;
        this.service = service;
    }

    // Refunds every paid or shipped order whose 7-day delivery deadline has passed
    public int run() {
        List<String> ids = new ArrayList<>(repo.findIdsByStatus(TransactionStatus.PAID));
       
        int refunded = 0;
        for (String id : ids) {
            try {
                service.refundNonDelivery(id);
                System.out.println("  Refunded " + id);
                refunded++;
            } catch (IllegalStateException e) {
                System.out.println("  Not due yet: " + id);
            }
        }
        return refunded;
    }

    public static void main(String[] args) {
        try (TransactionRepository repo = new TransactionRepository()) {
            RefundSweeper sweeper = new RefundSweeper(repo, new TransactionService(repo));
            System.out.println("Refunded " + sweeper.run() + " order(s)");
        }
    }
}