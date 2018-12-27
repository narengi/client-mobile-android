package xyz.narengi.android.common.dto;

import java.io.Serializable;

public class AccountProfile1 implements Serializable {
    private String avatar;
    private String bio;
    private String city;
    private String country;
    private String fullName;
    private House[] houses;
    private Pictures picture;
    private String province;
    private String uid;

    public String getAvatar() {
        return this.avatar;
    }

    public String getBio() {
        return this.bio;
    }

    public String getCity() {
        return this.city;
    }

    public String getCountry() {
        return this.country;
    }

    public String getFullName() {
        return this.fullName;
    }

    public House[] getHouses() {
        return this.houses;
    }

    public Pictures getPicture() {
        return this.picture;
    }

    public String getProvince() {
        return this.province;
    }

    public String getUid() {
        return this.uid;
    }

    public void setAvatar(String paramString) {
        this.avatar = paramString;
    }

    public void setBio(String paramString) {
        this.bio = paramString;
    }

    public void setCity(String paramString) {
        this.city = paramString;
    }

    public void setCountry(String paramString) {
        this.country = paramString;
    }

    public void setFullName(String paramString) {
        this.fullName = paramString;
    }

    public void setHouses(House[] paramArrayOfHouse) {
        this.houses = paramArrayOfHouse;
    }

    public void setPicture(Pictures paramPictures) {
        this.picture = paramPictures;
    }

    public void setProvince(String paramString) {
        this.province = paramString;
    }

    public void setUid(String paramString) {
        this.uid = paramString;
    }
}
