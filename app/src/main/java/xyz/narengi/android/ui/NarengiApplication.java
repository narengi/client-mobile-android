package xyz.narengi.android.ui;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import net.danlew.android.joda.JodaTimeAndroid;

import java.io.IOException;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import xyz.narengi.android.common.dto.AccountProfile;

/**
 * @author Siavash Mahmoudpour
 */
public class NarengiApplication extends Application {
    private static NarengiApplication instance;
    private String authorizationJsonHeader = "";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        JodaTimeAndroid.init(this);
        Fabric.with(this, new Crashlytics());
        setPicassoDownloader();

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        MultiDex.install(this);
    }

    public void setPicassoDownloader() {
        try {
            authorizationJsonHeader = AccountProfile.getLoggedInAccountProfile(this).getToken().getAuthString();
        } catch (Exception e) {
        }
        OkHttpClient picassoClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Interceptor.Chain chain) throws IOException {
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("Authorization", "Bearer " + authorizationJsonHeader)
                                .build();
                        return chain.proceed(newRequest);
                    }
                }).build();

        Picasso.setSingletonInstance(
                new Picasso
                        .Builder(this)
                        .downloader(new OkHttp3Downloader(picassoClient))
                        .build());
    }

    public static NarengiApplication getInstance() {
        return instance;
    }
}
