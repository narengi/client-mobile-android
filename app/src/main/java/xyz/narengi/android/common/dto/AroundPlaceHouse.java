package xyz.narengi.android.common.dto;

import org.parceler.Parcel;

import java.io.Serializable;

/**
 * @author Siavash Mahmoudpour
 */
@Parcel
public class AroundPlaceHouse implements Serializable {

    private String name;
    private Picture[] pictures;
    private String Cost;
    private GeoPoint Position;
    private Host Host;
    private String Rating;
    private String summary;
    private String CityName;
    private String FeatureSummray;
    private String detailUrl;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Picture[] getPictures() {
        return pictures;
    }

    public void setPictures(Picture[] pictures) {
        this.pictures = pictures;
    }

    public String getCost() {
        return Cost;
    }

    public void setCost(String cost) {
        Cost = cost;
    }

    public GeoPoint getPosition() {
        return Position;
    }

    public void setPosition(GeoPoint position) {
        Position = position;
    }

    public xyz.narengi.android.common.dto.Host getHost() {
        return Host;
    }

    public void setHost(xyz.narengi.android.common.dto.Host host) {
        Host = host;
    }

    public String getRating() {
        return Rating;
    }

    public void setRating(String rating) {
        Rating = rating;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCityName() {
        return CityName;
    }

    public void setCityName(String cityName) {
        CityName = cityName;
    }

    public String getFeatureSummray() {
        return FeatureSummray;
    }

    public void setFeatureSummray(String featureSummray) {
        FeatureSummray = featureSummray;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    public class Picture implements Serializable {
        private String url;
        private Styles styles;


        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Styles getStyles() {
            return styles;
        }

        public void setStyles(Styles styles) {
            this.styles = styles;
        }

        class Styles implements Serializable {
            private String original;
            private int w64;
            private int w480;
            private int w1024;

            public String getOriginal() {
                return original;
            }

            public void setOriginal(String original) {
                this.original = original;
            }

            public int getW64() {
                return w64;
            }

            public void setW64(int w64) {
                this.w64 = w64;
            }

            public int getW480() {
                return w480;
            }

            public void setW480(int w480) {
                this.w480 = w480;
            }

            public int getW1024() {
                return w1024;
            }

            public void setW1024(int w1024) {
                this.w1024 = w1024;
            }
        }
    }

}
