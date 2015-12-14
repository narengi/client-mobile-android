package xyz.narengi.android.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Siavash Mahmoudpour
 */
public class ImageDownloaderAsyncTask extends AsyncTask {

    private String imageUrl;

    public ImageDownloaderAsyncTask(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        return getImageBitmap(imageUrl);
    }

    private Bitmap getImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e("ImageDownloaderTask", "Error getting bitmap", e);
        }
        return bm;
    }
}
