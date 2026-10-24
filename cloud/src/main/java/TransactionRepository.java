import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionRepository implements AutoCloseable {

    private static final String TABLE_NAME = "Transactions";

    private final DynamoDbClient db;

    public TransactionRepository() {
        this.db = DynamoDbClient.builder().region(Region.US_EAST_1).build();
    }

    public void save(Transaction t) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("transactionId", str(t.getTransactionId()));
        item.put("buyerId", str(t.getBuyerId()));
        item.put("sellerId", str(t.getSellerId()));
        item.put("amountCents", num(t.getAmountCents()));
        item.put("deliveryFeeCents", num(t.getDeliveryFeeCents()));
        item.put("status", str(t.getStatus().name()));
        item.put("damageReported", AttributeValue.builder().bool(t.isDamageReported()).build());

        // Optional fields: only store them once they have a value
        if (t.getItemPhotoKey() != null) item.put("itemPhotoKey", str(t.getItemPhotoKey()));
        if (t.getItemPhotoHash() != null) item.put("itemPhotoHash", str(t.getItemPhotoHash()));
        if (t.getPaidAt() != null) item.put("paidAt", num(t.getPaidAt()));
        if (t.getDeliveredAt() != null) item.put("deliveredAt", num(t.getDeliveredAt()));
        if (t.getDamagePhotoKey() != null) item.put("damagePhotoKey", str(t.getDamagePhotoKey()));
        if (t.getDeliveryConfirmedBy() != null) item.put("deliveryConfirmedBy", str(t.getDeliveryConfirmedBy()));

        db.putItem(PutItemRequest.builder().tableName(TABLE_NAME).item(item).build());
    }

    public Transaction load(String transactionId) {
        GetItemResponse response = db.getItem(GetItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(Map.of("transactionId", str(transactionId)))
                .build());

        Map<String, AttributeValue> i = response.item();
        if (i == null || i.isEmpty()) {
            return null;   // no such transaction
        }

        Transaction t = new Transaction(
                i.get("transactionId").s(),
                i.get("buyerId").s(),
                i.get("sellerId").s(),
                Long.parseLong(i.get("amountCents").n()),
                Long.parseLong(i.get("deliveryFeeCents").n()));

        t.setStatus(TransactionStatus.valueOf(i.get("status").s()));
        t.setDamageReported(i.get("damageReported").bool());
        if (i.containsKey("itemPhotoKey")) t.setItemPhotoKey(i.get("itemPhotoKey").s());
        if (i.containsKey("itemPhotoHash")) t.setItemPhotoHash(i.get("itemPhotoHash").s());
        if (i.containsKey("paidAt")) t.setPaidAt(Long.parseLong(i.get("paidAt").n()));
        if (i.containsKey("deliveredAt")) t.setDeliveredAt(Long.parseLong(i.get("deliveredAt").n()));
        if (i.containsKey("damagePhotoKey")) t.setDamagePhotoKey(i.get("damagePhotoKey").s());
        if (i.containsKey("deliveryConfirmedBy")) t.setDeliveryConfirmedBy(i.get("deliveryConfirmedBy").s());
        return t;
    }

    // Ids of all transactions currently in the given status
    public List<String> findIdsByStatus(TransactionStatus status) {
        ScanRequest request = ScanRequest.builder()
                .tableName(TABLE_NAME)
                .filterExpression("#s = :s")
                .expressionAttributeNames(Map.of("#s", "status"))
                .expressionAttributeValues(Map.of(":s", str(status.name())))
                .build();
        List<String> ids = new ArrayList<>();
        db.scanPaginator(request).items().forEach(item -> ids.add(item.get("transactionId").s()));
        return ids;
    }

    private static AttributeValue str(String value) {
        return AttributeValue.builder().s(value).build();
    }

    private static AttributeValue num(long value) {
        return AttributeValue.builder().n(Long.toString(value)).build();
    }

    @Override
    public void close() {
        db.close();
    }
}