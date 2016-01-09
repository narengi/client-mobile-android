package xyz.narengi.android.common.dto;

/**
 * @author Siavash Mahmoudpour
 */
public class City {

    private String Name;
    private String[] Images;
    private GeoPoint Position;
    private String Summary;
    private int HouseCount;
    private String HouseCountText;
    private String URL;
    private AroundPlaceAttraction[] Attraction;
    private AroundPlaceHouse[] Houses;


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
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

    public String getSummary() {
        return Summary;
    }

    public void setSummary(String summary) {
        Summary = summary;
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

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public AroundPlaceAttraction[] getAttraction() {
        return Attraction;
    }

    public void setAttraction(AroundPlaceAttraction[] attraction) {
        Attraction = attraction;
    }

    public AroundPlaceHouse[] getHouses() {
        return Houses;
    }

    public void setHouses(AroundPlaceHouse[] houses) {
        Houses = houses;
    }
}
