package xyz.narengi.android.common.dto;

import org.parceler.Parcel;

/**
 * @author Siavash Mahmoudpour
 */
@Parcel
public class AroundPlaceCity {

    private String Name;
    private String[] Images;
    private GeoPoint Position;
    private int HouseCount;
    private String HouseCountText;
    private String Summary;
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

    public GeoPoint getPosition() {
        return Position;
    }

    public void setPosition(GeoPoint position) {
        Position = position;
    }

    public int getHouseCount() {
        return HouseCount;
    }

    public void setHouseCount(int houseCount) {
        HouseCount = houseCount;
    }

    public String getHouseCountText() {
        return HouseCountText;
    }

    public void setHouseCountText(String houseCountText) {
        HouseCountText = houseCountText;
    }

    public String getSummary() {
        return Summary;
    }

    public void setSummary(String summary) {
        Summary = summary;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }
}
