package xyz.narengi.android.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.viewpagerindicator.CirclePageIndicator;

import info.semsamot.actionbarrtlizer.ActionBarRtlizer;
import info.semsamot.actionbarrtlizer.RtlizeEverything;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.narengi.android.R;
import xyz.narengi.android.common.Constants;
import xyz.narengi.android.common.dto.AroundPlaceHouse;
import xyz.narengi.android.common.dto.Attraction;
import xyz.narengi.android.content.AttractionDeserializer;
import xyz.narengi.android.service.RetrofitApiEndpoints;
import xyz.narengi.android.ui.adapter.AttractionContentRecyclerAdapter;
import xyz.narengi.android.ui.adapter.ImageViewPagerAdapter;

/**
 * @author Siavash Mahmoudpour
 */
public class AttractionActivity extends ActionBarActivity {

    TextView titleTextView;
    TextView houseCountTextView;
    TextView summaryTextView;
    private ActionBarRtlizer rtlizer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction);
        setupViewPager();

        showProgress();
        if (getIntent() != null && getIntent().getStringExtra("attractionUrl") != null) {
            String attractionUrl = getIntent().getStringExtra("attractionUrl");
            getAttraction(attractionUrl);
        }

        setupToolbar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupContentRecyclerView(final Attraction attraction) {
        RecyclerView mRecyclerView = (RecyclerView)findViewById(R.id.attraction_housesRecyclerView);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        AttractionContentRecyclerAdapter recyclerAdapter = new AttractionContentRecyclerAdapter(this, attraction);
        mRecyclerView.setAdapter(recyclerAdapter);

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

                if (attraction == null || attraction.getHouses() == null || attraction.getHouses().length == 0)
                    return false;

                View childView = rv.findChildViewUnder(e.getX(), e.getY());
                if (childView != null && mGestureDetector.onTouchEvent(e)) {

                    int position = rv.getChildAdapterPosition(childView);
                    if (position > 0) {
                        AroundPlaceHouse house = attraction.getHouses()[position - 1];
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

    private void setPageTitle(String title) {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.attraction_toolbar);
        if (toolbar != null) {
            TextView titleTextView = (TextView)toolbar.findViewById(R.id.text_toolbar_title);
            titleTextView.setText(title);
        }
    }

    private void setupToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.attraction_toolbar);

        /*Drawable backButtonDrawable = getResources().getDrawable(R.drawable.ic_action_back);
        backButtonDrawable.setColorFilter(getResources().getColor(android.R.color.holo_orange_dark), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationIcon(backButtonDrawable);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });*/

        if (toolbar != null) {
            ImageButton backButton = (ImageButton)toolbar.findViewById(R.id.icon_toolbar_back);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
//            actionBar.setHomeButtonEnabled(true);
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setDisplayShowHomeEnabled(true);
//            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            actionBar.setTitle("");
            actionBar.setWindowTitle("");
            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.attraction_collapse_toolbar);
            collapsingToolbarLayout.setTitle("");
            collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        }
    }

    protected void rtlizeActionBar() {
        if (getSupportActionBar() != null) {
//            rtlizer = new ActionBarRtlizer(this, "toolbar_actionbar");
            rtlizer = new ActionBarRtlizer(this);;
            ViewGroup homeView = (ViewGroup) rtlizer.getHomeView();
            RtlizeEverything.rtlize(rtlizer.getActionBarView());
            if (rtlizer.getHomeViewContainer() instanceof ViewGroup) {
                RtlizeEverything.rtlize((ViewGroup) rtlizer.getHomeViewContainer());
            }
            RtlizeEverything.rtlize(homeView);
            rtlizer.flipActionBarUpIconIfAvailable(homeView);
        }
    }

    private void showProgress() {
        LinearLayout progressBarLayout = (LinearLayout)findViewById(R.id.attraction_progressLayout);
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.attraction_progressBar);

        progressBar.setVisibility(View.VISIBLE);
        progressBarLayout.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        LinearLayout progressBarLayout = (LinearLayout)findViewById(R.id.attraction_progressLayout);
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.attraction_progressBar);

        progressBar.setVisibility(View.GONE);
        progressBarLayout.setVisibility(View.GONE);
    }

    private void setupViewPager() {
        ViewPager viewPager = (ViewPager)findViewById(R.id.attraction_viewpager);

        Display display= ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int height = display.getHeight();

        viewPager.getLayoutParams().height = height/2;

        ImageViewPagerAdapter adapter = new ImageViewPagerAdapter(this, null);
        viewPager.setAdapter(adapter);

        CirclePageIndicator pageIndicator = (CirclePageIndicator)findViewById(R.id.attraction_pageIndicator);
        pageIndicator.setViewPager(viewPager);
    }

    private void openHouseDetail(AroundPlaceHouse house) {
        String houseUrl = house.getURL();
        Intent intent = new Intent(this, HouseActivity.class);
        intent.putExtra("houseUrl", houseUrl);
        startActivity(intent);
    }

    private void getAttraction(String url) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Attraction.class, new AttractionDeserializer()).create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.SERVER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);

        Call<Attraction> call = apiEndpoints.getAttraction(url);
        call.enqueue(new Callback<Attraction>() {
            @Override
            public void onResponse(Response<Attraction> response, Retrofit retrofit) {
                hideProgress();
                Attraction attraction = response.body();
                if (attraction != null) {
                    setPageTitle(attraction.getName());
                    ViewPager viewPager = (ViewPager)findViewById(R.id.attraction_viewpager);

                    Display display= ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                    int height = display.getHeight();
                    viewPager.getLayoutParams().height = height/2;

                    ImageViewPagerAdapter adapter = new ImageViewPagerAdapter(AttractionActivity.this, attraction.getImages());
                    viewPager.setAdapter(adapter);

                    titleTextView = (TextView) findViewById(R.id.attraction_info_title);
                    houseCountTextView = (TextView) findViewById(R.id.attraction_info_house_count);
                    summaryTextView = (TextView) findViewById(R.id.attraction_info_summary);

                    titleTextView.setText(attraction.getName());
                    houseCountTextView.setText(attraction.getAroundHousesText());
//                    summaryTextView.setText(attraction.getSummary() + "\n" + attraction.getSummary() + "\n" + attraction.getSummary() + "\n" + attraction.getSummary());
//                    summaryTextView.setText(attraction.getSummary());


                    setupContentRecyclerView(attraction);

                    CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.attraction_collapse_toolbar);
                    collapsingToolbarLayout.setTitle(attraction.getName());
                    collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                // Log error here since request failed
                t.printStackTrace();
                Log.d("AttractionActivity", "getAttractions onFailure : " + t.getMessage(), t);
                hideProgress();
                Toast.makeText(AttractionActivity.this, "Error getting attraction data!", Toast.LENGTH_LONG).show();
            }
        });
    }


}