package xyz.narengi.android.common.dto;

/**
 * @author Siavash Mahmoudpour
 */
public class ProvinceCity {

    private String city;


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return getCity();
    }
}
