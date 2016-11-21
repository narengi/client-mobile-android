package xyz.narengi.android.common.model;

import org.json.JSONObject;

/**
 * Created by Sepehr Behroozi on 11/20/16.
 */

public abstract class AroundLocationsData {


    public AroundLocationsData(JSONObject object) {
        parseJson(object);
    }

    public abstract void parseJson(JSONObject object);
}
