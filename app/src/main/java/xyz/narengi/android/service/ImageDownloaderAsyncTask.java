package xyz.narengi.android.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import xyz.narengi.android.content.RoundedTransformation;

/**
 * @author Siavash Mahmoudpour
 */
public class ImageDownloaderAsyncTask extends AsyncTask {

    private Context context;
    private String imageUrl;
    private int width, height;
    private String authorization;

    public ImageDownloaderAsyncTask(Context context, String imageUrl, int width, int height) {
        this.context = context;
        this.imageUrl = imageUrl;
        this.width = width;
        this.height = height;
    }

    public ImageDownloaderAsyncTask(Context context, String authorization, String imageUrl) {
        this.context = context;
        this.authorization = authorization;
        this.imageUrl = imageUrl;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        return getImageBitmap(imageUrl);
    }

    private Bitmap getImageBitmap(String url) {

        Picasso picasso;
        try {

            final float scale = context.getResources().getDisplayMetrics().density;
            int pixels = (int) (64 * scale);

            if (authorization != null && authorization.length() > 0) {

                pixels = (int) (96 * scale);
                OkHttpClient picassoClient = new OkHttpClient();

                picassoClient.networkInterceptors().add(new Interceptor() {

                    @Override
                    public com.squareup.okhttp.Response intercept(Chain chain) throws IOException {
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("access-token", authorization)
                                .build();
                        return chain.proceed(newRequest);
                    }
                });

                picasso = new Picasso.Builder(context).downloader(new OkHttpDownloader(picassoClient)).build();
            } else {
                picasso = Picasso.with(context);
            }

//            if (width > 0 && height > 0) {
            if (pixels > 0) {
                return picasso.load(imageUrl).transform(new RoundedTransformation(pixels / 2, 2)).resize(pixels, pixels).centerCrop().get();
            } else {
                return picasso.load(imageUrl).transform(new RoundedTransformation(50, 2)).resize(100, 100).centerCrop().get();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
//        try {
//            URL aURL = new URL(url);
//            URLConnection conn = aURL.openConnection();
//            conn.connect();
//            InputStream is = conn.getInputStream();
//            BufferedInputStream bis = new BufferedInputStream(is);
//            bm = BitmapFactory.decodeStream(bis);
//            bis.close();
//            is.close();
//        } catch (IOException e) {
//            Log.e("ImageDownloaderTask", "Error getting bitmap", e);
//        }
        return null;
    }
}
