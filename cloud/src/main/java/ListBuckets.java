import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public class ListBuckets {
    public static void main(String[] args) {
        S3Client s3 = S3Client.builder()
                .region(Region.US_EAST_1)
                .build();

        s3.listBuckets().buckets()
                .forEach(bucket -> System.out.println(bucket.name()));
    }
}