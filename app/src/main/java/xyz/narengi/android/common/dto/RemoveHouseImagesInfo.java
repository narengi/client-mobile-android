package xyz.narengi.android.common.dto;

import java.io.Serializable;

/**
 * @author Siavash Mahmoudpour
 */
public class RemoveHouseImagesInfo implements Serializable {
    private String[] picture_names;

    public String[] getPicture_names() {
        return picture_names;
    }

    public void setPicture_names(String[] picture_names) {
        this.picture_names = picture_names;
    }
}
