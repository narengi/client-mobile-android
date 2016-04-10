package xyz.narengi.android.common.dto;

import org.parceler.Parcel;

/**
 * @author Siavash Mahmoudpour
 */
@Parcel
public class AroundPlaceAttraction {

    private String Name;
    private String[] Images;
    private String CityName;
    private String AroundHousesText;
    private String[] AroundHouses;
    private GeoPoint Position;
    private String URL;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String[] getImages() {
        return Images;
    }

    public void setImages(String[] images) {
        Images = images;
    }

    public String getCityName() {
        return CityName;
    }

    public void setCityName(String cityName) {
        CityName = cityName;
    }

    public String getAroundHousesText() {
        return AroundHousesText;
    }

    public void setAroundHousesText(String aroundHousesText) {
        AroundHousesText = aroundHousesText;
    }

    public String[] getAroundHouses() {
        return AroundHouses;
    }

    public void setAroundHouses(String[] aroundHouses) {
        AroundHouses = aroundHouses;
    }

    public GeoPoint getPosition() {
        return Position;
    }

    public void setPosition(GeoPoint position) {
        Position = position;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }
}
