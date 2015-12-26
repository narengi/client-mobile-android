package xyz.narengi.android.ui.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.HttpUrl;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.AroundPlaceHouse;
import xyz.narengi.android.common.dto.City;
import xyz.narengi.android.content.CityDeserializer;
import xyz.narengi.android.service.RetrofitApiEndpoints;
import xyz.narengi.android.ui.adapter.AttractionsGridAdapter;
import xyz.narengi.android.ui.adapter.CityHousesRecyclerAdapter;

/**
 * @author Siavash Mahmoudpour
 */
public class CityActivity extends ActionBarActivity {

    TextView titleTextView;
    TextView houseCountTextView;
    TextView summaryTextView;
    TextView wikiTextView;
    GridView gridView;
    HorizontalScrollView attractionsScrollView;

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

//        setupAttractionsGrid(0, new ArrayList<AroundPlaceHouse>());
        setupHousesList(new ArrayList<AroundPlaceHouse>());
    }

    @Override
    public void onAttachedToWindow() {
        attractionsScrollView = (HorizontalScrollView) findViewById(R.id.city_attractionsHorizontalScrollView);
        scrollAttractionsGridRight();
    }

    private void scrollAttractionsGridRight() {
        attractionsScrollView.post(new Runnable() {
            public void run() {
                attractionsScrollView.fullScroll(ScrollView.FOCUS_RIGHT);
            }
        });
    }

    private void setupViewPager() {
        ViewPager viewPager = (ViewPager)findViewById(R.id.city_viewpager);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.scaledDensity;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        Display display= ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();

//        viewPager.getLayoutParams().height = (int)(dpHeight/2);
        viewPager.getLayoutParams().height = height/2;

        ViewPagerAdapter adapter = new ViewPagerAdapter(this, null, null, null, null);
        viewPager.setAdapter(adapter);

        CirclePageIndicator pageIndicator = (CirclePageIndicator)findViewById(R.id.city_pageIndicator);
        pageIndicator.setViewPager(viewPager);
    }

    private void setupAttractionsGrid(int size, List<AroundPlaceHouse> attractions) {
        size = size * 3;
        gridView = (GridView) findViewById(R.id.city_attractionsGridView);
        gridView.setNumColumns(size);

        // Calculated single Item Layout Width for each grid element ....
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
//
        int width = (int)dpWidth / 2;
        int height = width * 38 / 62;
//        int width = 120;

        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;

        int totalWidth = (int) (width * size * density);
        int singleItemWidth = (int) (width * density);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                totalWidth, LinearLayout.LayoutParams.MATCH_PARENT);

        gridView.setLayoutParams(params);
        gridView.setColumnWidth(singleItemWidth);
        gridView.setHorizontalSpacing(2);
        gridView.setStretchMode(GridView.STRETCH_SPACING);
        gridView.setNumColumns(size);

        AttractionsGridAdapter gridAdapter = new AttractionsGridAdapter(this, attractions);
        gridView.setAdapter(gridAdapter);
    }

    private void setupHousesList(List<AroundPlaceHouse> houses) {

        RecyclerView mRecyclerView = (RecyclerView)findViewById(R.id.city_housesRecyclerView);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        CityHousesRecyclerAdapter recyclerAdapter = new CityHousesRecyclerAdapter(houses, this);
        mRecyclerView.setAdapter(recyclerAdapter);

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

//                    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
//                    float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
//                    float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
//                    viewPager.getLayoutParams().height = (int)(dpHeight/2);

                    Display display= ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                    int height = display.getHeight();
                    viewPager.getLayoutParams().height = height/2;

                    ViewPagerAdapter adapter = new ViewPagerAdapter(CityActivity.this, city.getName(), city.getSummary(), city.getHouseCountText(), city.getImages());
                    viewPager.setAdapter(adapter);

                    titleTextView = (TextView) findViewById(R.id.city_viewpager_item_title);
                    houseCountTextView = (TextView) findViewById(R.id.city_viewpager_item_house_count);
                    summaryTextView = (TextView) findViewById(R.id.city_viewpager_item_summary);
                    wikiTextView = (TextView) findViewById(R.id.city_viewpager_item_wiki);
                    TextView attractionsCaptionTextView = (TextView) findViewById(R.id.city_attractionsCaption);
                    Button allAttractionsButton = (Button) findViewById(R.id.city_allAttractionsButton);

//                    titleTextView.setTextSize(28 * getResources().getDisplayMetrics().density);
//                    houseCountTextView.setTextSize(18 * getResources().getDisplayMetrics().density);
//                    summaryTextView.setTextSize(18 * getResources().getDisplayMetrics().density);
//                    wikiTextView.setTextSize(18 * getResources().getDisplayMetrics().density);
                    wikiTextView.setVisibility(View.VISIBLE);
//                    attractionsCaptionTextView.setTextSize(18 * getResources().getDisplayMetrics().density);
//                    allAttractionsButton.setTextSize(16 * getResources().getDisplayMetrics().density);

                    titleTextView.setText(city.getName());
                    houseCountTextView.setText(city.getHouseCountText());
                    summaryTextView.setText(city.getSummary() + "\n" + city.getSummary() + "\n" + city.getSummary() + "\n" + city.getSummary());
//                    summaryTextView.setText(city.getSummary());

                    if (city.getHouses() != null && city.getHouses().length > 0) {
                        List<AroundPlaceHouse> list = Arrays.asList(city.getHouses());
                        List<AroundPlaceHouse> duplicatedList = new ArrayList<AroundPlaceHouse>();
                        for (int i=0 ; i < 3 ; i++) {
                            duplicatedList.addAll(list);
                        }
//                        setupAttractionsGrid(city.getHouses().length, Arrays.asList(city.getHouses()));
                        setupAttractionsGrid(city.getHouses().length, duplicatedList);
                        setupHousesList(duplicatedList);
                    }

//                    if (city.getHouses() != null && city.getHouses().length > 0) {
//                        setupHousesList(Arrays.asList(city.getHouses()));
//                    }
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
