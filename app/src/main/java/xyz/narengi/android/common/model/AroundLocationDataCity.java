package xyz.narengi.android.common.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sepehr Behroozi on 11/20/16.
 */

public class AroundLocationDataCity extends AroundLocationsData {

    public static final String ID_JSON_KEY = "id";
    public static final String NAME_JSON_KEY = "name";
    public static final String SUMMARY_JSON_KEY = "summary";
    public static final String DESCRIPTION_JSON_KEY = "name";
    public static final String PICTURES_JSON_KEY = "pictures";

    private String id;
    private String name;
    private String summary;
    private String description;
    private String[] pictures;

    public AroundLocationDataCity(JSONObject object) {
        super(object);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getPictures() {
        return pictures;
    }

    public void setPictures(String[] pictures) {
        this.pictures = pictures;
    }

    @Override
    public void parseJson(JSONObject object) {
        id = name = summary = description = "";
        pictures = new String[0];
        if (object == null)
            return;
        try {
            id = object.isNull(ID_JSON_KEY) ? "" : object.getString(ID_JSON_KEY);
            name = object.isNull(NAME_JSON_KEY) ? "" : object.getString(NAME_JSON_KEY);
            summary = object.isNull(SUMMARY_JSON_KEY) ? "" : object.getString(SUMMARY_JSON_KEY);
            description = object.isNull(DESCRIPTION_JSON_KEY) ? "" : object.getString(DESCRIPTION_JSON_KEY);
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
