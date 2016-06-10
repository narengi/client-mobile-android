package xyz.narengi.android.common.dto;

import java.io.Serializable;

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
    private String Type;
    private String featureSummary;
    private String ReviewsURL;
    private String FeatureListURL;
    private int reviewsCount;
    private HousePrice Price;
    private HouseSpec Spec;
    private HouseExtraService[] ExtraServices;
    private String bookingUrl;
    private Commission Commission;
    private Location location;
    private String address;


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

    public void setFeatureList(HouseFeature[] featureList) {
        FeatureList = featureList;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getFeatureSummary() {
        return featureSummary;
    }

    public void setFeatureSummary(String featureSummary) {
        this.featureSummary = featureSummary;
    }

    public String getReviewsURL() {
        return ReviewsURL;
    }

    public void setReviewsURL(String reviewsURL) {
        ReviewsURL = reviewsURL;
    }

    public String getFeatureListURL() {
        return FeatureListURL;
    }

    public void setFeatureListURL(String featureListURL) {
        FeatureListURL = featureListURL;
    }

    public int getReviewsCount() {
        return reviewsCount;
    }

    public void setReviewsCount(int reviewsCount) {
        this.reviewsCount = reviewsCount;
    }

    public HousePrice getPrice() {
        return Price;
    }

    public void setPrice(HousePrice price) {
        Price = price;
    }

    public HouseSpec getSpec() {
        return Spec;
    }

    public void setSpec(HouseSpec spec) {
        Spec = spec;
    }

    public HouseExtraService[] getExtraServices() {
        return ExtraServices;
    }

    public void setExtraServices(HouseExtraService[] extraServices) {
        ExtraServices = extraServices;
    }

    public String getBookingUrl() {
        return bookingUrl;
    }

    public void setBookingUrl(String bookingUrl) {
        this.bookingUrl = bookingUrl;
    }

    public House.Commission getCommission() {
        return Commission;
    }

    public void setCommission(House.Commission commission) {
        Commission = commission;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public class Commission implements Serializable {
        private double rate;
        private double fee;

        public double getRate() {
            return rate;
        }

        public void setRate(double rate) {
            this.rate = rate;
        }

        public double getFee() {
            return fee;
        }

        public void setFee(double fee) {
            this.fee = fee;
        }
    }
}