package xyz.narengi.android.ui;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import net.danlew.android.joda.JodaTimeAndroid;

import java.io.IOException;

import xyz.narengi.android.common.dto.AccountProfile;

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

        final String authorizationJsonHeader = AccountProfile.getLoggedInAccountProfile(this).getToken().getAuthString();
        OkHttpClient picassoClient = new OkHttpClient();
        picassoClient.networkInterceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder()
                        .addHeader("access-token", authorizationJsonHeader)
                        .build();
                return chain.proceed(newRequest);
            }
        });

        Picasso.setSingletonInstance(
                new Picasso
                        .Builder(this)
                        .downloader(new OkHttpDownloader(picassoClient))
                        .build());
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
