package xyz.narengi.android.common.dto;

import java.io.Serializable;

/**
 * @author Siavash Mahmoudpour
 */
public class HouseAvailableDates implements Serializable {

    private String lastAllowedDate;
    private String startDate;
    private String endDate;
    private String[] dates;


    public String getLastAllowedDate() {
        return lastAllowedDate;
    }

    public void setLastAllowedDate(String lastAllowedDate) {
        this.lastAllowedDate = lastAllowedDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String[] getDates() {
        return dates;
    }

    public void setDates(String[] dates) {
        this.dates = dates;
    }
}
