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

import java.util.concurrent.ExecutionException;

import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.AroundPlaceHouse;
import xyz.narengi.android.common.dto.Attraction;
import xyz.narengi.android.service.ImageDownloaderAsyncTask;

/**
 * @author Siavash Mahmoudpour
 */
public class AttractionContentRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Attraction attraction;
    private Context context;

    public AttractionContentRecyclerAdapter(Context context, Attraction attraction) {
        this.context = context;
        this.attraction = attraction;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case 0:
                View view = inflater.inflate(R.layout.city_houses_caption, parent, false);
                viewHolder = new HousesCaptionViewHolder(view);
                break;
            default:
                view = inflater.inflate(R.layout.city_houses_item, parent, false);
                viewHolder = new HousesViewHolder(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case 0:
                HousesCaptionViewHolder housesCaptionViewHolder = (HousesCaptionViewHolder) viewHolder;
                break;
            default:
                HousesViewHolder specsViewHolder = (HousesViewHolder) viewHolder;
                setupHouses(specsViewHolder, position - 1);
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (attraction != null && attraction.getHouses() != null && attraction.getHouses().length > 0) {
            return attraction.getHouses().length + 1;
        } else {
            return 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void setupHouses(HousesViewHolder viewHolder, int position) {

        AroundPlaceHouse house = attraction.getHouses()[position];

        if (house.getPictures() != null && house.getPictures().length > 0) {
            Display display= ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            int width = display.getWidth();
            int imageWidth = width * 38 / 62;
            int imageHeight = (imageWidth / 2);
            Picasso.with(context).load(house.getPictures()[0].getUrl()).resize(imageWidth, imageHeight).into(viewHolder.houseImageView);
        }
        viewHolder.housePriceTextView.setText(house.getCost());
        if (house.getHost() != null && house.getHost().getImageUrl() != null) {
            try {
                int width=0 , height=0;
                if (viewHolder.hostFab != null) {
                    if (viewHolder.hostFab.getWidth() > 0 && viewHolder.hostFab.getHeight() > 0) {
                        width = viewHolder.hostFab.getWidth();
                        height = viewHolder.hostFab.getHeight();
                    } else if (viewHolder.hostFab.getLayoutParams() != null) {
                        width = viewHolder.hostFab.getLayoutParams().width;
                        height = viewHolder.hostFab.getLayoutParams().height;
                    }
                }

                ImageDownloaderAsyncTask imageDownloaderAsyncTask = new ImageDownloaderAsyncTask(context, house.getHost().getImageUrl(),
                        width, height);
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

    public class HousesCaptionViewHolder extends RecyclerView.ViewHolder {
        public TextView housesCaptionTextView;

        public HousesCaptionViewHolder(View view) {
            super(view);

            housesCaptionTextView = (TextView)view.findViewById(R.id.city_housesCaption);
        }
    }

    public class HousesViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView houseImageView;
        public TextView housePriceTextView;
        public FloatingActionButton hostFab;
        public TextView houseTitleTextView;
        public TextView houseFeatureSummary;
        public RatingBar houseRatingBar;

        public HousesViewHolder(View view) {
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