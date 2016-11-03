package xyz.narengi.android.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.AroundLocation;
import xyz.narengi.android.common.dto.AroundPlaceAttraction;
import xyz.narengi.android.common.dto.AroundPlaceCity;
import xyz.narengi.android.common.dto.AroundPlaceHouse;

/**
 * @author Siavash Mahmoudpour
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private List<AroundLocation> objects;
    private Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ViewPager viewPager;
        public ViewHolder(View view) {
            super(view);
//            this.viewPager = viewPager;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerAdapter(List<AroundLocation> objects, Context context) {
        this.objects = objects;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.explore_list_item, parent, false);

        ViewPager viewPager = (ViewPager)itemView.findViewById(R.id.viewpager);

        Display display= ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//        int width = display.getWidth();
        int height = display.getHeight();

        viewPager.getLayoutParams().height = (height * 3)/ 7;

        ViewHolder holder = new ViewHolder(itemView);
        holder.viewPager = viewPager;
        return holder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
//        holder.mTextView.setText(mDataset[position]);

        AroundLocation aroundLocation = objects.get(position);

        String[] imageUrls = null;
        String title = "";
        String summary = "";

        if (aroundLocation.getData() != null) {
            if (aroundLocation.getData() instanceof AroundPlaceCity) {
                AroundPlaceCity city = (AroundPlaceCity)aroundLocation.getData();
                imageUrls = city.getImages();
                title = city.getName();
                summary = city.getHouseCountText();
            } else if (aroundLocation.getData() instanceof AroundPlaceAttraction) {
                AroundPlaceAttraction attraction = (AroundPlaceAttraction)aroundLocation.getData();
                imageUrls = attraction.getImages();
                title = attraction.getName();
                summary = attraction.getAroundHousesText();
            } else if (aroundLocation.getData() instanceof AroundPlaceHouse) {
                AroundPlaceHouse house = (AroundPlaceHouse)aroundLocation.getData();
                AroundPlaceHouse.Picture[] pictures = house.getPictures();
                imageUrls = new String[pictures.length];
                for (int i = 0; i < pictures.length; i++)
                    imageUrls[i] = pictures[i].getUrl();
                title = house.getName();
                summary = house.getSummary();
            }
        }

        holder.viewPager.setId(1000+position);
        ViewPagerAdapter adapter = new ViewPagerAdapter(context, aroundLocation, title, summary, imageUrls);
        holder.viewPager.setAdapter(adapter);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return objects.size();
    }

    public class ViewPagerAdapter extends PagerAdapter {
        // Declare Variables
        private Context context;
        private AroundLocation aroundLocation;
        private String title;
        private String summary;
        private String[] imageUrls;
        private LayoutInflater inflater;

        public ViewPagerAdapter(Context context, AroundLocation aroundLocation, String title, String summary, String[] imageUrls) {
            this.context = context;
            this.aroundLocation = aroundLocation;
            this.title = title;
            this.summary = summary;
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
            return view == ((RelativeLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            ImageView imageView;
            TextView titleTextView;
            TextView summaryTextView;
            Bitmap imageBitmap;

            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.explore_viewpager_item, container,
                    false);

            titleTextView = (TextView) itemView.findViewById(R.id.explore_viewpager_item_title);
            summaryTextView = (TextView) itemView.findViewById(R.id.explore_viewpager_item_summary);

//            titleTextView.setTextSize(28 * context.getResources().getDisplayMetrics().density);
//            summaryTextView.setTextSize(22 * context.getResources().getDisplayMetrics().density);

            titleTextView.setText(title);
            summaryTextView.setText(summary);

            imageView = (ImageView) itemView.findViewById(R.id.explore_viewpager_item_image);

//            Picasso picasso = Picasso.with(context);
//            picasso.setIndicatorsEnabled(true);
            Picasso.with(context).load(imageUrls[position]).into(imageView);

//            ImageDownloaderAsyncTask imageDownloaderAsyncTask = new ImageDownloaderAsyncTask(imageUrls[position]);
//            AsyncTask task = imageDownloaderAsyncTask.execute();
//            try {
//                imageBitmap = (Bitmap)task.get();
//                imageView.setImageBitmap(imageBitmap);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            }
            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // Remove viewpager_item.xml from ViewPager
            container.removeView((RelativeLayout) object);

        }
    }
}
