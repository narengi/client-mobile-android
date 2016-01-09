package xyz.narengi.android.common.dto;

import java.io.Serializable;
import java.util.List;

/**
 * @author Siavash Mahmoudpour
 */
public class House implements Serializable {

    private String Name;
    private String[] Images;
    private String Cost;
    private GeoPoint Position;
    private Host Host;
    private String Rating;
    private String Summary;
    private String CityName;
    private String URL;
    private HouseReview[] Reviews;
    private HouseFeature[] FeatureList;
    private String FeatureSummray;
    private String type;
    private int bedroomCount;
    private int guestCount;
    private int bedCount;
    private String costText;



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

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public HouseReview[] getReviews() {
        return Reviews;
    }

    public void setReviews(HouseReview[] reviews) {
        Reviews = reviews;
    }

    public HouseFeature[] getFeatureList() {
        return FeatureList;
    }

    public void setFeatureList(HouseFeature[] features) {
        FeatureList = features;
    }

    public String getFeatureSummray() {
        return FeatureSummray;
    }

    public void setFeatureSummray(String featureSummray) {
        FeatureSummray = featureSummray;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getBedroomCount() {
        return bedroomCount;
    }

    public void setBedroomCount(int bedroomCount) {
        this.bedroomCount = bedroomCount;
    }

    public int getGuestCount() {
        return guestCount;
    }

    public void setGuestCount(int guestCount) {
        this.guestCount = guestCount;
    }

    public int getBedCount() {
        return bedCount;
    }

    public void setBedCount(int bedCount) {
        this.bedCount = bedCount;
    }

    public String getCostText() {
        return costText;
    }

    public void setCostText(String costText) {
        this.costText = costText;
    }
}
