package xyz.narengi.android.common.dto;

import java.io.Serializable;
import java.util.Locale;

import xyz.narengi.android.common.model.*;
import xyz.narengi.android.common.model.AroundLocation;
import xyz.narengi.android.service.WebServiceConstants;

/**
 * @author Siavash Mahmoudpour
 */
public class House implements Serializable {

    private String name;
    private String[] Images;
    private String Cost;
    private GeoPoint Position;
    private Host Host;
    private String Rating;
    private String Summary;
    private String CityName;
    private String ProvinceName;
	private String detailUrl;
	private String[] dates;
    private HouseReview[] Reviews;
    private HouseFeature[] FeatureList;
    private Type type;
    private String featureSummary;
    private String ReviewsURL;
    private String FeatureListURL;
    private int reviewsCount;
    private HousePrice prices;
    private HouseSpec spec;
    private HouseExtraService[] ExtraServices;
    private String bookingUrl;
    private Commission Commission;
    private Location location;
    private String address;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] getDates() {
		return dates;
	}

	public void setDates(String[] dates) {
		this.dates = dates;
	}

	public static String getDetailUrl(String houseId) {
        return String.format(Locale.ENGLISH, WebServiceConstants.House.HOUSE_DETAILS_API_FORMAT, houseId);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
		this.name = name;
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

    public String getProvinceName() {
        return ProvinceName;
    }

    public void setProvinceName(String provinceName) {
        ProvinceName = provinceName;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
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
        return prices;
    }

    public void setPrice(HousePrice price) {
        this.prices = price;
    }

    public HouseSpec getSpec() {
        return spec;
    }

    public void setSpec(HouseSpec spec) {
		this.spec = spec;
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