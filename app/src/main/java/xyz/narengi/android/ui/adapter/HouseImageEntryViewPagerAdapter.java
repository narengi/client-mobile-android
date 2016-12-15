package xyz.narengi.android.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
    private List<String> imageUrls;

    public HouseImageEntryViewPagerAdapter(Context context, List<Uri> imageUris, List<String> imageUrls) {
        this.context = context;
        this.imageUris = imageUris;
        this.imageUrls = imageUrls;
    }

    @Override
    public int getCount() {
        int count = 0;
        if (imageUrls != null)
            count = imageUrls.size();
        if (imageUris != null)
            count += imageUris.size();

        return count;
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
        final LinearLayout progressBarLayout = (LinearLayout)itemView.findViewById(R.id.image_viewpager_item_progressBarLayout);
//        final ProgressBar progressBar = (ProgressBar)itemView.findViewById(R.id.image_viewpager_item_progressBar);

        progressBarLayout.setVisibility(View.VISIBLE);
//        progressBar.setVisibility(View.VISIBLE);

        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        int imageUrlSize = 0;
        if (imageUrls != null) {
            imageUrlSize = imageUrls.size();
        }

        int imageUriSize = 0;
        if (imageUris != null) {
            imageUriSize = imageUris.size();
        }

        if (position < imageUrlSize) {
            Picasso.with(context).load(imageUrls.get(position)).into(imageView);//todo
        } else if (position < imageUriSize + imageUrlSize){
            Picasso.with(context).load("file://" + imageUris.get(position - imageUrlSize).getPath()).into(imageView);
        }

//        if (imageInfoArray == null) {
//            if (imageUris != null && imageUris.size() > position) {
//                progressBarLayout.setVisibility(View.VISIBLE);
//                progressBar.setVisibility(View.VISIBLE);
//
//                 Get the dimensions of the bitmap
//                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//                bmOptions.inJustDecodeBounds = true;
//                BitmapFactory.decodeFile(imageUris.get(position).getPath(), bmOptions);
//                int photoW = bmOptions.outWidth;
//                int photoH = bmOptions.outHeight;
//
//                 Determine how much to scale down the image
//                if (targetW > 0 && targetH > 0) {
//                    int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
//                    bmOptions.inSampleSize = scaleFactor;
//
//                }
//                 Decode the image file into a Bitmap sized to fill the View
//                bmOptions.inJustDecodeBounds = false;
//                bmOptions.inPurgeable = true;
//
//                Bitmap bitmap = BitmapFactory.decodeFile(imageUris.get(position).getPath(), bmOptions);
//                imageView.setImageBitmap(bitmap);
//
//                progressBarLayout.setVisibility(View.GONE);
//                progressBar.setVisibility(View.GONE);
//            } else {
//                progressBarLayout.setVisibility(View.GONE);
//                progressBar.setVisibility(View.GONE);
//            }
//        } else if ( imageInfoArray.length > position) {
//
//            progressBarLayout.setVisibility(View.VISIBLE);
//            progressBar.setVisibility(View.VISIBLE);
//
//            Picasso.with(context).load(imageInfoArray[position].getUrl()).into(imageView, new Callback() {
//                @Override
//                public void onSuccess() {
//                    progressBar.setVisibility(View.GONE);
//                    progressBarLayout.setVisibility(View.GONE);
//                }
//
//                @Override
//                public void onError() {
//
//                }
//            });
//        } else if (imageUris != null && imageUris.size() > (position - imageInfoArray.length)) {
//
//            progressBarLayout.setVisibility(View.VISIBLE);
//            progressBar.setVisibility(View.VISIBLE);
//
//             Get the dimensions of the bitmap
//            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//            bmOptions.inJustDecodeBounds = true;
//            BitmapFactory.decodeFile(imageUris.get(position - imageInfoArray.length).getPath(), bmOptions);
//            int photoW = bmOptions.outWidth;
//            int photoH = bmOptions.outHeight;
//
//             Determine how much to scale down the image
//            if (targetW > 0 && targetH > 0) {
//                int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
//                bmOptions.inSampleSize = scaleFactor;
//
//            }
//             Decode the image file into a Bitmap sized to fill the View
//            bmOptions.inJustDecodeBounds = false;
//            bmOptions.inPurgeable = true;
//
//            Bitmap bitmap = BitmapFactory.decodeFile(imageUris.get(position - imageInfoArray.length).getPath(), bmOptions);
//            imageView.setImageBitmap(bitmap);
//
//            progressBarLayout.setVisibility(View.GONE);
//            progressBar.setVisibility(View.GONE);
//
//        } else {
//            progressBarLayout.setVisibility(View.GONE);
//            progressBar.setVisibility(View.GONE);
//        }

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}
