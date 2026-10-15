import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.HexFormat;

public class PhotoUploader implements AutoCloseable {

    private static final String BUCKET_NAME = System.getenv("PHOTO_BUCKET");
    private static final Region REGION = Region.US_EAST_1;

    private final S3Client s3;

    public PhotoUploader() {
        this.s3 = S3Client.builder().region(REGION).build();
    }

    public String uploadPhoto(String localFilePath, int transactionId, String photoType) {
        String key = "transactions/" + transactionId + "/" + photoType + ".jpg";
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(key)
                .contentType("image/jpeg")
                .build();
        s3.putObject(request, RequestBody.fromFile(Paths.get(localFilePath)));
        return key;
    }

    public void downloadPhoto(String key, String saveToPath) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(key)
                .build();
        s3.getObject(request, Paths.get(saveToPath));
    }

    public static String sha256(String filePath) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(Files.readAllBytes(Path.of(filePath))));
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void close() {
        s3.close();
    }

    public static void main(String[] args) throws IOException {
        if (BUCKET_NAME == null || BUCKET_NAME.isBlank()) {
            throw new IllegalStateException("Set the PHOTO_BUCKET environment variable first");
        }
        try (PhotoUploader uploader = new PhotoUploader()) {
            String key = uploader.uploadPhoto("test.jpg", 1, "item");
            System.out.println("Uploaded key: " + key);
            System.out.println("SHA-256: " + sha256("test.jpg"));
            Files.deleteIfExists(Paths.get("downloaded.jpg"));
            uploader.downloadPhoto(key, "downloaded.jpg");
            System.out.println("Downloaded OK");
        }
    }
}