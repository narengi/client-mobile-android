package xyz.narengi.android.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Sepebr Behroozi on 9/22/2016 AD.
 */

public class SharedPref {
    private static final String SHARED_PREFERENCES_NAME = "ng_pref";
    private static SharedPref instance;
    private Context context;
    private SharedPreferences preferences;

    private SharedPref(Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPref getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPref(context);
        }
        return instance;
    }

    public void save(String key, String value) {
        SharedPreferences.Editor edit = this.preferences.edit();
        edit.putString(key, value);
        edit.apply();
    }

    public String getString(String key, String defaultValue) {
        return this.preferences.getString(key, defaultValue);
    }

    public String getString(String key) {
        return getString(key, null);
    }

    public void getSerial(String key) {
    }
}
