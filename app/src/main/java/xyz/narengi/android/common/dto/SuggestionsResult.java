package xyz.narengi.android.common.dto;

import com.google.gson.annotations.SerializedName;

/**
 * @author Siavash Mahmoudpour
 */
//@Parcel
public class SuggestionsResult {

    @SerializedName("House")
    private AroundPlaceHouse[] house;


    @SerializedName("City")
    private AroundPlaceCity[] city;

    @SerializedName("Attraction")
    private AroundPlaceAttraction[] attraction;



    public AroundPlaceCity[] getCity() {
        return city;
    }

    public void setCity(AroundPlaceCity[] city) {
        this.city = city;
    }

    public AroundPlaceAttraction[] getAttraction() {
        return attraction;
    }

    public void setAttraction(AroundPlaceAttraction[] attraction) {
        this.attraction = attraction;
    }

    public AroundPlaceHouse[] getHouse() {
        return house;
    }

    public void setHouse(AroundPlaceHouse[] house) {
        this.house = house;
    }
}
