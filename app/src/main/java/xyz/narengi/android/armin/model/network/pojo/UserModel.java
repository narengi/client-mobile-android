package xyz.narengi.android.armin.model.network.pojo;

import android.util.Patterns;

import java.util.ArrayList;

/**
 * Created by arminghm on 1/29/17.
 */

public class UserModel {
    private String username;
    private String email;
    private String fullName;
    private String avatar;
    private String location;
    private String aboutMe;
    private ArrayList<HouseModel> houseModels;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public ArrayList<HouseModel> getHouseModels() {
        return houseModels;
    }

    public void setHouseModels(ArrayList<HouseModel> houseModels) {
        this.houseModels = houseModels;
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
