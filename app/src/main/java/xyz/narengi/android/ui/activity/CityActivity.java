package xyz.narengi.android.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.narengi.android.R;
import xyz.narengi.android.common.Constants;
import xyz.narengi.android.common.dto.AroundPlaceAttraction;
import xyz.narengi.android.common.dto.AroundPlaceHouse;
import xyz.narengi.android.common.dto.City;
import xyz.narengi.android.content.CityDeserializer;
import xyz.narengi.android.service.RetrofitApiEndpoints;
import xyz.narengi.android.ui.adapter.CityContentRecyclerAdapter;
import xyz.narengi.android.ui.widget.MyLinearLayoutManager;
import xyz.narengi.android.ui.adapter.AttractionsGridAdapter;
import xyz.narengi.android.ui.adapter.CityHousesRecyclerAdapter;
import xyz.narengi.android.ui.adapter.ImageViewPagerAdapter;

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
    private LinearLayout attractionsLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        setupViewPager();

        showProgress();
        if (getIntent() != null && getIntent().getStringExtra("cityUrl") != null) {
            String cityUrl = getIntent().getStringExtra("cityUrl");
            getCity(cityUrl);
        }

        TextView wikiTextView = (TextView)findViewById(R.id.city_viewpager_item_wiki);
        wikiTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_action_wikipedia), null);

//        setupAttractionsGrid(0, new ArrayList<AroundPlaceHouse>());

//        setupHousesList(new ArrayList<AroundPlaceHouse>());
        setupToolbar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
//            Toast.makeText(this, "Back button pressed", Toast.LENGTH_LONG).show();
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupContentRecyclerView(final City city) {
        RecyclerView mRecyclerView = (RecyclerView)findViewById(R.id.city_housesRecyclerView);

        // use a linear layout manager
//        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
//        MyLinearLayoutManager mLayoutManager = new MyLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false, getScreenHeight(this), 0);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        CityContentRecyclerAdapter recyclerAdapter = new CityContentRecyclerAdapter(this, city);
        mRecyclerView.setAdapter(recyclerAdapter);
//        mRecyclerView.setHasFixedSize(true);
//        mRecyclerView.setNestedScrollingEnabled(false);

        final GestureDetector mGestureDetector;
        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

                if (city == null || city.getHouses() == null || city.getHouses().length == 0)
                    return false;

                View childView = rv.findChildViewUnder(e.getX(), e.getY());
                if (childView != null && mGestureDetector.onTouchEvent(e)) {

                    int index = -1;
                    int position = rv.getChildAdapterPosition(childView);

                    if (city.getAttractions() != null && city.getAttractions().length > 0) {
                        index = position - 2;
                    } else {
                        index = position - 1;
                    }

                    if (index >= 0 && city.getHouses() != null && city.getHouses().length > index) {
                        AroundPlaceHouse house = city.getHouses()[index];
                        openHouseDetail(house);
                    }
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    private void setupToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.city_toolbar);

        Drawable backButtonDrawable = getResources().getDrawable(R.drawable.ic_action_back);
        backButtonDrawable.setColorFilter(getResources().getColor(android.R.color.holo_orange_dark), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationIcon(backButtonDrawable);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            actionBar.setTitle("");
            actionBar.setWindowTitle("");
            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.city_collapse_toolbar);
            collapsingToolbarLayout.setTitle("");
            collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        }
    }

    private void showProgress() {
        LinearLayout progressBarLayout = (LinearLayout)findViewById(R.id.city_progressLayout);
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.city_progressBar);

        progressBar.setVisibility(View.VISIBLE);
        progressBarLayout.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        LinearLayout progressBarLayout = (LinearLayout)findViewById(R.id.city_progressLayout);
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.city_progressBar);

        progressBar.setVisibility(View.GONE);
        progressBarLayout.setVisibility(View.GONE);
    }

    @Override
    public void onAttachedToWindow() {
        scrollAttractionsGridRight();
        scrollContentToStart();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        scrollAttractionsGridRight();
        scrollContentToStart();
    }

    private void scrollAttractionsGridRight() {
//        attractionsScrollView = (HorizontalScrollView) findViewById(R.id.city_attractionsHorizontalScrollView);
//        attractionsScrollView.post(new Runnable() {
//            public void run() {
//                attractionsScrollView.fullScroll(ScrollView.FOCUS_RIGHT);
//            }
//        });
    }

    private void scrollContentToStart() {
//        final NestedScrollView contentScrollView = (NestedScrollView) findViewById(R.id.city_contentScrollView);
//        contentScrollView.post(new Runnable() {
//            public void run() {
//                contentScrollView.scrollTo(0, 0);
//            }
//        });

//        CoordinatorLayout coordinatorLayout = (CoordinatorLayout)findViewById(R.id.city_coordinatorLayout);
//        AppBarLayout appBarLayout = (AppBarLayout)findViewById(R.id.city_appbar);
//        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
//        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
//        if (behavior != null) {
//            behavior.onNestedFling(coordinatorLayout, appBarLayout, null, 0, 10000, true);
//        }
    }

    private void setupViewPager() {
        ViewPager viewPager = (ViewPager)findViewById(R.id.city_viewpager);

        Display display= ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int height = display.getHeight();

        viewPager.getLayoutParams().height = height/2;

        ImageViewPagerAdapter adapter = new ImageViewPagerAdapter(this, null);
        viewPager.setAdapter(adapter);

        CirclePageIndicator pageIndicator = (CirclePageIndicator)findViewById(R.id.city_pageIndicator);
        pageIndicator.setViewPager(viewPager);
    }

    private void setupAttractionsGrid(int size, List<AroundPlaceAttraction> attractions) {
//        size = size * 3;

        //city_attractionsLayout
        LayoutInflater inflater = LayoutInflater.from(this);
        attractionsLayout = (LinearLayout)inflater.inflate(R.layout.city_attractions, null);

        gridView = (GridView) attractionsLayout.findViewById(R.id.city_attractionsGridView);
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

    private void showAttractionViews() {

        gridView = (GridView) findViewById(R.id.city_attractionsGridView);
        LinearLayout attractionsLayout = (LinearLayout)findViewById(R.id.city_attractionsLayout);
        RelativeLayout attractionsHeaderLayout = (RelativeLayout)findViewById(R.id.city_attractionsHeaderLayout);
        Button allAttractionsButton = (Button)findViewById(R.id.city_allAttractionsButton);
        TextView attractionsCaptionTextView = (TextView)findViewById(R.id.city_attractionsCaption);
        HorizontalScrollView attractionsScrollView = (HorizontalScrollView)findViewById(R.id.city_attractionsHorizontalScrollView);
        View attractionsFooterView = findViewById(R.id.city_attractionsFooter);

        attractionsLayout.setVisibility(View.VISIBLE);
        attractionsHeaderLayout.setVisibility(View.VISIBLE);
        allAttractionsButton.setVisibility(View.VISIBLE);
        attractionsCaptionTextView.setVisibility(View.VISIBLE);
        attractionsScrollView.setVisibility(View.VISIBLE);
        gridView.setVisibility(View.VISIBLE);
        attractionsFooterView.setVisibility(View.VISIBLE);
    }

    private void hideAttractionViews() {
        gridView = (GridView) findViewById(R.id.city_attractionsGridView);
        LinearLayout attractionsLayout = (LinearLayout)findViewById(R.id.city_attractionsLayout);
        RelativeLayout attractionsHeaderLayout = (RelativeLayout)findViewById(R.id.city_attractionsHeaderLayout);
        Button allAttractionsButton = (Button)findViewById(R.id.city_allAttractionsButton);
        TextView attractionsCaptionTextView = (TextView)findViewById(R.id.city_attractionsCaption);
        HorizontalScrollView attractionsScrollView = (HorizontalScrollView)findViewById(R.id.city_attractionsHorizontalScrollView);
        View attractionsFooterView = findViewById(R.id.city_attractionsFooter);

        attractionsLayout.setVisibility(View.INVISIBLE);
        attractionsHeaderLayout.setVisibility(View.INVISIBLE);
        allAttractionsButton.setVisibility(View.INVISIBLE);
        attractionsCaptionTextView.setVisibility(View.INVISIBLE);
        attractionsScrollView.setVisibility(View.INVISIBLE);
        gridView.setVisibility(View.INVISIBLE);
        attractionsFooterView.setVisibility(View.INVISIBLE);
    }

    private int getScreenHeight(Context context) {
        int measuredHeight;
        Point size = new Point();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            wm.getDefaultDisplay().getSize(size);
            measuredHeight = size.y;
        } else {
            Display d = wm.getDefaultDisplay();
            measuredHeight = d.getHeight();
        }

        return measuredHeight;
    }

    private void setupHousesList(final List<AroundPlaceHouse> houses) {

        RecyclerView mRecyclerView = (RecyclerView)findViewById(R.id.city_housesRecyclerView);

        LinearLayout attractionsLayout = (LinearLayout)findViewById(R.id.city_attractionsLayout);
        int attractionsLayoutHeight;
        attractionsLayoutHeight = attractionsLayout.getMeasuredHeight();
        if (attractionsLayoutHeight <= 0)
            attractionsLayoutHeight = attractionsLayout.getHeight();

        TextView housesCaptionTextView = (TextView)findViewById(R.id.city_housesCaption);
        int housesCaptionHeight;
        housesCaptionHeight = housesCaptionTextView.getMeasuredHeight();
        if (housesCaptionHeight <= 0)
            housesCaptionHeight = housesCaptionTextView.getHeight();
        housesCaptionHeight = housesCaptionHeight * 3;


        int housesHeaderHeight = (attractionsLayoutHeight + housesCaptionHeight) * 3;
        // use a linear layout manager
//        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        MyLinearLayoutManager mLayoutManager = new MyLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false, getScreenHeight(this), housesHeaderHeight);
        mRecyclerView.setLayoutManager(mLayoutManager);

        CityHousesRecyclerAdapter recyclerAdapter = new CityHousesRecyclerAdapter(houses, this);
        mRecyclerView.setAdapter(recyclerAdapter);

        if (this.attractionsLayout != null) {

        }

        final GestureDetector mGestureDetector;
        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View childView = rv.findChildViewUnder(e.getX(), e.getY());
                if (childView != null && mGestureDetector.onTouchEvent(e)) {

                    int position = rv.getChildAdapterPosition(childView);
                    if (houses.size() > position) {
                        AroundPlaceHouse house = houses.get(position);
                        openHouseDetail(house);
                    }

                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);
    }

    private void openHouseDetail(AroundPlaceHouse house) {
        String houseUrl = house.getURL();
        Intent intent = new Intent(this, HouseActivity.class);
        intent.putExtra("houseUrl", houseUrl);
        startActivity(intent);
    }

    private void getCity(String url) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(City.class, new CityDeserializer()).create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.SERVER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);

        Call<City> call = apiEndpoints.getCity(url);
        call.enqueue(new Callback<City>() {
            @Override
            public void onResponse(Response<City> response, Retrofit retrofit) {
//                int statusCode = response.code();
                hideProgress();
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

                    ImageViewPagerAdapter adapter = new ImageViewPagerAdapter(CityActivity.this, city.getImages());
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
//                    summaryTextView.setText(city.getSummary() + "\n" + city.getSummary() + "\n" + city.getSummary() + "\n" + city.getSummary());
                    summaryTextView.setText(city.getSummary());


                    setupContentRecyclerView(city);

//                    if (city.getAttractions() != null && city.getAttractions().length > 0) {
//                        showAttractionViews();
//                        setupAttractionsGrid(city.getAttractions().length, Arrays.asList(city.getAttractions()));
//                    } else {
//                        hideAttractionViews();
//                    }
//
//                    if (city.getHouses() != null && city.getHouses().length > 0) {
//                        setupHousesList(Arrays.asList(city.getHouses()));
//                    }

                    CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.city_collapse_toolbar);
                    collapsingToolbarLayout.setTitle(city.getName());
                    collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

//                    collapsingToolbarLayout.setContentScrimColor(getResources().getColor(android.R.color.holo_orange_dark));
//                    collapsingToolbarLayout.setStatusBarScrimColor(getResources().getColor(android.R.color.holo_orange_light));

//                    setupToolbar();

                    scrollContentToStart();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                // Log error here since request failed
                t.printStackTrace();
                Log.d("CityActivity", "getCity onFailure : " + t.getMessage(), t);
                hideProgress();
                Toast.makeText(CityActivity.this, "Error getting city data!", Toast.LENGTH_LONG).show();
            }
        });
    }


}
