package xyz.narengi.android.common.dto;

import java.io.Serializable;

/**
 * @author Siavash Mahmoudpour
 */
public class HouseSpec implements Serializable {

    private int bedroom;
    private int guest_count;
    private int max_guest_count;
    private int bed;

	public int getBedroomCount() {
        return bedroom;
    }

    public void setBedroomCount(int bedroomCount) {
        this.bedroom = bedroomCount;
    }

    public int getGuestCount() {
        return guest_count;
    }

    public void setGuestCount(int guestCount) {
        this.guest_count = guestCount;
    }

    public int getMaxGuestCount() {
        return max_guest_count;
    }

    public void setMaxGuestCount(int maxGuestCount) {
        this.max_guest_count = maxGuestCount;
    }

    public int getBedCount() {
        return bed;
    }

    public void setBedCount(int bedCount) {
        this.bed = bedCount;
    }
}
