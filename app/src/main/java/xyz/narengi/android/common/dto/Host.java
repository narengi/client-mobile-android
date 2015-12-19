package xyz.narengi.android.common.dto;

import org.parceler.Parcel;

import java.util.List;

/**
 * @author Siavash Mahmoudpour
 */
@Parcel
public class Host {

//    private String[] Houses;
    private String DisplayName;
    private String ImageUrl;

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String name) {
        DisplayName = name;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }
}
