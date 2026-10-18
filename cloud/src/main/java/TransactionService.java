import java.time.Instant;
import java.util.function.LongSupplier;

public class TransactionService {

    public static final long DELIVERY_DEADLINE_SECONDS = 7L * 24 * 60 * 60;
    public static final long DAMAGE_WINDOW_SECONDS = 24L * 60 * 60;

    private final TransactionRepository repo;
    private final LongSupplier clock;   // returns "now" in epoch seconds

    public TransactionService(TransactionRepository repo) {
        this(repo, () -> Instant.now().getEpochSecond());
    }

    // The clock is passed in so a demo can fast-forward days without waiting
    public TransactionService(TransactionRepository repo, LongSupplier clock) {
        this.repo = repo;
        this.clock = clock;
    }

    public Transaction createOrder(String id, String buyerId, String sellerId,
                                   long amountCents, long deliveryFeeCents) {
        Transaction t = new Transaction(id, buyerId, sellerId, amountCents, deliveryFeeCents);
        repo.save(t);
        return t;
    }

    // Buyer approves the seller's photo of the exact item
    public Transaction confirmItem(String id, String buyerId, String photoKey, String photoHash) {
        Transaction t = load(id);
        requireStatus(t, TransactionStatus.CREATED);
        requireBuyer(t, buyerId);
        t.setItemPhotoKey(photoKey);
        t.setItemPhotoHash(photoHash);
        t.setStatus(TransactionStatus.ITEM_CONFIRMED);
        repo.save(t);
        return t;
    }

    // Simulated payment into the app. Starts the 7-day delivery clock
    public Transaction pay(String id, String buyerId) {
        Transaction t = load(id);
        requireStatus(t, TransactionStatus.ITEM_CONFIRMED);
        requireBuyer(t, buyerId);
        t.setPaidAt(clock.getAsLong());
        t.setStatus(TransactionStatus.PAID);
        repo.save(t);
        return t;
    }

    // role is "BUYER" or "COURIER". Starts the 1-day damage window
    public Transaction confirmDelivery(String id, String actorId, String role) {
        Transaction t = load(id);
        requireStatus(t, TransactionStatus.PAID);
        long now = clock.getAsLong();
        if (now > t.getPaidAt() + DELIVERY_DEADLINE_SECONDS) {
            throw new IllegalStateException("The 7-day delivery deadline has passed, the buyer is due a refund");
        }
        if ("BUYER".equals(role)) {
            requireBuyer(t, actorId);
        } else if (!"COURIER".equals(role)) {
            throw new IllegalArgumentException("Role must be BUYER or COURIER");
        }
        t.setDeliveredAt(now);
        t.setDeliveryConfirmedBy(role);
        t.setStatus(TransactionStatus.DELIVERED);
        repo.save(t);
        return t;
    }

    // Only allowed inside the 1-day window after delivery
    public Transaction reportDamage(String id, String buyerId, String damagePhotoKey) {
        Transaction t = load(id);
        requireStatus(t, TransactionStatus.DELIVERED);
        requireBuyer(t, buyerId);
        if (clock.getAsLong() > t.getDeliveredAt() + DAMAGE_WINDOW_SECONDS) {
            throw new IllegalStateException("The 1-day damage window has closed");
        }
        t.setDamageReported(true);
        t.setDamagePhotoKey(damagePhotoKey);
        t.setStatus(TransactionStatus.DAMAGE_REPORTED);
        repo.save(t);
        return t;
    }

    // Seller is paid once the damage window passes with no report
    public Transaction releasePayment(String id) {
        Transaction t = load(id);
        requireStatus(t, TransactionStatus.DELIVERED);
        if (clock.getAsLong() <= t.getDeliveredAt() + DAMAGE_WINDOW_SECONDS) {
            throw new IllegalStateException("The damage window is still open");
        }
        t.setStatus(TransactionStatus.COMPLETED);
        repo.save(t);
        return t;
    }

    // Buyer is refunded when 7 days pass after payment with no delivery
    public Transaction refundNonDelivery(String id) {
        Transaction t = load(id);
        requireStatus(t, TransactionStatus.PAID);
        if (clock.getAsLong() <= t.getPaidAt() + DELIVERY_DEADLINE_SECONDS) {
            throw new IllegalStateException("The 7-day delivery deadline has not passed yet");
        }
        t.setStatus(TransactionStatus.REFUNDED);
        repo.save(t);
        return t;
    }

    // Damaged item came back: seller confirms, buyer is refunded in full
    public Transaction confirmReturn(String id, String sellerId) {
        Transaction t = load(id);
        requireStatus(t, TransactionStatus.DAMAGE_REPORTED);
        if (!t.getSellerId().equals(sellerId)) {
            throw new IllegalStateException("Only the seller can confirm the return");
        }
        t.setStatus(TransactionStatus.REFUNDED);
        repo.save(t);
        return t;
    }

    private Transaction load(String id) {
        Transaction t = repo.load(id);
        if (t == null) {
            throw new IllegalArgumentException("No such transaction: " + id);
        }
        return t;
    }

    private void requireStatus(Transaction t, TransactionStatus expected) {
        if (t.getStatus() != expected) {
            throw new IllegalStateException("Transaction " + t.getTransactionId()
                    + " is " + t.getStatus() + ", expected " + expected);
        }
    }

    private void requireBuyer(Transaction t, String buyerId) {
        if (!t.getBuyerId().equals(buyerId)) {
            throw new IllegalStateException("Only the buyer can do this");
        }
    }
}