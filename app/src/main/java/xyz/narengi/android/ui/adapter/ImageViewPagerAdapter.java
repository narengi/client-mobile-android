package xyz.narengi.android.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import xyz.narengi.android.R;

/**
 * @author Siavash Mahmoudpour
 */
public class ImageViewPagerAdapter extends PagerAdapter {
    // Declare Variables
    private Context context;
    private String[] imageUrls;

    public ImageViewPagerAdapter(Context context, String[] imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @Override
    public int getCount() {
        if (imageUrls != null)
            return imageUrls.length;
        else
            return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
//        return view == ((RelativeLayout) object);
        return view == ((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        ImageView imageView;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.image_viewpager_item, container,
                false);

        final LinearLayout progressBarLayout = (LinearLayout)itemView.findViewById(R.id.image_viewpager_item_progressBarLayout);
        final ProgressBar progressBar = (ProgressBar)itemView.findViewById(R.id.image_viewpager_item_progressBar);
        progressBar.setVisibility(View.VISIBLE);
        progressBarLayout.setVisibility(View.VISIBLE);
        imageView = (ImageView) itemView.findViewById(R.id.image_viewpager_item_image);

        Picasso.with(context).load(imageUrls[position]).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
                progressBarLayout.setVisibility(View.GONE);
            }

            @Override
            public void onError() {

            }
        });
        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}