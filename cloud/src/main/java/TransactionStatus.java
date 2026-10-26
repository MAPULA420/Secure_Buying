public enum TransactionStatus {
    CREATED,          // order made
    ITEM_CONFIRMED,   // buyer approved the seller's photo
    PAID,             // buyer paid into the app (simulated)
    SHIPPED,          // seller's ID scanned at the courier, parcel handed over
    DELIVERED,        // courier scanned the buyer's ID, damage window running
    DAMAGE_REPORTED,  // buyer reported damage inside the window
    COMPLETED,        // seller paid
    REFUNDED          // buyer refunded
}