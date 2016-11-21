package xyz.narengi.android.common.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sepehr Behroozi on 11/20/16.
 */

public class AroundLocation {

    private Type type;
    private AroundLocationsData data;
    private JSONObject aroundLocationObject;

    public AroundLocation(JSONObject aroundLocationObject) {
        this.aroundLocationObject = aroundLocationObject;

        if (aroundLocationObject != null) {
            try {
                String typeString = aroundLocationObject.isNull("Type") ? "" : aroundLocationObject.getString("Type");
                type = Type.fromString(typeString);
                prepareData();
                Log.d("asd","ads");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public AroundLocationsData getData() {
        return data;
    }

    public void setData(AroundLocationsData data) {
        this.data = data;
    }

    public JSONObject getAroundLocationObject() {
        return aroundLocationObject;
    }

    public void setAroundLocationObject(JSONObject aroundLocationObject) {
        this.aroundLocationObject = aroundLocationObject;
    }

    private void prepareData() {
        if (type == null)
            return;
        switch (type) {
            case HOUSE:
                try {
                    data = new AroundLocationDataHouse(aroundLocationObject.getJSONObject("Data"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case CITY:
                try {
                    data = new AroundLocationDataCity(aroundLocationObject.getJSONObject("Data"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case ATTRACTION:
                try {
                    data = new AroundLocationDataAttraction(aroundLocationObject.getJSONObject("Data"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    public enum Type {
        CITY("City"),
        HOUSE("House"),
        ATTRACTION("Attraction");


        private final String value;

        Type(String value) {
            this.value = value;
        }

        public static Type fromString(String value) {
            for (Type type : values())
                if (type.getValue().equalsIgnoreCase(value))
                    return type;
            return null;
        }

        public String getValue() {
            return value;
        }
    }

}
