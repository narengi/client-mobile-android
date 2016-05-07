package xyz.narengi.android.common.dto;

import com.byagowi.persiancalendar.Entity.Day;

import java.io.Serializable;

/**
 * @author Siavash Mahmoudpour
 */
public class BookProperties implements Serializable {

    private House house;
    private int guestsCount;
    private int daysCount;
    private HouseExtraService[] extraServices;
    private BookPriceItem[] priceItems;
    private Day arriveDay;
    private Day departDay;
    private double totalPrice;


    public House getHouse() {
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
    }

    public int getGuestsCount() {
        return guestsCount;
    }

    public void setGuestsCount(int guestsCount) {
        this.guestsCount = guestsCount;
    }

    public int getDaysCount() {
        return daysCount;
    }

    public void setDaysCount(int daysCount) {
        this.daysCount = daysCount;
    }

    public HouseExtraService[] getExtraServices() {
        return extraServices;
    }

    public void setExtraServices(HouseExtraService[] extraServices) {
        this.extraServices = extraServices;
    }

    public BookPriceItem[] getPriceItems() {
        return priceItems;
    }

    public void setPriceItems(BookPriceItem[] priceItems) {
        this.priceItems = priceItems;
    }

    public Day getArriveDay() {
        return arriveDay;
    }

    public void setArriveDay(Day arriveDay) {
        this.arriveDay = arriveDay;
    }

    public Day getDepartDay() {
        return departDay;
    }

    public void setDepartDay(Day departDay) {
        this.departDay = departDay;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
