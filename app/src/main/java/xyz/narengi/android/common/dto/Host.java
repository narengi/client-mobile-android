package xyz.narengi.android.common.dto;

import org.parceler.Parcel;

import java.util.List;

/**
 * @author Siavash Mahmoudpour
 */
@Parcel
public class Host {

//    private String[] Houses;
    private String Name;
    private String ImageUrl;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }
}
