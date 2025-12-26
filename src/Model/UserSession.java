package Model;

/**
 * Returns who is using the app
 */
public class UserSession {
    private static UserSession instance;
    private String username;

    private UserSession() {
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    //after logout
    public void clearUserSession() {
        this.username = null;
        instance = null;
    }
}
