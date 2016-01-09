package xyz.narengi.android.common.dto;

import org.parceler.Parcel;

import java.io.Serializable;

/**
 * @author Siavash Mahmoudpour
 */
@Parcel
public class AroundPlaceHouse implements Serializable {

    private String Name;
    private String[] Images;
    private String Cost;
    private GeoPoint Position;
    private Host Host;
    private String Rating;
    private String Summary;
    private String CityName;
    private String FeatureSummray;
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

    public String getCost() {
        return Cost;
    }

    public void setCost(String cost) {
        Cost = cost;
    }

    public GeoPoint getPosition() {
        return Position;
    }

    public void setPosition(GeoPoint position) {
        Position = position;
    }

    public xyz.narengi.android.common.dto.Host getHost() {
        return Host;
    }

    public void setHost(xyz.narengi.android.common.dto.Host host) {
        Host = host;
    }

    public String getRating() {
        return Rating;
    }

    public void setRating(String rating) {
        Rating = rating;
    }

    public String getSummary() {
        return Summary;
    }

    public void setSummary(String summary) {
        Summary = summary;
    }

    public String getCityName() {
        return CityName;
    }

    public void setCityName(String cityName) {
        CityName = cityName;
    }

    public String getFeatureSummray() {
        return FeatureSummray;
    }

    public void setFeatureSummray(String featureSummray) {
        FeatureSummray = featureSummray;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }
}
