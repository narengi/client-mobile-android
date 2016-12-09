package xyz.narengi.android.common.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sepehr Behroozi on 11/20/16.
 */

public class AroundLocationDataHouse extends AroundLocationsData {

    public static final String ID_JSON_KEY = "id";
    public static final String OWNER_ID_JSON_KEY = "ownerId";
    public static final String NAME_JSON_KEY = "name";
    public static final String PRICE_KEY = "price";
    public static final String PICTURES_JSON_KEY = "pictures";
    public static final String CITY_JSON_KEY = "city";
    public static final String LOCATION_JSON_KEY = "location";
    public static final String PROVINCE_JSON_KEY = "province";
    public static final String SUMMARY_JSON_KEY = "summary";
    public static final String DETAIL_URL_JSON_KEY = "detailUrl";
    public static final String FEATURES_JSON_KEY = "features";

    private String id;
    private String ownerId;
    private String name;
    private String[] pictures;
    private String city;
    private String province;
    private String summary;
    private String price;
    private String detailUrl;
    private String[] features;

    public AroundLocationDataHouse(JSONObject object) {
        super(object);
    }


    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getPictures() {
        return pictures;
    }

    public void setPictures(String[] pictures) {
        this.pictures = pictures;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    public String[] getFeatures() {
        return features;
    }

    public void setFeatures(String[] features) {
        this.features = features;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void parseJson(JSONObject object) {
        ownerId = name = city = province = summary = detailUrl = id = "";
        pictures = features = new String[0];
        if (object == null)
            return;
        try {
            id = object.isNull(ID_JSON_KEY) ? "" : object.getString(ID_JSON_KEY);
            ownerId = object.isNull(OWNER_ID_JSON_KEY) ? "" : object.getString(OWNER_ID_JSON_KEY);
            name = object.isNull(NAME_JSON_KEY) ? "" : object.getString(NAME_JSON_KEY);
            price = object.isNull(PRICE_KEY) ? "" : object.getString(PRICE_KEY);
            summary = object.isNull(SUMMARY_JSON_KEY) ? "" : object.getString(SUMMARY_JSON_KEY);
            detailUrl = object.isNull(DETAIL_URL_JSON_KEY) ? "" : object.getString(DETAIL_URL_JSON_KEY);
            if (object.has(LOCATION_JSON_KEY)) {
                city = object.getJSONObject(LOCATION_JSON_KEY).isNull(CITY_JSON_KEY) ? "" : object.getJSONObject(LOCATION_JSON_KEY).getString(CITY_JSON_KEY);
                province = object.getJSONObject(LOCATION_JSON_KEY).isNull(PROVINCE_JSON_KEY) ? "" : object.getJSONObject(LOCATION_JSON_KEY).getString(PROVINCE_JSON_KEY);
            } else {
                city = province = "";
            }
            if (object.has(PICTURES_JSON_KEY)) {
                JSONArray picturesArray = object.getJSONArray(PICTURES_JSON_KEY);
                if (picturesArray != null) {
                    pictures = new String[picturesArray.length()];
                    for (int i = 0; i < picturesArray.length(); i++)
                        pictures[i] = picturesArray.getString(i);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
