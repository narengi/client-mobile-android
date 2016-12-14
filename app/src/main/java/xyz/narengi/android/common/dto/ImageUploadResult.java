package xyz.narengi.android.common.dto;

import java.io.Serializable;

/**
 * @author Siavash Mahmoudpour
 */
public class ImageUploadResult implements Serializable {
    private String[] uids;

    public String[] getUids() {
        return uids;
    }

    public void setUids(String[] uids) {
        this.uids = uids;
    }
}
