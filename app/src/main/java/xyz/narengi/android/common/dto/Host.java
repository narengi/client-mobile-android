package xyz.narengi.android.common.dto;

import org.parceler.Parcel;

import java.util.List;

/**
 * @author Siavash Mahmoudpour
 */
@Parcel
public class Host {

    private String ID;
    private String DisplayName;
    private String CellNumber;
    private String Email;
    private String ImageUrl;
    private String[] Houses;


    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }

    public String getCellNumber() {
        return CellNumber;
    }

    public void setCellNumber(String cellNumber) {
        CellNumber = cellNumber;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String[] getHouses() {
        return Houses;
    }

    public void setHouses(String[] houses) {
        Houses = houses;
    }
}
