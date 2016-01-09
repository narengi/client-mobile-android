package xyz.narengi.android.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

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

    private String imageUrl;
    private Context context;

    public ImageDownloaderAsyncTask(Context context, String imageUrl) {
        this.context = context;
        this.imageUrl = imageUrl;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        return getImageBitmap(imageUrl);
    }

    private Bitmap getImageBitmap(String url) {
//        Bitmap bm = null;
        try {
            return Picasso.with(context).load(imageUrl).transform(new RoundedTransformation(50, 4)).resize(90, 90).centerCrop().get();
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
