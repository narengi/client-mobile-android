package xyz.narengi.android.common.dto;

/**
 * @author Siavash Mahmoudpour
 */
public class AccessToken {

    private String username;
    private String token;
    private String type;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
