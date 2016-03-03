package xyz.narengi.android.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.concurrent.ExecutionException;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.narengi.android.R;
import xyz.narengi.android.common.Constants;
import xyz.narengi.android.common.dto.House;
import xyz.narengi.android.common.dto.HouseFeature;
import xyz.narengi.android.content.HouseDeserializer;
import xyz.narengi.android.service.ImageDownloaderAsyncTask;
import xyz.narengi.android.service.RetrofitApiEndpoints;
import xyz.narengi.android.ui.adapter.HouseContentRecyclerAdapter;
import xyz.narengi.android.ui.adapter.ImageViewPagerAdapter;

/**
 * @author Siavash Mahmoudpour
 */
public class HouseActivity extends ActionBarActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house);
        setupToolbar();
//        setupViewPager();

        showProgress();
        if (getIntent() != null && getIntent().getStringExtra("houseUrl") != null) {
            String houseUrl = getIntent().getStringExtra("houseUrl");
            getHouse(houseUrl);
        }

//        if (getIntent() != null && getIntent().getSerializableExtra("house") != null) {
//            Serializable houseSerializable = getIntent().getSerializableExtra("house");
//            if (houseSerializable instanceof AroundPlaceHouse) {
//                AroundPlaceHouse house = (AroundPlaceHouse)houseSerializable;
//                setHouse(house);
//            }
//        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
//        setupToolbar();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
//        setupToolbar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
//            Toast.makeText(this, "Back button pressed", Toast.LENGTH_LONG).show();
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.house_toolbar);

        Drawable backButtonDrawable = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
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
            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.house_collapse_toolbar);
            collapsingToolbarLayout.setTitle("");
            collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        }

        /*final NestedScrollView contentScrollView = (NestedScrollView)findViewById(R.id.house_contentScrollView);
//        LinearLayout contentLayout = (LinearLayout)findViewById(R.id.house_titleInfoLayout);

        ViewTreeObserver observer = contentScrollView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // TODO Auto-generated method stub
                int height= contentScrollView.getHeight();
                Toast.makeText(HouseActivity.this, "Content height : " + height, Toast.LENGTH_LONG).show();

                Display display= ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                int displayHeight = display.getHeight();
                toolbar.getLayoutParams().height = displayHeight - height;

//                contentScrollView.getViewTreeObserver().removeGlobalOnLayoutListener(
//                        this);
            }
        });*/

    }

    private void showProgress() {
        LinearLayout progressBarLayout = (LinearLayout)findViewById(R.id.house_progressLayout);
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.house_progressBar);

        progressBar.setVisibility(View.VISIBLE);
        progressBarLayout.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        LinearLayout progressBarLayout = (LinearLayout)findViewById(R.id.house_progressLayout);
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.house_progressBar);

        progressBar.setVisibility(View.GONE);
        progressBarLayout.setVisibility(View.GONE);
    }

    private void setHouse(final House house) {
//        String mapUrl = "https://maps.googleapis.com/maps/api/staticmap?center=";
//        if (house.getPosition() != null) {
//            house.getPosition().setLat(35.710139);
//            house.getPosition().setLng(51.418049);
//            String latLngString = String.valueOf(house.getPosition().getLat()) + "," + String.valueOf(house.getPosition().getLng());
//            mapUrl = mapUrl + latLngString;
//            mapUrl = mapUrl + "&zoom=14&size=";
//            Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//            int width = display.getWidth();
//            mapUrl = mapUrl + String.valueOf(width) + "x" + String.valueOf(120);
//            mapUrl = mapUrl + "&markers=color:red%7Clabel:C%7C";
//            mapUrl = mapUrl + latLngString;
//            getHouseMapImage(mapUrl);
//        }

        if (house.getImages() != null && house.getImages().length > 0) {
            setupImageViewPager(house.getImages());
        }

//        setupTitleInfoLayout(house);
        TextView priceTextView = (TextView)findViewById(R.id.house_price);
        priceTextView.setVisibility(View.VISIBLE);
        priceTextView.setText(house.getCost());

        FloatingActionButton houseHostFab= (FloatingActionButton)findViewById(R.id.house_hostFab);
        houseHostFab.setVisibility(View.VISIBLE);
        houseHostFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                house.getHost().setImageUrl(house.getImages()[0]);
                if (house.getHost() != null && house.getHost().getHostURL() != null)
                    openHostActivity(house.getHost().getHostURL());
            }
        });
        if (house.getHost() != null && house.getHost().getImageUrl() != null)
            setupHostFab(house.getHost().getImageUrl());

//        setupSpecsLayout(house);
//        setDescription(house);
//        setupFeaturesLayout(house);

        RecyclerView mRecyclerView = (RecyclerView)findViewById(R.id.house_contentRecyclerView);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
//        MyLinearLayoutManager mLayoutManager = new MyLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false, getScreenHeight(this), 0);
//        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
//        HouseLinearLayoutManager mLayoutManager = new HouseLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        HouseContentRecyclerAdapter recyclerAdapter = new HouseContentRecyclerAdapter(this, house);
        mRecyclerView.setAdapter(recyclerAdapter);
//        mRecyclerView.setHasFixedSize(false);
//        mRecyclerView.setNestedScrollingEnabled(true);

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.house_collapse_toolbar);
        collapsingToolbarLayout.setTitle(house.getName());
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

//        collapsingToolbarLayout.setContentScrimColor(getResources().getColor(android.R.color.holo_orange_dark));
//        collapsingToolbarLayout.setStatusBarScrimColor(getResources().getColor(android.R.color.holo_orange_light));

//        setupToolbar();
    }

    private void openHostActivity(String hostUrl) {
        Intent intent = new Intent(this, HostActivity.class);
        intent.putExtra("hostUrl", hostUrl);
        startActivity(intent);
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

    private void setupImageViewPager(String[] images) {

//        RelativeLayout viewPagerLayout = (RelativeLayout)findViewById(R.id.house_imageLayout);
        ViewPager viewPager = (ViewPager)findViewById(R.id.house_imageViewpager);

        Display display= ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int height = display.getHeight();
        viewPager.getLayoutParams().height = height/2;
//        viewPagerLayout.getLayoutParams().height = height/2;

        ImageViewPagerAdapter adapter = new ImageViewPagerAdapter(this, images);
        viewPager.setAdapter(adapter);

        CirclePageIndicator pageIndicator = (CirclePageIndicator)findViewById(R.id.house_imagePageIndicator);
        pageIndicator.setViewPager(viewPager);
    }

    private void getHouseMapImage(String mapUrl) {

        ImageView mapImageView = (ImageView)findViewById(R.id.house_mapImage);
        Picasso.with(this).load(mapUrl).into(mapImageView);
    }

    private void setupTitleInfoLayout(House house) {

        TextView priceTextView = (TextView)findViewById(R.id.house_price);
        TextView cityTextView = (TextView)findViewById(R.id.house_city);
        TextView summaryTextView = (TextView)findViewById(R.id.house_summary);
        TextView ratingTextView = (TextView)findViewById(R.id.house_rating);

        priceTextView.setText(house.getCost());
        cityTextView.setText(house.getCityName());
        summaryTextView.setText(house.getSummary());
//        ratingDescriptionTextView.setText(getString(R.string.house_rating_description, house.getRating()));
        ratingTextView.setText("(" + house.getRating() + " رای" + ")");


        RatingBar houseRatingBar = (RatingBar)findViewById(R.id.house_ratingBar);
        Drawable drawable = houseRatingBar.getProgressDrawable();
        drawable.setColorFilter(Color.parseColor("#FFFDEC00"), PorterDuff.Mode.SRC_ATOP);
    }

    private void setupHostFab(String imageUrl) {

        FloatingActionButton houseHostFab= (FloatingActionButton)findViewById(R.id.house_hostFab);
        try {
            int width=0 , height=0;
            if (houseHostFab != null) {
                if (houseHostFab.getWidth() > 0 && houseHostFab.getHeight() > 0) {
                    width = houseHostFab.getWidth();
                    height = houseHostFab.getHeight();
                } else if (houseHostFab.getLayoutParams() != null) {
                    width = houseHostFab.getLayoutParams().width;
                    height = houseHostFab.getLayoutParams().height;
                }
            }
            ImageDownloaderAsyncTask imageDownloaderAsyncTask = new ImageDownloaderAsyncTask(this, imageUrl,
                    width, height);
            AsyncTask asyncTask = imageDownloaderAsyncTask.execute();

            Bitmap hostImageBitmap = (Bitmap)asyncTask.get();
            if (hostImageBitmap != null) {

                Bitmap circleBitmap = Bitmap.createBitmap(hostImageBitmap.getWidth(), hostImageBitmap.getHeight(), Bitmap.Config.ARGB_8888);

                BitmapShader shader = new BitmapShader (hostImageBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                Paint paint = new Paint();
                paint.setShader(shader);
                paint.setAntiAlias(true);
                Canvas c = new Canvas(circleBitmap);
                c.drawCircle(hostImageBitmap.getWidth() / 2, hostImageBitmap.getHeight() / 2, hostImageBitmap.getWidth() / 2, paint);

                houseHostFab.setImageBitmap(hostImageBitmap);

//                houseHostFab.setRippleColor(getResources().getColor(android.R.color.holo_orange_dark));
//                houseHostFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_orange_dark)));
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void setupSpecsLayout(House house) {

        TextView bedCountTextView = (TextView)findViewById(R.id.house_bedCount);
        TextView guestCountTextView = (TextView)findViewById(R.id.house_guestCount);
        TextView bedroomCountTextView = (TextView)findViewById(R.id.house_bedroomCount);
        TextView typeTextView = (TextView)findViewById(R.id.house_type);

        bedCountTextView.setText(getString(R.string.house_bed_count, house.getBedCount()));
        guestCountTextView.setText(getString(R.string.house_guest_count, house.getGuestCount()));
        bedroomCountTextView.setText(getString(R.string.house_bedroom_count, house.getBedroomCount()));

        if (house.getType() != null) {
            if (house.getType().equals("apartment")) {
                typeTextView.setText(getString(R.string.house_type_apartment));
            } else if (house.getType().equals("villa")) {
                typeTextView.setText(getString(R.string.house_type_villa));
            } else if (house.getType().equals("house")) {
                typeTextView.setText(getString(R.string.house_type_house));
            } else {
                typeTextView.setText(getString(R.string.house_type_house));
            }
        } else {
            typeTextView.setText(getString(R.string.house_type_house));
        }

        bedCountTextView.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_action_maps_hotel), null, null);
        guestCountTextView.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_action_social_person_outline), null, null);
        bedroomCountTextView.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_action_maps_store_mall_directory), null, null);
        typeTextView.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_action_action_account_balance), null, null);
    }

    private void setDescription(House house) {

        String description = house.getName() + ", " + house.getSummary() + ", " + house.getFeatureSummray();
        description += description;

        TextView descriptionTextView = (TextView)findViewById(R.id.house_description);
        descriptionTextView.setText(description);
    }

    private void setupFeaturesLayout(final House house) {

        if (house.getFeatureList() != null) {

            LinearLayout featuresLayout = (LinearLayout)findViewById(R.id.house_featuresLayout);

            if (house.getFeatureList().length <= 5) {

                float layoutWeight = featuresLayout.getWeightSum() / house.getFeatureList().length;
                for (HouseFeature houseFeature:house.getFeatureList()) {
                    TextView featureTextView = createHouseFeatureTextView(houseFeature, layoutWeight);
                    featuresLayout.addView(featureTextView);
                }
            } else {

                float layoutWeight = featuresLayout.getWeightSum() / 5;

                int extraFeatures = house.getFeatureList().length - 4;
                String buttonText = String.valueOf(extraFeatures) + "+";
                Button moreFeaturesButton = createMoreFeaturesButton(buttonText, layoutWeight);
                featuresLayout.addView(moreFeaturesButton);

                moreFeaturesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(HouseActivity.this, HouseFeaturesActivity.class);
                        intent.putExtra("house", house);
                        startActivity(intent);
                    }
                });

                for (int i=0 ; i < 4 ; i++) {

                    HouseFeature houseFeature = house.getFeatureList()[i];
                    TextView featureTextView = createHouseFeatureTextView(houseFeature, layoutWeight);
                    featuresLayout.addView(featureTextView);
                }
            }
        }
    }

    private TextView createHouseFeatureTextView(HouseFeature houseFeature, float layoutWeight) {

        TextView featureTextView = new TextView(this);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, layoutWeight);
        params.gravity = Gravity.CENTER_VERTICAL;
        params.setMargins(8, 8, 8, 8);

        featureTextView.setPadding(4, 4, 4, 4);
        featureTextView.setTextAppearance(this, android.R.style.TextAppearance_Small);
//        featureTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.font_size_small));
        featureTextView.setTextColor(Color.BLACK);
        featureTextView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        featureTextView.setGravity(Gravity.CENTER);
        featureTextView.setTypeface(featureTextView.getTypeface(), Typeface.BOLD);
        featureTextView.setText(houseFeature.getName());
        featureTextView.setLayoutParams(params);

        if (houseFeature.getType() != null) {
            switch (houseFeature.getType()) {
                case "furniture":
                    Drawable drawable = getResources().getDrawable(android.R.drawable.ic_menu_call);
                    drawable.setColorFilter(getResources().getColor(android.R.color.holo_orange_dark),PorterDuff.Mode.SRC_ATOP);
                    featureTextView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
                    break;
                case "oven":
                    drawable = getResources().getDrawable(android.R.drawable.ic_menu_mylocation);
                    drawable.setColorFilter(getResources().getColor(android.R.color.holo_orange_dark),PorterDuff.Mode.SRC_ATOP);
                    featureTextView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
                    break;
                case "internet":
                    drawable = getResources().getDrawable(android.R.drawable.ic_menu_camera);
                    drawable.setColorFilter(getResources().getColor(android.R.color.holo_orange_dark),PorterDuff.Mode.SRC_ATOP);
                    featureTextView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
                    break;
                default:
                    drawable = getResources().getDrawable(android.R.drawable.ic_menu_agenda);
                    drawable.setColorFilter(getResources().getColor(android.R.color.holo_orange_dark),PorterDuff.Mode.SRC_ATOP);
                    featureTextView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
                    break;
            }
        } else {
            Drawable drawable = getResources().getDrawable(android.R.drawable.ic_dialog_email);
            drawable.setColorFilter(getResources().getColor(android.R.color.holo_orange_dark),PorterDuff.Mode.SRC_ATOP);
            featureTextView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        }

        return featureTextView;
    }

    private Button createMoreFeaturesButton(String text, float layoutWeight) {

        Button moreFeaturesButton = new Button(this);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, layoutWeight);
        params.gravity = Gravity.CENTER_VERTICAL;
        params.setMargins(8, 8, 8, 8);

        moreFeaturesButton.setPadding(4, 4, 4, 4);
//        moreFeaturesButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.font_size_normal));
        moreFeaturesButton.setTextAppearance(this, android.R.style.TextAppearance_Medium);
        moreFeaturesButton.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
        moreFeaturesButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        moreFeaturesButton.setGravity(Gravity.CENTER);
        moreFeaturesButton.setTypeface(moreFeaturesButton.getTypeface(), Typeface.BOLD);
        moreFeaturesButton.setText(text);
        moreFeaturesButton.setLayoutParams(params);

        return moreFeaturesButton;
    }

    private void getHouse(String url) {

        url = url + "?filter[review]=5&filter[feature]=10000";

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(House.class, new HouseDeserializer()).create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.SERVER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);

        Call<House> call = apiEndpoints.getHouse(url);
        call.enqueue(new Callback<House>() {
            @Override
            public void onResponse(Response<House> response, Retrofit retrofit) {
//                int statusCode = response.code();
                hideProgress();
                House house = response.body();
                if (house != null) {

                    Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                    int height = display.getHeight();
                    setHouse(house);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                // Log error here since request failed
                t.printStackTrace();
                Log.d("HouseActivity", "getHouse onFailure : " + t.getMessage(), t);
                hideProgress();
                Toast.makeText(HouseActivity.this, "Error getting house data!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
