package xyz.narengi.android.armin.model.network.pojo;

import java.util.ArrayList;

/**
 * Created by arminghm on 1/27/17.
 */

public class HouseModel {
    private String name;
    private String description;
    private String priceRent;
    private ArrayList<String> images;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriceRent() {
        return priceRent;
    }

    public void setPriceRent(String priceRent) {
        this.priceRent = priceRent;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }
}
