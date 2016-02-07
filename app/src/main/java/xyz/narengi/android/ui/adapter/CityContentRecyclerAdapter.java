package xyz.narengi.android.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.AroundPlaceHouse;
import xyz.narengi.android.common.dto.City;
import xyz.narengi.android.service.ImageDownloaderAsyncTask;

/**
 * @author Siavash Mahmoudpour
 */
public class CityContentRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private City city;
    private Context context;

    public CityContentRecyclerAdapter(Context context, City city) {
        this.context = context;
        this.city = city;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case 0:
                View view = inflater.inflate(R.layout.city_attractions, parent, false);
                viewHolder = new AttractionsViewHolder(view);
                break;
            case 1:
                view = inflater.inflate(R.layout.city_houses_caption, parent, false);
                viewHolder = new HousesCaptionViewHolder(view);
                break;
            default:
                view = inflater.inflate(R.layout.city_houses_item, parent, false);
                viewHolder = new HouseViewHolder(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case 0:
                AttractionsViewHolder attractionsViewHolder = (AttractionsViewHolder) viewHolder;
                setupAttractionsGrid(attractionsViewHolder);
                break;
            case 1:
                HousesCaptionViewHolder housesCaptionViewHolder = (HousesCaptionViewHolder) viewHolder;
                break;
            default:
                HouseViewHolder specsViewHolder = (HouseViewHolder) viewHolder;
                setupHouse(specsViewHolder, position);
                break;
        }

    }

    @Override
    public int getItemCount() {

        int itemCount = 1;
        if (city == null)
            return 0;
        if (city.getAttraction() != null && city.getAttraction().length > 0)
            itemCount++;
        if (city.getHouses() != null && city.getHouses().length > 0)
            itemCount += city.getHouses().length;

        return itemCount;
    }

    @Override
    public int getItemViewType(int position) {

        if (city.getAttraction() != null && city.getAttraction().length > 0) {
            switch (position) {
                case 0:
                    return 0;
                case 1:
                    return 1;
                default:
                    return 2;
            }
        } else {
            switch (position) {
                case 0:
                    return 1;
                default:
                    return 2;
            }
        }
    }

    private void setupAttractionsGrid(AttractionsViewHolder viewHolder) {
//        size = size * 3;

        int size = city.getAttraction().length;
        viewHolder.attractionsGridView.setNumColumns(size);

        // Calculated single Item Layout Width for each grid element ....
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
//
        int width = (int)dpWidth / 2;
        int height = width * 38 / 62;
//        int width = 120;

        DisplayMetrics dm = new DisplayMetrics();
        ((ActionBarActivity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;

        int totalWidth = (int) (width * size * density);
        int singleItemWidth = (int) (width * density);


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                totalWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        viewHolder.attractionsGridView.setLayoutParams(params);
        viewHolder.attractionsGridView.setColumnWidth(singleItemWidth);
        viewHolder.attractionsGridView.setHorizontalSpacing(2);
        viewHolder.attractionsGridView.setStretchMode(GridView.STRETCH_SPACING);
        viewHolder.attractionsGridView.setNumColumns(size);

        AttractionsGridAdapter gridAdapter = new AttractionsGridAdapter(context, Arrays.asList(city.getAttraction()));
        viewHolder.attractionsGridView.setAdapter(gridAdapter);
    }

    private void setupHouse(HouseViewHolder viewHolder, int position) {

        int index;
        if (city.getAttraction() != null && city.getAttraction().length > 0)
            index = position - 2;
        else
            index = position - 1;

        AroundPlaceHouse house = city.getHouses()[index];

        if (house.getImages() != null && house.getImages().length > 0) {
            Display display= ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            int width = display.getWidth();
            int imageWidth = width * 38 / 62;
            int imageHeight = (imageWidth / 2);
            Picasso.with(context).load(house.getImages()[0]).resize(imageWidth, imageHeight).into(viewHolder.houseImageView);
        }
        viewHolder.housePriceTextView.setText(house.getCost());
        if (house.getHost() != null && house.getHost().getImageUrl() != null) {
            try {
//                Bitmap hostImageBitmap = Picasso.with(context).load(house.getHost().getImageUrl()).get();
                ImageDownloaderAsyncTask imageDownloaderAsyncTask = new ImageDownloaderAsyncTask(context, house.getHost().getImageUrl());
//                Bitmap hostImageBitmap = Picasso.with(context).load(house.getHost().getImageUrl()).get();
                AsyncTask asyncTask = imageDownloaderAsyncTask.execute();

                Bitmap hostImageBitmap = (Bitmap)asyncTask.get();
                if (hostImageBitmap != null) {

                    Bitmap circleBitmap = Bitmap.createBitmap(hostImageBitmap.getWidth(), hostImageBitmap.getHeight(), Bitmap.Config.ARGB_8888);

                    BitmapShader shader = new BitmapShader (hostImageBitmap,  Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                    Paint paint = new Paint();
                    paint.setShader(shader);
                    paint.setAntiAlias(true);
                    Canvas c = new Canvas(circleBitmap);
                    c.drawCircle(hostImageBitmap.getWidth() / 2, hostImageBitmap.getHeight() / 2, hostImageBitmap.getWidth() / 2, paint);

                    viewHolder.hostFab.setImageBitmap(hostImageBitmap);
                }


            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        viewHolder.houseTitleTextView.setText(house.getName());
        viewHolder.houseFeatureSummary.setText(house.getFeatureSummray());
    }

    public class AttractionsViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public HorizontalScrollView horizontalScrollView;
        public GridView attractionsGridView;

        public AttractionsViewHolder(View view) {
            super(view);

            horizontalScrollView = (HorizontalScrollView)view.findViewById(R.id.city_attractionsHorizontalScrollView);
            attractionsGridView = (GridView)view.findViewById(R.id.city_attractionsGridView);
        }
    }

    public class HousesCaptionViewHolder extends RecyclerView.ViewHolder {
        public TextView housesCaptionTextView;

        public HousesCaptionViewHolder(View view) {
            super(view);

            housesCaptionTextView = (TextView)view.findViewById(R.id.city_housesCaption);
        }
    }

    public class HouseViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView houseImageView;
        public TextView housePriceTextView;
        public FloatingActionButton hostFab;
        public TextView houseTitleTextView;
        public TextView houseFeatureSummary;
        public RatingBar houseRatingBar;

        public HouseViewHolder(View view) {
            super(view);

            houseImageView = (ImageView)view.findViewById(R.id.houses_item_image);
            housePriceTextView = (TextView)view.findViewById(R.id.houses_item_price);
            hostFab= (FloatingActionButton)view.findViewById(R.id.houses_item_hostFab);
            houseTitleTextView = (TextView)view.findViewById(R.id.houses_item_house_title);
            houseFeatureSummary = (TextView)view.findViewById(R.id.houses_item_house_featureSummary);
            houseRatingBar = (RatingBar)view.findViewById(R.id.houses_item_house_ratingBar);

            Drawable drawable = houseRatingBar.getProgressDrawable();
            drawable.setColorFilter(context.getResources().getColor(R.color.rating_bar_yelloh), PorterDuff.Mode.SRC_ATOP);
        }
    }
}
