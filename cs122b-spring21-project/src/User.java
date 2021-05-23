/**
 * This User class only has the username field in this example.
 * You can add more attributes such as the user's shopping cart items.
 */
public class User {

    private final String username;
    private String lastAccessTime;
    private final String customerId;
    private final String userType;
    public User(String username, String lastAccessTime, String customerId,String userType) {
        this.username = username; this.lastAccessTime = lastAccessTime;
        this.customerId = customerId;
        this.userType = userType;
    }

    public String getUsername() {
        return username;
    }

    public String getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(String lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public String getCustomerId() {
        return customerId;
    }
    public String getUserType(){return userType;}

}
