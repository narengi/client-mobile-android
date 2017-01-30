package xyz.narengi.android.armin.view.adapters;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import xyz.narengi.android.R;

/**
 * Created by arminghm on 1/31/17.
 */

public class ImageAdapter extends PagerAdapter {
    private ArrayList<String> images;

    public ImageAdapter(ArrayList<String> images) {
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView img = new ImageView(container.getContext());
        ViewGroup.LayoutParams params = new ViewGroup
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        img.setLayoutParams(params);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Picasso.with(container.getContext())
                .load("https://api.narengi.xyz/v1" + images.get(position))
                .error(R.mipmap.ic_launcher)
                .placeholder(R.drawable.temp_placeholder_drawable)
                .into(img);

        container.addView(img);
        return img;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
