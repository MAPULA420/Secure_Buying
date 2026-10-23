public class User {

    private final String userId;
    private final String fullName;
    private final String role;        // BUYER, SELLER or COURIER
    private final boolean verified;   // simulated ID check, the ID number is never stored

    public User(String userId, String fullName, String role, boolean verified) {
        if (!role.equals("BUYER") && !role.equals("SELLER") && !role.equals("COURIER")) {
            throw new IllegalArgumentException("Role must be BUYER, SELLER or COURIER");
        }
        this.userId = userId;
        this.fullName = fullName;
        this.role = role;
        this.verified = verified;
    }

    public String getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
    public boolean isVerified() { return verified; }
}