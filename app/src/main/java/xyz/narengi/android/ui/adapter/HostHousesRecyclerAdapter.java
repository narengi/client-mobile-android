package xyz.narengi.android.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.House;

/**
 * @author Siavash Mahmoudpour
 */
public class HostHousesRecyclerAdapter extends RecyclerView.Adapter<HostHousesRecyclerAdapter.HousesViewHolder> {
    private House[] objects;
    private Context context;

    public class HousesViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView houseImageView;
        public TextView housePriceTextView;
        public TextView houseTitleTextView;
        public TextView houseFeatureSummary;
        public RatingBar houseRatingBar;

        public HousesViewHolder(View view) {
            super(view);

            houseImageView = (ImageView)itemView.findViewById(R.id.host_houses_item_image);
            housePriceTextView = (TextView)itemView.findViewById(R.id.host_houses_item_price);
            houseTitleTextView = (TextView)itemView.findViewById(R.id.host_houses_item_title);
            houseFeatureSummary = (TextView)itemView.findViewById(R.id.host_houses_item_featureSummary);
            houseRatingBar = (RatingBar)itemView.findViewById(R.id.host_houses_item_ratingBar);

            Drawable drawable = houseRatingBar.getProgressDrawable();
            drawable.setColorFilter(context.getResources().getColor(R.color.rating_bar_yelloh), PorterDuff.Mode.SRC_ATOP);
        }
    }

    // Provide a reference to the views for each data item
//    public class HousesViewHolder extends RecyclerView.ViewHolder {
//        // each data item is just a string in this case
//        public ImageView houseImageView;
//        public TextView housePriceTextView;
//        public FloatingActionButton hostFab;
//        public TextView houseTitleTextView;
//        public TextView houseFeatureSummary;
//        public RatingBar houseRatingBar;
//
//        public HousesViewHolder(View view) {
//            super(view);
//
//            houseImageView = (ImageView)itemView.findViewById(R.id.houses_item_image);
//            housePriceTextView = (TextView)itemView.findViewById(R.id.houses_item_price);
//            hostFab= (FloatingActionButton)itemView.findViewById(R.id.houses_item_hostFab);
//            houseTitleTextView = (TextView)itemView.findViewById(R.id.houses_item_house_title);
//            houseFeatureSummary = (TextView)itemView.findViewById(R.id.houses_item_house_featureSummary);
//            houseRatingBar = (RatingBar)itemView.findViewById(R.id.houses_item_house_ratingBar);
//
//            Drawable drawable = houseRatingBar.getProgressDrawable();
//            drawable.setColorFilter(context.getResources().getColor(R.color.rating_bar_yelloh), PorterDuff.Mode.SRC_ATOP);
//        }
//    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public HostHousesRecyclerAdapter(Context context, House[] objects) {
        this.context = context;
        this.objects = objects;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public HostHousesRecyclerAdapter.HousesViewHolder onCreateViewHolder(ViewGroup parent,
                                                                   int viewType) {

        HousesViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.host_houses_item, parent, false);
        viewHolder = new HousesViewHolder(view);

        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(HousesViewHolder viewHolder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
//        viewHolder.mTextView.setText(mDataset[position]);

        House house = objects[position];

        if (house.getImages() != null && house.getImages().length > 0) {
            Display display= ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            int width = display.getWidth();
            int imageWidth = width * 38 / 62;
            int imageHeight = (imageWidth / 2);
            Picasso.with(context).load(house.getImages()[0]).resize(imageWidth, imageHeight).into(viewHolder.houseImageView);
        }
        viewHolder.housePriceTextView.setText(house.getCost());

        viewHolder.houseTitleTextView.setText(house.getName());
        Drawable drawable = context.getResources().getDrawable(android.R.drawable.ic_menu_compass);
        drawable.setColorFilter(context.getResources().getColor(android.R.color.holo_orange_dark), PorterDuff.Mode.SRC_ATOP);
        viewHolder.houseTitleTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        viewHolder.houseFeatureSummary.setText(house.getFeatureSummary());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (objects != null && objects.length > 0)
            return objects.length;
        return 0;
    }


    public Bitmap getRoundedRectBitmap(Bitmap bitmap) {
        Bitmap result = null;
        try {
            result = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);

            int color = 0xff424242;
            Paint paint = new Paint();
            Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawCircle(50, 50, 50, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);

        } catch (NullPointerException e) {
        } catch (OutOfMemoryError o) {
        }
        return result;
    }
}
