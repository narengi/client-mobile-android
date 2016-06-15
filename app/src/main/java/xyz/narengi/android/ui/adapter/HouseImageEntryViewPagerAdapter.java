package xyz.narengi.android.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import java.util.List;

import xyz.narengi.android.R;

/**
 * @author Siavash Mahmoudpour
 */
public class HouseImageEntryViewPagerAdapter extends PagerAdapter {

    private Context context;
    private List<Uri> imageUris;

    public HouseImageEntryViewPagerAdapter(Context context, List<Uri> imageUris) {
        this.context = context;
        this.imageUris = imageUris;
    }

    @Override
    public int getCount() {
        if (imageUris != null)
            return imageUris.size();
        else
            return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        ImageView imageView;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.image_viewpager_item, container,
                false);

        imageView = (ImageView) itemView.findViewById(R.id.image_viewpager_item_image);

        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageUris.get(position).getPath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        if (targetW > 0 && targetH > 0) {
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
            bmOptions.inSampleSize = scaleFactor;

        }
        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imageUris.get(position).getPath(), bmOptions);
        imageView.setImageBitmap(bitmap);
        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}
