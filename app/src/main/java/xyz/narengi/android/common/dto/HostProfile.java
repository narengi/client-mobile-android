package xyz.narengi.android.common.dto;

import java.io.Serializable;

/**
 * @author Siavash Mahmoudpour
 */
public class HostProfile implements Serializable {

    private String DisplayName;
    private String ImageUrl;
    private String Job;
    private String CellNumber;
    private House[] Houses;
    private String MemberFrom;
    private String LocationText;
    private String Summary;
    private HouseReview[] reviews;


    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getJob() {
        return Job;
    }

    public void setJob(String job) {
        Job = job;
    }

    public String getCellNumber() {
        return CellNumber;
    }

    public void setCellNumber(String cellNumber) {
        CellNumber = cellNumber;
    }

    public House[] getHouses() {
        return Houses;
    }

    public void setHouses(House[] houses) {
        Houses = houses;
    }

    public String getMemberFrom() {
        return MemberFrom;
    }

    public void setMemberFrom(String memberFrom) {
        MemberFrom = memberFrom;
    }

    public String getLocationText() {
        return LocationText;
    }

    public void setLocationText(String locationText) {
        LocationText = locationText;
    }

    public String getSummary() {
        return Summary;
    }

    public void setSummary(String summary) {
        Summary = summary;
    }

    public HouseReview[] getReviews() {
        return reviews;
    }

    public void setReviews(HouseReview[] reviews) {
        this.reviews = reviews;
    }
}
