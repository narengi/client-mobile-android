package xyz.narengi.android.common.dto;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * @author Siavash Mahmoudpour
 */
public class AccessToken implements Serializable {
    private static final String USERNAME_JSON_KEY = "username";
    private static final String TOKEN_JSON_KEY = "token";
    private static final String TYPE_JSON_KEY = "type";

    private String username;
    private String token;
    private String type;

    public static void removeAccessToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("profile", Context.MODE_PRIVATE);
        preferences.edit().clear().apply();
    }

    public static AccessToken fromJsonObject(JSONObject object) {
        if (object == null)
            return null;
        AccessToken result = new AccessToken();
        try {
            result.token = object.isNull(TOKEN_JSON_KEY) ? "" : object.getString(TOKEN_JSON_KEY);
            result.username = object.isNull(USERNAME_JSON_KEY) ? "" : object.getString(USERNAME_JSON_KEY);
            result.type = object.isNull(TYPE_JSON_KEY) ? "" : object.getString(TYPE_JSON_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @NonNull
    public String getAuthString() {
        return token;
    }

    public JSONObject toJsonObject() {
        JSONObject result = new JSONObject();
        try {
            result.put(USERNAME_JSON_KEY, username == null ? JSONObject.NULL : username);
            result.put(TOKEN_JSON_KEY, token == null ? JSONObject.NULL : token);
            result.put(TYPE_JSON_KEY, type == null ? JSONObject.NULL : type);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }
}
