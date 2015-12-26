package xyz.narengi.android.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.AroundPlaceHouse;

/**
 * @author Siavash Mahmoudpour
 */
public class CityHousesRecyclerAdapter  extends RecyclerView.Adapter<CityHousesRecyclerAdapter.ViewHolder> {
    private List<AroundPlaceHouse> objects;
    private Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView houseImageView;
//        public TextView housePriceTextView;
        public FloatingActionButton hostFab;
        public TextView houseTitleTextView;
        public TextView houseFeatureSummary;
        public RatingBar houseRatingBar;

        public ViewHolder(View view) {
            super(view);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CityHousesRecyclerAdapter(List<AroundPlaceHouse> objects, Context context) {
        this.objects = objects;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CityHousesRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.city_houses_item, parent, false);

        ImageView houseImageView = (ImageView)itemView.findViewById(R.id.houses_item_image);
//        TextView housePriceTextView = (TextView)itemView.findViewById(R.id.houses_item_price);
        FloatingActionButton houseHostFab= (FloatingActionButton)itemView.findViewById(R.id.houses_item_hostFab);
        TextView houseTitleTextView = (TextView)itemView.findViewById(R.id.houses_item_house_title);
        TextView houseFeatureSummaryTextView = (TextView)itemView.findViewById(R.id.houses_item_house_featureSummary);
        RatingBar houseRatingBar = (RatingBar)itemView.findViewById(R.id.houses_item_house_ratingBar);


        ViewHolder holder = new ViewHolder(itemView);
        holder.houseImageView = houseImageView;
//        holder.housePriceTextView = housePriceTextView;
        holder.hostFab = houseHostFab;
        holder.houseTitleTextView = houseTitleTextView;
        holder.houseFeatureSummary = houseFeatureSummaryTextView;
        holder.houseRatingBar = houseRatingBar;
        Drawable drawable = holder.houseRatingBar.getProgressDrawable();
        drawable.setColorFilter(Color.parseColor("#FFFDEC00"), PorterDuff.Mode.SRC_ATOP);

        return holder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
//        holder.mTextView.setText(mDataset[position]);

        AroundPlaceHouse house = objects.get(position);

        if (house.getImages() != null && house.getImages().length > 0)
            Picasso.with(context).load(house.getImages()[0]).into(holder.houseImageView);
//        holder.housePriceTextView.setText(house.getCost());
//        if (house.getHost() != null && house.getHost().getImageUrl() != null) {
//            try {
//                Bitmap hostImageBitmap = Picasso.with(context).load(house.getHost().getImageUrl()).get();
//                if (hostImageBitmap != null)
//                    holder.hostFab.setImageBitmap(hostImageBitmap);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        holder.houseTitleTextView.setText(house.getName());
        holder.houseFeatureSummary.setText(house.getFeatureSummray());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return objects.size();
    }
}
