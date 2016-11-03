package xyz.narengi.android.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
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

import java.util.List;
import java.util.concurrent.ExecutionException;

import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.AroundPlaceHouse;
import xyz.narengi.android.service.ImageDownloaderAsyncTask;

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
        public TextView housePriceTextView;
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
        TextView housePriceTextView = (TextView)itemView.findViewById(R.id.houses_item_price);
        FloatingActionButton houseHostFab= (FloatingActionButton)itemView.findViewById(R.id.houses_item_hostFab);
        TextView houseTitleTextView = (TextView)itemView.findViewById(R.id.houses_item_house_title);
        TextView houseFeatureSummaryTextView = (TextView)itemView.findViewById(R.id.houses_item_house_featureSummary);
        RatingBar houseRatingBar = (RatingBar)itemView.findViewById(R.id.houses_item_house_ratingBar);


        ViewHolder holder = new ViewHolder(itemView);
        holder.houseImageView = houseImageView;
        holder.housePriceTextView = housePriceTextView;
        holder.hostFab = houseHostFab;
        holder.houseTitleTextView = houseTitleTextView;
        holder.houseFeatureSummary = houseFeatureSummaryTextView;
        holder.houseRatingBar = houseRatingBar;
        Drawable drawable = holder.houseRatingBar.getProgressDrawable();
        drawable.setColorFilter(context.getResources().getColor(R.color.rating_bar_yelloh), PorterDuff.Mode.SRC_ATOP);

        return holder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
//        viewHolder.mTextView.setText(mDataset[position]);

        AroundPlaceHouse house = objects.get(position);

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
//                Bitmap hostImageBitmap = Picasso.with(context).load(house.getHost().getImageUrl()).get();
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

//                    viewHolder.hostFab.setBackgroundDrawable(new BitmapDrawable(getRoundedRectBitmap(circleBitmap)));
//                    viewHolder.hostFab.setImageBitmap(circleBitmap);
                    viewHolder.hostFab.setImageBitmap(hostImageBitmap);
                }
//                    viewHolder.hostFab.setBackgroundDrawable(new BitmapDrawable(getRoundedRectBitmap(hostImageBitmap)));
//                    viewHolder.hostFab.setBackgroundDrawable(new BitmapDrawable(hostImageBitmap));
//                    viewHolder.hostFab.setImageBitmap(hostImageBitmap);


            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        viewHolder.houseTitleTextView.setText(house.getName());
        viewHolder.houseFeatureSummary.setText(house.getFeatureSummray());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return objects.size();
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
