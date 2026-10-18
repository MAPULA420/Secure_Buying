public class Transaction {

    private String transactionId;
    private String buyerId;
    private String sellerId;
    private long amountCents;
    private long deliveryFeeCents;
    private TransactionStatus status = TransactionStatus.CREATED;

    private String itemPhotoKey;      // S3 key of the seller's pre-shipping photo
    private String itemPhotoHash;     // SHA-256 of that photo
    private Long paidAt;              // epoch seconds, set when payment arrives
    private Long deliveredAt;         // epoch seconds, starts the 1-day window
    private boolean damageReported;
    private String damagePhotoKey;
    private String deliveryConfirmedBy;   // BUYER or COURIER

    public Transaction(String transactionId, String buyerId, String sellerId,
                       long amountCents, long deliveryFeeCents) {
        this.transactionId = transactionId;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.amountCents = amountCents;
        this.deliveryFeeCents = deliveryFeeCents;
    }

    public String getTransactionId() { return transactionId; }
    public String getBuyerId() { return buyerId; }
    public String getSellerId() { return sellerId; }
    public long getAmountCents() { return amountCents; }
    public long getDeliveryFeeCents() { return deliveryFeeCents; }

    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }

    public String getItemPhotoKey() { return itemPhotoKey; }
    public void setItemPhotoKey(String itemPhotoKey) { this.itemPhotoKey = itemPhotoKey; }

    public String getItemPhotoHash() { return itemPhotoHash; }
    public void setItemPhotoHash(String itemPhotoHash) { this.itemPhotoHash = itemPhotoHash; }

    public Long getPaidAt() { return paidAt; }
    public void setPaidAt(Long paidAt) { this.paidAt = paidAt; }

    public Long getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(Long deliveredAt) { this.deliveredAt = deliveredAt; }

    public boolean isDamageReported() { return damageReported; }
    public void setDamageReported(boolean damageReported) { this.damageReported = damageReported; }

    public String getDamagePhotoKey() { return damagePhotoKey; }

    public String getDeliveryConfirmedBy() { return deliveryConfirmedBy; }
    public void setDeliveryConfirmedBy(String deliveryConfirmedBy) { this.deliveryConfirmedBy = deliveryConfirmedBy; }
    public void setDamagePhotoKey(String damagePhotoKey) { this.damagePhotoKey = damagePhotoKey; }
}