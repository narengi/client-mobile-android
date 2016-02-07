package xyz.narengi.android.common.dto;

import org.parceler.Parcel;

import java.io.Serializable;
import java.util.List;

/**
 * @author Siavash Mahmoudpour
 */
@Parcel
public class Host implements Serializable {

    private String DisplayName;
    private String HostURL;
    private String ImageUrl;


    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }

    public String getHostURL() {
        return HostURL;
    }

    public void setHostURL(String hostURL) {
        HostURL = hostURL;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

}
