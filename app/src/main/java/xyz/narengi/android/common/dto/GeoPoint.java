package xyz.narengi.android.common.dto;

import org.parceler.Parcel;

import java.io.Serializable;

/**
 * @author Siavash Mahmoudpour
 */
@Parcel
public class GeoPoint implements Serializable {

    private double lat;
    private double lng;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
