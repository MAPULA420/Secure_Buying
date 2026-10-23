public class UserDemo {

    public static void main(String[] args) {
        try (UserRepository repo = new UserRepository()) {
            repo.save(new User("buyer-1", "Test Buyer", "BUYER", true));
            repo.save(new User("seller-1", "Test Seller", "SELLER", true));
            repo.save(new User("courier-1", "Test Courier", "COURIER", true));
            System.out.println("Registered 3 users");

            User u = repo.load("seller-1");
            System.out.println("Loaded: " + u.getUserId() + " | " + u.getFullName()
                    + " | " + u.getRole() + " | verified=" + u.isVerified());
            System.out.println("Unknown user: " + repo.load("nobody"));
        }
    }
}