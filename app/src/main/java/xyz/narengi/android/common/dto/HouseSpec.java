package xyz.narengi.android.common.dto;

import java.io.Serializable;

/**
 * @author Siavash Mahmoudpour
 */
public class HouseSpec implements Serializable {

    private int bedroom;
    private int guestCount;
    private int maxGuestCount;
    private int bed;

	public int getBedroomCount() {
        return bedroom;
    }

    public void setBedroomCount(int bedroomCount) {
        this.bedroom = bedroomCount;
    }

    public int getGuestCount() {
        return guestCount;
    }

    public void setGuestCount(int guestCount) {
        this.guestCount = guestCount;
    }

    public int getMaxGuestCount() {
        return maxGuestCount;
    }

    public void setMaxGuestCount(int maxGuestCount) {
        this.maxGuestCount = maxGuestCount;
    }

    public int getBedCount() {
        return bed;
    }

    public void setBedCount(int bedCount) {
        this.bed = bedCount;
    }
}
