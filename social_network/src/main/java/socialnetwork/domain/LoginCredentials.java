package socialnetwork.domain;

public class LoginCredentials extends Entity<String> {
    private Long userID;
    private String password;

    public LoginCredentials(Long userID, String password) {
        this.userID = userID;
        this.password = password;
    }

    public Long getUserID() {
        return userID;
    }

    public String getPassword() {
        return password;
    }
}
