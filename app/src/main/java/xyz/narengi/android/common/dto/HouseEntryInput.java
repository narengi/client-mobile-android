package xyz.narengi.android.common.dto;

import java.io.Serializable;

/**
 * @author Siavash Mahmoudpour
 */
public class HouseEntryInput implements Serializable {

    private String Name;
    private HouseEntryPrice Price;
    private GeoPoint Position;
    private String Summary;
    private Location Location;
    private String type;
    private HouseSpec Spec;
    private String[] AvailableDates;
    private HouseFeature[] FeatureList;


    public HouseEntryInput() {
        Price = new HouseEntryPrice();
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public HouseEntryPrice getPrice() {
        return Price;
    }

    public void setPrice(HouseEntryPrice price) {
        this.Price = price;
    }

    public GeoPoint getPosition() {
        return Position;
    }

    public void setPosition(GeoPoint position) {
        this.Position = position;
    }

    public String getSummary() {
        return Summary;
    }

    public void setSummary(String summary) {
        this.Summary = summary;
    }

    public Location getLocation() {
        return Location;
    }

    public void setLocation(Location location) {
        this.Location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public HouseSpec getSpec() {
        return Spec;
    }

    public void setSpec(HouseSpec spec) {
        this.Spec = spec;
    }

    public String[] getAvailableDates() {
        return AvailableDates;
    }

    public void setAvailableDates(String[] availableDates) {
        this.AvailableDates = availableDates;
    }

    public HouseFeature[] getFeatureList() {
        return FeatureList;
    }

    public void setFeatureList(HouseFeature[] featureList) {
        this.FeatureList = featureList;
    }
}



