package xyz.narengi.android.ui.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.HttpUrl;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.CirclePageIndicator;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.City;
import xyz.narengi.android.content.CityDeserializer;
import xyz.narengi.android.service.RetrofitApiEndpoints;

/**
 * @author Siavash Mahmoudpour
 */
public class CityActivity extends ActionBarActivity {

    TextView titleTextView;
    TextView houseCountTextView;
    TextView summaryTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        setupViewPager();

        if (getIntent() != null && getIntent().getStringExtra("cityUrl") != null) {
            String cityUrl = getIntent().getStringExtra("cityUrl");
            getCity(cityUrl);
        }

        TextView wikiTextView = (TextView)findViewById(R.id.city_viewpager_item_wiki);
        wikiTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_action_wikipedia), null);
    }

    private void setupViewPager() {
        ViewPager viewPager = (ViewPager)findViewById(R.id.city_viewpager);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
//
        viewPager.getLayoutParams().height = (int)(dpHeight/2);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this, null, null, null, null);
        viewPager.setAdapter(adapter);

        CirclePageIndicator pageIndicator = (CirclePageIndicator)findViewById(R.id.city_pageIndicator);
        pageIndicator.setViewPager(viewPager);
    }

    private void getCity(String url) {
        String BASE_URL = "http://149.202.20.233:3500";
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(City.class, new CityDeserializer()).create();

        HttpUrl httpUrl = HttpUrl.parse(url);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);

        Call<City> call = apiEndpoints.getCity(url);
        call.enqueue(new Callback<City>() {
            @Override
            public void onResponse(Response<City> response, Retrofit retrofit) {
//                int statusCode = response.code();
                City city = response.body();
                if (city != null) {
                    ViewPager viewPager = (ViewPager)findViewById(R.id.city_viewpager);

                    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                    float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
                    float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
//
                    viewPager.getLayoutParams().height = (int)(dpHeight/2);

                    ViewPagerAdapter adapter = new ViewPagerAdapter(CityActivity.this, city.getName(), city.getSummary(), city.getHouseCountText(), city.getImages());
                    viewPager.setAdapter(adapter);

                    titleTextView = (TextView) findViewById(R.id.city_viewpager_item_title);
                    houseCountTextView = (TextView) findViewById(R.id.city_viewpager_item_house_count);
                    summaryTextView = (TextView) findViewById(R.id.city_viewpager_item_summary);

                    titleTextView.setText(city.getName());
                    houseCountTextView.setText(city.getHouseCountText());
                    summaryTextView.setText(city.getSummary());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                // Log error here since request failed
                t.printStackTrace();
                Log.d("CityActivity", "getCity onFailure : " + t.getMessage(), t);
            }
        });
    }


    private class ViewPagerAdapter extends PagerAdapter {
        // Declare Variables
        private Context context;
        private String title;
        private String summary;
        private String houseCountText;
        private String[] imageUrls;
        private LayoutInflater inflater;

        public ViewPagerAdapter(Context context, String title, String summary, String houseCountText, String[] imageUrls) {
            this.context = context;
            this.title = title;
            this.summary = summary;
            this.houseCountText = houseCountText;
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
            Bitmap imageBitmap;

            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.city_viewpager_item, container,
                    false);

            imageView = (ImageView) itemView.findViewById(R.id.city_viewpager_item_image);

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