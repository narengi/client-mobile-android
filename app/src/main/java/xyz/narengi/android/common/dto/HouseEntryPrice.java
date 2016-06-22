package xyz.narengi.android.common.dto;

import java.io.Serializable;

/**
 * @author Siavash Mahmoudpour
 */
public class HouseEntryPrice implements Serializable {

    private double price;
    private double extraGuestPrice;

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getExtraGuestPrice() {
        return extraGuestPrice;
    }

    public void setExtraGuestPrice(double extraGuestPrice) {
        this.extraGuestPrice = extraGuestPrice;
    }
}
