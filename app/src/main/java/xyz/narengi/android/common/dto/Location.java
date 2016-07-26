package xyz.narengi.android.common.dto;

import java.io.Serializable;

/**
 * @author Siavash Mahmoudpour
 */
public class Location implements Serializable {

    private String City;
    private String Province;

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        this.City = city;
    }

    public String getProvince() {
        return Province;
    }

    public void setProvince(String province) {
        this.Province = province;
    }
}