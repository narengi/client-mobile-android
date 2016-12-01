package xyz.narengi.android.common.dto;

import java.io.Serializable;

/**
 * @author Siavash Mahmoudpour
 */
public class HousePrice implements Serializable {

    private int price;
    private String currency;
    private String currencyText;
    private double weeklyDiscount;
    private double monthlyDiscount;
    private int extraGuestPrice;

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrencyText() {
        return currencyText;
    }

    public void setCurrencyText(String currencyText) {
        this.currencyText = currencyText;
    }

    public double getWeeklyDiscount() {
        return weeklyDiscount;
    }

    public void setWeeklyDiscount(double weeklyDiscount) {
        this.weeklyDiscount = weeklyDiscount;
    }

    public double getMonthlyDiscount() {
        return monthlyDiscount;
    }

    public void setMonthlyDiscount(double monthlyDiscount) {
        this.monthlyDiscount = monthlyDiscount;
    }

    public int getExtraGuestPrice() {
        return extraGuestPrice;
    }

    public void setExtraGuestPrice(int extraGuestPrice) {
        this.extraGuestPrice = extraGuestPrice;
    }
}
