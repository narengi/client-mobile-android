package xyz.narengi.android.common.dto;

/**
 * @author Siavash Mahmoudpour
 */
//@Parcel
public class SuggestionsResult {

//    @SerializedName("City")
    private AroundPlaceCity[] City;

//    @SerializedName("Attraction")
    private AroundPlaceAttraction[] Attraction;

//    @SerializedName("House")
    private AroundPlaceHouse[] House;


    public AroundPlaceCity[] getCity() {
        return City;
    }

    public void setCity(AroundPlaceCity[] city) {
        this.City = city;
    }

    public AroundPlaceAttraction[] getAttraction() {
        return Attraction;
    }

    public void setAttraction(AroundPlaceAttraction[] attraction) {
        this.Attraction = attraction;
    }

    public AroundPlaceHouse[] getHouse() {
        return House;
    }

    public void setHouse(AroundPlaceHouse[] house) {
        this.House = house;
    }
}
