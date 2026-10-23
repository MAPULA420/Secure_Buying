import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.Map;

public class UserRepository implements AutoCloseable {

    private static final String TABLE_NAME = "Users";

    private final DynamoDbClient db;

    public UserRepository() {
        this.db = DynamoDbClient.builder().region(Region.US_EAST_1).build();
    }

    public void save(User u) {
        db.putItem(PutItemRequest.builder().tableName(TABLE_NAME).item(Map.of(
                "userId", s(u.getUserId()),
                "fullName", s(u.getFullName()),
                "role", s(u.getRole()),
                "verified", AttributeValue.builder().bool(u.isVerified()).build()
        )).build());
    }

    public User load(String userId) {
        Map<String, AttributeValue> i = db.getItem(GetItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(Map.of("userId", s(userId)))
                .build()).item();
        if (i == null || i.isEmpty()) {
            return null;   // not registered
        }
        return new User(i.get("userId").s(), i.get("fullName").s(),
                i.get("role").s(), i.get("verified").bool());
    }

    private static AttributeValue s(String value) {
        return AttributeValue.builder().s(value).build();
    }

    @Override
    public void close() {
        db.close();
    }
}