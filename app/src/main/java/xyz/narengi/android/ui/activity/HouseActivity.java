package xyz.narengi.android.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.siyamed.shapeimageview.mask.PorterShapeImageView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nineoldandroids.view.ViewHelper;
import com.squareup.picasso.Picasso;

import info.semsamot.actionbarrtlizer.ActionBarRtlizer;
import info.semsamot.actionbarrtlizer.RtlizeEverything;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.AccountProfile;
import xyz.narengi.android.common.dto.House;
import xyz.narengi.android.armin.model.network.RetrofitApiEndpoints;
import xyz.narengi.android.service.RetrofitService;
import xyz.narengi.android.ui.dialog.BetaDialog;
import xyz.narengi.android.ui.dialog.FeatureDialog;
import xyz.narengi.android.ui.widget.CustomTextView;
import xyz.narengi.android.ui.widget.ObservableScrollView;
import xyz.narengi.android.ui.widget.ObservableScrollViewCallbacks;
import xyz.narengi.android.ui.widget.ScrollState;
import xyz.narengi.android.ui.widget.ScrollUtils;
import xyz.narengi.android.util.Util;

/**
 * @author Siavash Mahmoudpour
 */
public class HouseActivity extends AppCompatActivity implements ObservableScrollViewCallbacks {
    private House house;
    private ActionBarRtlizer rtlizer;
    private String[] stringArray;

    private View header;
    private View mToolbarView;
    private ObservableScrollView mScrollView;
    private int mParallaxImageHeight;
    private int mParallaxImageHeight2;
    private View llErrorContainer;
    private View btnRetry;
    private String houseUrl;
    private CustomTextView toolbarTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_house2);
//        setupToolbar();


        btnRetry = findViewById(R.id.btnRetry);
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getHouse(houseUrl);
            }
        });

        llErrorContainer = findViewById(R.id.llErrorContainer);
        mToolbarView = findViewById(R.id.toolbar);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.primary)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        } else {
            mToolbarView.setPadding(0, 0, 0, 0);
        }


//        View back = findViewById(R.id.icon_toolbar_back);
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });


        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        header = findViewById(R.id.header);

//        mToolbarView.

        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);

        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);
        mParallaxImageHeight2 = getResources().getDimensionPixelSize(R.dimen.parallax_image_height2);


        toolbarTitle = (CustomTextView) findViewById(R.id.toolbarTitle);
//        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
//        collapsingToolbar.setTitle(" ");

        findViewById(R.id.book).setOnClickListener(a);
        findViewById(R.id.v1).setOnClickListener(a);
        findViewById(R.id.v2).setOnClickListener(a);
        findViewById(R.id.v3).setOnClickListener(a);
        findViewById(R.id.v4).setOnClickListener(a);

        if (getIntent() != null && getIntent().getStringExtra("houseId") != null) {
            stringArray = getIntent().getStringArrayExtra("images");


            ViewPager vpImages = (ViewPager) findViewById(R.id.vpImages);
            if (stringArray != null) {
                PicturesPagerAdapter adapter = new PicturesPagerAdapter(stringArray, this);
                vpImages.setAdapter(adapter);
            }

//        showProgress();
            houseUrl = getIntent().getStringExtra("houseId");
//            getHouseImage(houseUrl);
            getHouse(houseUrl);
        } else {
            Toast.makeText(this, getString(R.string.error_alert_title), Toast.LENGTH_SHORT).show();
        }
    }

    View.OnClickListener a = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showDialog();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 301) {
            if (resultCode == 302) {
                //user registered
                openSignUpConfirm();
            } else if (resultCode == 303) {
                //user logged in
                openBookHouse();
            }
        }

        if (resultCode == 401 || resultCode == 101 || requestCode == 101) {
            openBookHouse();
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    protected void rtlizeActionBar() {
        if (getSupportActionBar() != null) {
//            rtlizer = new ActionBarRtlizer(this, "toolbar_actionbar");
            rtlizer = new ActionBarRtlizer(this);
            ;
            ViewGroup homeView = (ViewGroup) rtlizer.getHomeView();
            RtlizeEverything.rtlize(rtlizer.getActionBarView());
            if (rtlizer.getHomeViewContainer() instanceof ViewGroup) {
                RtlizeEverything.rtlize((ViewGroup) rtlizer.getHomeViewContainer());
            }
            RtlizeEverything.rtlize(homeView);
            rtlizer.flipActionBarUpIconIfAvailable(homeView);
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        boolean result = super.onCreateOptionsMenu(menu);
        rtlizer = new ActionBarRtlizer(this);
        ViewGroup actionBarView = rtlizer.getActionBarView();
        View actionMenuView = rtlizer.getActionMenuView();
//        ViewGroup homeView = (ViewGroup)rtlizer.findViewByClass("HomeView", actionBarView);
        ViewGroup homeView = (ViewGroup)rtlizer.getHomeView();

        rtlizer.flipActionBarUpIconIfAvailable(homeView);
        RtlizeEverything.rtlize(actionBarView, true);
        RtlizeEverything.rtlize(homeView, true);
//        return result;
        return super.onCreateOptionsMenu(menu);
    }*/

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//
//        rtlizer = new ActionBarRtlizer(this);
//        ViewGroup actionBarView = rtlizer.getActionBarView();
//        View actionMenuView = rtlizer.getActionMenuView();
////        ViewGroup homeView = (ViewGroup)rtlizer.findViewByClass("HomeView", actionBarView);
//        ViewGroup homeView = (ViewGroup)rtlizer.getHomeView();
//
//        rtlizer.flipActionBarUpIconIfAvailable(homeView);
//        RtlizeEverything.rtlize(actionBarView, true);
//        RtlizeEverything.rtlize(homeView, true);
//
//        return super.onPrepareOptionsMenu(menu);
//    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void openSignUpConfirm() {
        Intent intent = new Intent(this, SignUpConfirmActivity.class);
        startActivityForResult(intent, 101);
    }

    private void setPageTitle(String title) {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.house_toolbar);
        if (toolbar != null) {
            TextView titleTextView = (TextView) toolbar.findViewById(R.id.text_toolbar_title);
            titleTextView.setText(title);
        }
    }

    private void setupToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.house_toolbar);

//        Drawable backButtonDrawable = getResources().getDrawable(R.drawable.ic_action_back);
//        backButtonDrawable.setColorFilter(getResources().getColor(android.R.color.holo_orange_dark), PorterDuff.Mode.SRC_ATOP);
//        toolbar.setNavigationIcon(backButtonDrawable);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });

        if (toolbar != null) {
            ImageButton backButton = (ImageButton) toolbar.findViewById(R.id.icon_toolbar_back);
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
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.house_progressBar);
        progressBar.setVisibility(View.VISIBLE);
        View content = findViewById(R.id.content);
        content.setVisibility(View.GONE);
    }

    private void hideProgress() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.house_progressBar);
        progressBar.setVisibility(View.GONE);
        View content = findViewById(R.id.content);
        content.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.house_progressBar);
        progressBar.setVisibility(View.GONE);
    }

    //
//    private void getHouseImage(String houseUrl) {
//        Retrofit retrofit = RetrofitService.getInstance().getRetrofit();
//
//        RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);
//        Call<ImageInfo[]> call = apiEndpoints.getHouseImages(houseUrl + "/pictures");
//
//        call.enqueue(new Callback<ImageInfo[]>() {
//            @Override
//            public void onResponse(Call<ImageInfo[]> call, Response<ImageInfo[]> response) {
//                int statusCode = response.code();
//                ImageInfo[] result = response.body();
//                if (result != null && result.length > 0) {
//                    String[] imageUrls = new String[result.length];
//                    for (int i = 0; i < result.length; i++) {
//                        imageUrls[i] = result[i].getUrl();
//                        setupImageViewPager(imageUrls);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ImageInfo[]> call, Throwable t) {
//                t.printStackTrace();
//            }
//        });
//    }

    private void setHouse(final House house) {

        this.house = house;
//        setPageTitle(house.getName());

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

//        if (house.getPictures() != null && house.getPictures().length > 0) {
//            setupImageViewPager(house.getPictures());
//        }

//        setupTitleInfoLayout(house);
        TextView name = (TextView) findViewById(R.id.house_name);
        name.setText(house.getName());


        TextView location = (TextView) findViewById(R.id.location);
        if (location != null) {
            location.setText(house.getLocation().getCity() + "، " + house.getLocation().getProvince());
        }

        TextView priceTextView = (TextView) findViewById(R.id.house_price);
//        if (house.getPrice() != null) {
        priceTextView.setText(Util.convertNumber(house.getPriceString().split(" " , 2)[0] + ""));
//        }
        TextView priceText = (TextView) findViewById(R.id.price_string);
//        if (house.getPrice() != null) {
        priceText.setText(Util.convertNumber(house.getPriceString().split(" ", 2)[1] + " \n" + getString(R.string.harshabeghamat)));
//        }


        View priceLayout = findViewById(R.id.priceLayout);
        priceLayout.setVisibility(View.VISIBLE);

        TextView typeText = (TextView) findViewById(R.id.typeText);
        if (house.getType() != null) {
            typeText.setText(house.getType().getTitle());
        }

        TextView room = (TextView) findViewById(R.id.room);
        if (house.getSpec() != null) {
            room.setText(Util.convertNumber(house.getSpec().getBedroomCount() + " " + getString(R.string.bedroom)));
        }

        TextView geust = (TextView) findViewById(R.id.geust);
        if (house.getSpec() != null) {
            geust.setText(Util.convertNumber(house.getSpec().getGuestCount() + " " + getString(R.string.geust)));
        }

        TextView bed = (TextView) findViewById(R.id.bed);
        if (house.getSpec() != null) {
            bed.setText(Util.convertNumber(house.getSpec().getBedCount() + " " + getString(R.string.bed)));
        }

        TextView description = (TextView) findViewById(R.id.description);
        description.setText(house.getSummary());

//        Picasso.with(this).load(house.getPictures()[0].getHash())

        ImageView map = (ImageView) findViewById(R.id.map);
        Picasso.with(this).load("https://api.narengi.xyz/v1" + house.getGoogleMap()).into(map);

        PorterShapeImageView avatar = (PorterShapeImageView) findViewById(R.id.avatar);
        avatar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(HouseActivity.this, ViewProfileActivity1.class);
                intent.putExtra("id", house.getOwner().getUid());
                startActivity(intent);
            }
        });

        try {
            Picasso.with(this).load("https://api.narengi.xyz/v1" + house.getOwner().getPicture().getUrl()).into(avatar);
        } catch (Exception ignored) {
        }

        if (house.getFeatureList() != null && house.getFeatureList().length > 0) {
            View view = findViewById(R.id.feature1);
            View featureList = findViewById(R.id.featureList);
            TextView featureText1 = (TextView) findViewById(R.id.featureText1);
            featureText1.setText(house.getFeatureList()[0].getTitle());
            ImageView featureImage1 = (ImageView) findViewById(R.id.featureImage1);
            Picasso.with(this).load("http://api.narengi.xyz/v1/medias/feature/" + house.getFeatureList()[0].getKey()).into(featureImage1);
            view.setVisibility(View.VISIBLE);
            featureList.setVisibility(View.VISIBLE);
        }

        if (house.getFeatureList() != null && house.getFeatureList().length > 1) {
            View view = findViewById(R.id.feature2);
            TextView featureText2 = (TextView) findViewById(R.id.featureText2);
            featureText2.setText(house.getFeatureList()[1].getTitle());
            view.setVisibility(View.VISIBLE);
            ImageView featureImage2 = (ImageView) findViewById(R.id.featureImage2);
            Picasso.with(this).load("http://api.narengi.xyz/v1/medias/feature/" + house.getFeatureList()[1].getKey()).into(featureImage2);
        }

        if (house.getFeatureList() != null && house.getFeatureList().length > 2) {
            View view = findViewById(R.id.feature3);
            TextView featureText3 = (TextView) findViewById(R.id.featureText3);
            featureText3.setText(house.getFeatureList()[2].getTitle());
            ImageView featureImage3 = (ImageView) findViewById(R.id.featureImage3);
            Picasso.with(this).load("http://api.narengi.xyz/v1/medias/feature/" + house.getFeatureList()[2].getKey()).into(featureImage3);
            view.setVisibility(View.VISIBLE);
        }

        if (house.getFeatureList() != null && house.getFeatureList().length > 3) {
            View view = findViewById(R.id.feature4);
            TextView featureText4 = (TextView) findViewById(R.id.featureText4);
            featureText4.setText(house.getFeatureList()[3].getTitle());
            ImageView featureImage4 = (ImageView) findViewById(R.id.featureImage4);
            Picasso.with(this).load("http://api.narengi.xyz/v1/medias/feature/" + house.getFeatureList()[3].getKey()).into(featureImage4);
            view.setVisibility(View.VISIBLE);
        }

        if (house.getFeatureList() != null && house.getFeatureList().length > 4) {
            View more_feature = findViewById(R.id.more_feature);
            TextView moreFeatureText = (TextView) findViewById(R.id.moreFeatureText);
            moreFeatureText.setText(Util.convertNumber( "+" + (house.getFeatureList().length - 4)));
            more_feature.setVisibility(View.VISIBLE);

            more_feature.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new FeatureDialog(HouseActivity.this, house.getFeatureList()).show();
                }
            });
        }

//        TextView priceTextView = (TextView) findViewById(R.id.house_price);
//        priceTextView.setVisibility(View.VISIBLE);
//        priceTextView.setText(house.getCost());

//        FloatingActionButton houseHostFab = (FloatingActionButton) findViewById(R.id.house_hostFab);
//        houseHostFab.setVisibility(View.VISIBLE);
//        houseHostFab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                house.getHost().setImageUrl(house.getPictures()[0]);
//                if (house.getHost() != null && house.getHost().getHostURL() != null)
//                    openHostActivity(house.getHost().getHostURL());
//            }
//        });
//        if (house.getHost() != null && house.getHost().getImageUrl() != null)
//            setupHostFab(house.getHost().getImageUrl());

//        setupSpecsLayout(house);
//        setDescription(house);
//        setupFeaturesLayout(house);

//        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.house_contentRecyclerView);

        // use a linear layout manager
//        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
//        MyLinearLayoutManager mLayoutManager = new MyLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false, getScreenHeight(this), 0);
//        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
//        HouseLinearLayoutManager mLayoutManager = new HouseLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        mRecyclerView.setLayoutManager(mLayoutManager);

//        HouseContentRecyclerAdapter recyclerAdapter = new HouseContentRecyclerAdapter(this, house);
//        mRecyclerView.setAdapter(recyclerAdapter);
//        mRecyclerView.setHasFixedSize(false);
//        mRecyclerView.setNestedScrollingEnabled(true);

//        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.house_collapse_toolbar);
//        collapsingToolbarLayout.setTitle(house.getName());
//        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

//        collapsingToolbarLayout.setContentScrimColor(getResources().getColor(android.R.color.holo_orange_dark));
//        collapsingToolbarLayout.setStatusBarScrimColor(getResources().getColor(android.R.color.holo_orange_light));

//        setupToolbar();

//        Button bookHouseButton = (Button) findViewById(R.id.house_bookButton);
//        bookHouseButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                openBookHouse();
//            }
//        });
    }

    private void openBookHouse() {
        if (AccountProfile.getLoggedInAccountProfile(this) != null && house != null && house.getDetailUrl() != null && house.getBookingUrl() != null) {

            Intent intent = new Intent(this, BookActivity.class);
            intent.putExtra("house", house);
            startActivityForResult(intent, 501);

        } else {
            openSignInSignUp();
        }
    }

    private void openSignInSignUp() {
        Intent intent = new Intent(this, SignInSignUpActivity.class);
        startActivityForResult(intent, 301);
    }

    private void openHostActivity(String hostUrl) {
        Intent intent = new Intent(this, HostActivity.class);
        intent.putExtra("hostUrl", hostUrl);
        startActivity(intent);
    }
//
//    private void setupImageViewPager(String[] images) {
//
////        RelativeLayout viewPagerLayout = (RelativeLayout)findViewById(R.id.house_imageLayout);
//        ViewPager viewPager = (ViewPager) findViewById(R.id.house_imageViewpager);
//
//        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//        int height = display.getHeight();
//        viewPager.getLayoutParams().height = height / 2;
////        viewPagerLayout.getLayoutParams().height = height/2;
//
//        ImageViewPagerAdapter adapter = new ImageViewPagerAdapter(this, images);
//        viewPager.setAdapter(adapter);
//
//        CirclePageIndicator pageIndicator = (CirclePageIndicator) findViewById(R.id.house_imagePageIndicator);
//        pageIndicator.setViewPager(viewPager);
//    }
////
//    private void setupHostFab(String imageUrl) {
//
//        FloatingActionButton houseHostFab = (FloatingActionButton) findViewById(R.id.house_hostFab);
//        try {
//            int width = 0, height = 0;
//            if (houseHostFab != null) {
//                if (houseHostFab.getWidth() > 0 && houseHostFab.getHeight() > 0) {
//                    width = houseHostFab.getWidth();
//                    height = houseHostFab.getHeight();
//                } else if (houseHostFab.getLayoutParams() != null) {
//                    width = houseHostFab.getLayoutParams().width;
//                    height = houseHostFab.getLayoutParams().height;
//                }
//            }
//            ImageDownloaderAsyncTask imageDownloaderAsyncTask = new ImageDownloaderAsyncTask(this, imageUrl,
//                    width, height);
//            AsyncTask asyncTask = imageDownloaderAsyncTask.execute();
//
//            Bitmap hostImageBitmap = (Bitmap) asyncTask.get();
//            if (hostImageBitmap != null) {
//
//                Bitmap circleBitmap = Bitmap.createBitmap(hostImageBitmap.getWidth(), hostImageBitmap.getHeight(), Bitmap.Config.ARGB_8888);
//
//                BitmapShader shader = new BitmapShader(hostImageBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
//                Paint paint = new Paint();
//                paint.setShader(shader);
//                paint.setAntiAlias(true);
//                Canvas c = new Canvas(circleBitmap);
//                c.drawCircle(hostImageBitmap.getWidth() / 2, hostImageBitmap.getHeight() / 2, hostImageBitmap.getWidth() / 2, paint);
//
//                houseHostFab.setImageBitmap(hostImageBitmap);
//
////                houseHostFab.setRippleColor(getResources().getColor(android.R.color.holo_orange_dark));
////                houseHostFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_orange_dark)));
//            }
//
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//    }

    private void getHouse(String id) {
        llErrorContainer.setVisibility(View.GONE);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
//        showProgress();
//        url = url + "?filter[review]=5&filter[feature]=10000";

//        Gson gson = new GsonBuilder()
//                .registerTypeAdapter(House.class, new HouseDeserializer()).create();
        Gson gson = new GsonBuilder().create();

        Retrofit retrofit = RetrofitService.getInstance().getRetrofit();

        RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);

        Call<House> call = apiEndpoints.getHouse("https://api.narengi.xyz/v1/houses/" + id);
        call.enqueue(new Callback<House>() {
            @Override
            public void onResponse(Call<House> call, Response<House> response) {
//                int statusCode = response.code();
                hideProgress();
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    House house = response.body();
                    if (house != null) {

//                        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//                        int height = display.getHeight();
                        setHouse(house);
                    }
                } else {
//                    Toast.makeText(HouseActivity.this, R.string.error_alert_title, Toast.LENGTH_SHORT).show();
                    llErrorContainer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<House> call, Throwable t) {
                // Log error here since request failed
                t.printStackTrace();
                llErrorContainer.setVisibility(View.VISIBLE);
                progressDialog.dismiss();

//                progressDialog.dismiss();
                Log.d("HouseActivity", "getHouse onFailure : " + t.getMessage(), t);
//                hideProgress();
//                Toast.makeText(HouseActivity.this, R.string.error_alert_title, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onScrollChanged(mScrollView.getCurrentScrollY(), false, false);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        int baseColor = getResources().getColor(R.color.primary);
        float alpha = Math.min(1, (float) scrollY / mParallaxImageHeight);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));


        float alpha2 = Math.min(1, (float) scrollY / mParallaxImageHeight2);
        if (alpha2 >= 1) {
            toolbarTitle.setText(house.getName());
        } else {

            toolbarTitle.setText("");
        }
        ViewHelper.setTranslationY(header, scrollY / 2);
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }


    class PicturesPagerAdapter extends PagerAdapter {
        private String[] pictures;
        private Context context;

        public PicturesPagerAdapter(String[] pictures, Context context) {
            this.pictures = pictures;
            this.context = context;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView img = new ImageView(context);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            img.setLayoutParams(params);
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);

            try {
                Glide.with(context).load("https://api.narengi.xyz/v1/" + pictures[position]).into(img);
            } catch (Exception e) {
            }

            container.addView(img);
            return img;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return pictures.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    private void showDialog(){
        new BetaDialog(this).show();

    }
}
