public class TransactionDemo {

    public static void main(String[] args) {
        try (TransactionRepository repo = new TransactionRepository()) {
            Transaction t = new Transaction("txn-001", "buyer-1", "seller-1", 12050, 3500);
            t.setItemPhotoKey("transactions/1/item.jpg");
            t.setItemPhotoHash("717227cc73c70bfe3c912a2a6a4ddb8fa52f8bbd016fc507a02f98c9d922d4d5");

            repo.save(t);
            System.out.println("Saved transaction " + t.getTransactionId());

            Transaction loaded = repo.load("txn-001");
            System.out.println("Loaded: " + loaded.getTransactionId()
                    + " | buyer=" + loaded.getBuyerId()
                    + " | seller=" + loaded.getSellerId()
                    + " | amount=" + loaded.getAmountCents() + " cents"
                    + " | status=" + loaded.getStatus()
                    + " | photo=" + loaded.getItemPhotoKey());
        }
    }
}