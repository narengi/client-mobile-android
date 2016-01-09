package xyz.narengi.android.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.AroundPlaceAttraction;

/**
 * @author Siavash Mahmoudpour
 */
public class AttractionsGridAdapter extends BaseAdapter {

    private Context mContext;
    private List<AroundPlaceAttraction> mAttractions;

    public AttractionsGridAdapter(Context c, List<AroundPlaceAttraction> attractions) {
        mContext = c;
        mAttractions = attractions;
    }

    public int getCount() {
        if (mAttractions != null)
            return mAttractions.size();
        return 0;
    }

    public Object getItem(int position) {
        if (mAttractions != null && mAttractions.size() > position)
            return mAttractions.get(position);
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.attractions_grid_item, parent,
                false);

        ImageView imageView;
        TextView titleTextView;

//        if (convertView == null) {
        if (itemView != null) {
            // if it's not recycled, initialize some attributes
//            imageView = new ImageView(mContext);
            imageView = (ImageView)itemView.findViewById(R.id.attractions_grid_item_image);
//            DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
//            float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
//            float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
//
            Display display= ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//            int screenHeight = display.getHeight();

//            int width = (int)dpWidth / 2;
//            int height = width * 38 / 62;

            int width = (int)display.getWidth() / 2;
            int height = width * 38 / 62;

//            imageView.setLayoutParams(new GridView.LayoutParams(width, height));
            imageView.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setPadding(8, 8, 8, 8);

            titleTextView = (TextView)itemView.findViewById(R.id.attractions_grid_item_title);
//            titleTextView.setTextSize(18 * mContext.getResources().getDisplayMetrics().density);

            if (mAttractions != null && mAttractions.size() > position) {
                AroundPlaceAttraction attraction = mAttractions.get(position);
                titleTextView.setText(attraction.getName());
                if (attraction.getImages() != null && attraction.getImages().length > 0)
                    Picasso.with(mContext).load(attraction.getImages()[0]).transform(new RoundedTransformation(15, 2)).into(imageView);
            }


        } /*else {
            imageView = (ImageView) convertView;
        }*/

//        imageView.setImageResource(mThumbIds[position]);
//        return imageView;
        return itemView;
    }

    // references to our images
    private Integer[] mThumbIds = {
            R.drawable.sample_4, R.drawable.sample_4,
            R.drawable.sample_4, R.drawable.sample_4,
            R.drawable.sample_4, R.drawable.sample_4,
            R.drawable.sample_4
    };

    private class RoundedTransformation implements com.squareup.picasso.Transformation {
        private final int radius;
        private final int margin;  // dp

        // radius is corner radii in dp
        // margin is the board in dp
        public RoundedTransformation(final int radius, final int margin) {
            this.radius = radius;
            this.margin = margin;
        }

        @Override
        public Bitmap transform(final Bitmap source) {
            final Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

            Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            canvas.drawRoundRect(new RectF(margin, margin, source.getWidth() - margin, source.getHeight() - margin), radius, radius, paint);

            if (source != output) {
                source.recycle();
            }

            return output;
        }

        @Override
        public String key() {
            return "rounded";
        }
    }
}
