package xyz.narengi.android.armin.model.network.pojo;

import android.util.Patterns;

/**
 * Created by arminghm on 1/29/17.
 */

public class UserModel {
    private String username;
    private String email;
    private String fullName;
    private String token;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public boolean emailValidation(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean passwordValidation(String password) {
        // TODO: arminghm 1/29/17 Be consistent with the server validation length.
        return password.length() > 6;
    }
}
