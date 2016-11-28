package xyz.narengi.android.ui;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * @author Siavash Mahmoudpour
 */
public class NarengiApplication extends Application {

    private static NarengiApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        JodaTimeAndroid.init(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static NarengiApplication getInstance() {
        return instance;
    }
}
