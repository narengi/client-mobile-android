package xyz.narengi.android.ui;

import android.app.Application;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * @author Siavash Mahmoudpour
 */
public class NarengiApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
    }
}
