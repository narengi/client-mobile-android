package xyz.narengi.android.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.byagowi.persiancalendar.Entity.Day;
import com.byagowi.persiancalendar.Utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import info.semsamot.actionbarrtlizer.ActionBarRtlizer;
import info.semsamot.actionbarrtlizer.RtlizeEverything;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import xyz.narengi.android.R;
import xyz.narengi.android.common.HouseEntryStep;
import xyz.narengi.android.common.dto.House;
import xyz.narengi.android.common.dto.HouseEntryInput;
import xyz.narengi.android.common.dto.HouseEntryPrice;
import xyz.narengi.android.common.dto.ImageInfo;
import xyz.narengi.android.common.dto.Location;
import xyz.narengi.android.service.RetrofitApiEndpoints;
import xyz.narengi.android.service.RetrofitService;
import xyz.narengi.android.ui.fragment.HouseDatesEntryFragment;
import xyz.narengi.android.ui.fragment.HouseEntryBaseFragment;
import xyz.narengi.android.ui.fragment.HouseFeaturesEntryFragment;
import xyz.narengi.android.ui.fragment.HouseGuestEntryFragment;
import xyz.narengi.android.ui.fragment.HouseImagesEntryFragment;
import xyz.narengi.android.ui.fragment.HouseInfoEntryFragment;
import xyz.narengi.android.ui.fragment.HouseMapEntryFragment;
import xyz.narengi.android.ui.fragment.HouseRoomEntryFragment;
import xyz.narengi.android.ui.fragment.HouseTypeEntryFragment;
import xyz.narengi.android.util.DateUtils;
import xyz.narengi.android.util.Util;

/**
 * @author Siavash Mahmoudpour
 */
public class AddHouseActivity extends AppCompatActivity implements HouseEntryBaseFragment.OnInteractionListener {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private ActionBarRtlizer rtlizer;
    private House house;
    private HouseEntryStep currentStep;
    private List<Uri> imageUris;
    private ImageInfo[] imageInfoArray;
    private Map<String, List<Day>> selectedDaysMap;
    private long requestMillis = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_house);
        currentStep = HouseEntryStep.HOUSE_INFO;
        setupToolbar();
        setupIndicatorsSize();

        house = new House();

        HouseEntryBaseFragment houseEntryFragment = new HouseInfoEntryFragment();
        initFirstFragment(houseEntryFragment);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.add_house_content, houseEntryFragment, "AddHouseContentFragment")
                .commit();

        updatePageTitle(R.string.house_info_entry_page_title);

        TextView indicatorTextView1 = (TextView) findViewById(R.id.add_house_indicator1);
        indicatorTextView1.setTextColor(getResources().getColor(android.R.color.white));
        indicatorTextView1.setBackgroundResource(R.drawable.circle_bg_orange);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            new AlertDialog.Builder(this)
                    .setTitle("دسترسی")
                    .setMessage("برای اضافه کردن خانه، برنامه نیاز به داشتن دسترسی به مکان‌یاب شما دارد")
                    .setPositiveButton("دادن دسترسی", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
                            requestMillis = System.currentTimeMillis();
                            ActivityCompat.requestPermissions(AddHouseActivity.this, permissions, 12);
                        }
                    })
                    .setNegativeButton("انصراف", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .create().show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        long now = System.currentTimeMillis();
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            if (now - requestMillis <= 100) {
                Util.startInstalledAppDetailsActivity(this);
            }
            finish();
        }
    }

    private void setPageTitle(String title) {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.add_house_toolbar);
        if (toolbar != null) {
            TextView titleTextView = (TextView) toolbar.findViewById(R.id.text_toolbar_title);
            titleTextView.setText(title);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
//        backToPreviousSection(readCurrentFragmentHouse());
        if (house == null || house.getDetailUrl() == null) {
            super.onBackPressed();
        } else {
            setResult(2001);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 2001) {
            setResult(2001);
            finish();
        }
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


    private void addHouse() {

        //TODO : user test1003@test.com has more than 224 houses. use it for list test.
        HouseEntryInput houseEntryInput = getHouseEntryInput();
        if (houseEntryInput == null)
            return;

        Retrofit retrofit = RetrofitService.getInstance().getRetrofit();

        RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);
        Call<House> call = apiEndpoints.addHouse(houseEntryInput);

        call.enqueue(new Callback<House>() {
            @Override
            public void onResponse(Call<House> call, Response<House> response) {
                hideProgress();
                int statusCode = response.code();
                House resultHouse = response.body();
                if (resultHouse == null) {
                    try {
                        if (response.errorBody() != null) {
                            Toast.makeText(AddHouseActivity.this, response.errorBody().string(), Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    house = resultHouse;
                    showUpdateHouseResultDialog();
                }
            }

            @Override
            public void onFailure(Call<House> call, Throwable t) {
                hideProgress();
                Toast.makeText(AddHouseActivity.this, "Exception : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private int getScreenWidth(Context context) {
        int measuredWidth;
        Point size = new Point();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            wm.getDefaultDisplay().getSize(size);
            measuredWidth = size.x;
        } else {
            Display d = wm.getDefaultDisplay();
            measuredWidth = d.getHeight();
        }

        return measuredWidth;
    }

    public void showUpdateHouseResultDialog() {

        Toast toast = new Toast(getApplicationContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_sign_up_success, null);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params != null) {
            int width = (getScreenWidth(this) * 3 / 5);
            params.width = width;
            params.height = width;
            view.setLayoutParams(params);
        }

        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);

        int margin = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        toast.setGravity(Gravity.CENTER, 0, margin);
        toast.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                goToNextSection();
            }
        }, 1000);
    }

    private void updateHouse() {
        HouseEntryInput houseEntryInput = getHouseEntryInput();
        if (houseEntryInput == null)
            return;

        Retrofit retrofit = RetrofitService.getInstance().getRetrofit();

        RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);
        Call<House> call = apiEndpoints.updateHouse(house.getDetailUrl(), houseEntryInput);

        call.enqueue(new Callback<House>() {
            @Override
            public void onResponse(Call<House> call, Response<House> response) {
                int statusCode = response.code();
                House resultHouse = response.body();
                if (resultHouse == null) {
                    try {
                        if (response.errorBody() != null) {
                            Toast.makeText(AddHouseActivity.this, response.errorBody().string(), Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    house = resultHouse;
                    showUpdateHouseResultDialog();
                }

            }

            @Override
            public void onFailure(Call<House> call, Throwable t) {
                hideProgress();
                Toast.makeText(AddHouseActivity.this, "Exception : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void addUpdateHouse() {

        if (currentStep == HouseEntryStep.HOUSE_INFO) {
            if (house == null || house.getDetailUrl() == null || house.getDetailUrl().length() == 0)
                addHouse();
            else updateHouse();
        } else if (currentStep == HouseEntryStep.HOUSE_IMAGES) {
            hideProgress();
            showUpdateHouseResultDialog();
        } else {
            updateHouse();
        }
    }

    private HouseEntryInput getHouseEntryInput() {
        if (house == null)
            return null;

        HouseEntryInput houseEntryInput = new HouseEntryInput();
        houseEntryInput.setName(house.getName());
        if (house.getPrice() != null) {
            HouseEntryPrice price = new HouseEntryPrice();
            price.setPrice(house.getPrice().getPrice());
            price.setExtraGuestPrice(house.getPrice().getExtraGuestPrice());
            houseEntryInput.setPrice(price);
        }

        houseEntryInput.setPosition(house.getPosition());
        houseEntryInput.setSummary(house.getSummary());

        if (house.getProvinceName() != null || house.getCityName() != null) {
            Location location = new Location();
            location.setProvince(house.getProvinceName());
            location.setCity(house.getCityName());
            houseEntryInput.setLocation(location);
        }
        houseEntryInput.setType(house.getType());
        houseEntryInput.setSpec(house.getSpec());
        houseEntryInput.setAvailableDates(getSelectedDates());
        houseEntryInput.setFeatureList(house.getFeatureList());

        return houseEntryInput;
    }

    private String[] getSelectedDates() {
        if (selectedDaysMap == null || selectedDaysMap.size() == 0)
            return null;

        List<String> selectedDates = new ArrayList<String>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        for (Map.Entry<String, List<Day>> mapEntry : selectedDaysMap.entrySet()) {
            List<Day> selectedDays = mapEntry.getValue();
            if (selectedDays == null)
                continue;
            for (Day day : selectedDays) {
                Date date = DateUtils.getInstance(this).getDateOfDay(day);
                if (date != null) {
                    selectedDates.add(dateFormat.format(date));
                }
            }
        }

        String[] datesArray = new String[selectedDates.size()];
        selectedDates.toArray(datesArray);

        return datesArray;
    }

    private void setupToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.add_house_toolbar);

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

//            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.add_house_collapse_toolbar);
//            collapsingToolbarLayout.setTitle("");
//            collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        }
    }

    private void showProgress() {
        LinearLayout progressBarLayout = (LinearLayout) findViewById(R.id.add_house_progressLayout);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.add_house_progressBar);

        if (progressBar != null && progressBarLayout != null) {
            progressBar.setVisibility(View.VISIBLE);
            progressBarLayout.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgress() {
        LinearLayout progressBarLayout = (LinearLayout) findViewById(R.id.add_house_progressLayout);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.add_house_progressBar);

        if (progressBar != null && progressBarLayout != null) {
            progressBar.setVisibility(View.GONE);
            progressBarLayout.setVisibility(View.GONE);
        }
    }

    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        NestedScrollView scrollView = (NestedScrollView) findViewById(R.id.add_house_scrollview);
        if (scrollView != null)
            scrollView.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    private void goToNextSection() {

        switch (currentStep) {
            case HOUSE_INFO:
                currentStep = HouseEntryStep.HOUSE_MAP;
                goToMapSection();
                break;
            case HOUSE_MAP:
                currentStep = HouseEntryStep.HOUSE_TYPE;
                goToHouseTypeSection();
                break;
            case HOUSE_TYPE:
                currentStep = HouseEntryStep.HOUSE_ROOMS;
                goToHouseRoomSection();
                break;
            case HOUSE_ROOMS:
                currentStep = HouseEntryStep.HOUSE_GUESTS;
                goToGuestsSection();
                break;
            case HOUSE_GUESTS:
                currentStep = HouseEntryStep.HOUSE_FEATURES;
                goToFeaturesSection();
                break;
            case HOUSE_FEATURES:
                currentStep = HouseEntryStep.HOUSE_IMAGES;
                goToImagesSection();
                break;
            case HOUSE_IMAGES:
                currentStep = HouseEntryStep.HOUSE_DATES;
                goToDatesSection();
                break;
            case HOUSE_DATES:
                openAddHouseConfirm();
                break;
        }

        if (currentStep != HouseEntryStep.HOUSE_MAP && currentStep != HouseEntryStep.HOUSE_FEATURES)
            requestDisallowInterceptTouchEvent(false);
    }

    private void backToPreviousSection(House house) {
        this.house = house;
        switch (currentStep) {
            case HOUSE_INFO:
                currentStep = null;
                this.house = null;
                finish();
                //save house data.
                //make server call.
                //replace fragment.
                //change title
                //update indicators
                break;
            case HOUSE_MAP:
                currentStep = HouseEntryStep.HOUSE_INFO;
                backToInfoSection();
                break;
            case HOUSE_TYPE:
                currentStep = HouseEntryStep.HOUSE_MAP;
                backToMapSection();
//                NestedScrollView scrollView = (NestedScrollView)findViewById(R.id.add_house_scrollview);
//                if (scrollView != null)
//                    scrollView.requestDisallowInterceptTouchEvent(true);
                break;
            case HOUSE_ROOMS:
                currentStep = HouseEntryStep.HOUSE_TYPE;
                backToHouseTypeSection();
                break;
            case HOUSE_GUESTS:
                currentStep = HouseEntryStep.HOUSE_ROOMS;
                backToRoomSection();
                break;
            case HOUSE_FEATURES:
                currentStep = HouseEntryStep.HOUSE_GUESTS;
                backToGuestsSection();
                break;
            case HOUSE_IMAGES:
                currentStep = HouseEntryStep.HOUSE_FEATURES;
                backToFeaturesSection();
                break;
            case HOUSE_DATES:
                currentStep = HouseEntryStep.HOUSE_IMAGES;
                backToImagesSection();
                break;
//            default:
//                scrollView = (NestedScrollView)findViewById(R.id.add_house_scrollview);
//                if (scrollView != null)
//                    scrollView.requestDisallowInterceptTouchEvent(false);
//                break;
        }
        if (currentStep != HouseEntryStep.HOUSE_MAP && currentStep != HouseEntryStep.HOUSE_FEATURES)
            requestDisallowInterceptTouchEvent(false);
    }

    private House readCurrentFragmentHouse() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.add_house_content);
        if (currentFragment != null && currentFragment instanceof HouseEntryBaseFragment) {
            house = ((HouseEntryBaseFragment) currentFragment).getHouse();
        }
        return house;
    }

    private void initFirstFragment(HouseEntryBaseFragment houseEntryFragment) {
        houseEntryFragment.setEntryMode(HouseEntryBaseFragment.EntryMode.ADD);
        houseEntryFragment.setOnInteractionListener(this);
    }

    private void initNextFragment(HouseEntryBaseFragment houseEntryFragment) {
        houseEntryFragment.setHouse(house);
        houseEntryFragment.setEntryMode(HouseEntryBaseFragment.EntryMode.ADD);
        houseEntryFragment.setOnInteractionListener(this);
    }

    private void updatePageTitle(int titleResId) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
//            actionBar.setTitle(getString(titleResId));
//            actionBar.setWindowTitle(getString(titleResId));

            String title = getString(titleResId);
            setPageTitle(title);

//            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.add_house_collapse_toolbar);
//            if (collapsingToolbarLayout != null) {
//                collapsingToolbarLayout.setTitle(getString(R.string.house_info_entry_page_title));
//            }
        }
    }

    private void backToInfoSection() {
        HouseEntryBaseFragment houseEntryFragment = new HouseInfoEntryFragment();
        initNextFragment(houseEntryFragment);

        readCurrentFragmentHouse();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.add_house_content, houseEntryFragment, "AddHouseContentFragment")
                .commit();

        updatePageTitle(R.string.house_info_entry_page_title);

        TextView indicatorTextView1 = (TextView) findViewById(R.id.add_house_indicator1);
        if (indicatorTextView1 != null) {
            indicatorTextView1.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView1.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_orange));
        }

        TextView indicatorTextView2 = (TextView) findViewById(R.id.add_house_indicator2);
        if (indicatorTextView2 != null) {
            indicatorTextView2.setTextColor(getResources().getColor(R.color.text_gray_dark));
            indicatorTextView2.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_gray_light));
        }
    }

    private void goToMapSection() {
        HouseEntryBaseFragment houseEntryFragment = new HouseMapEntryFragment();
        initNextFragment(houseEntryFragment);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.add_house_content, houseEntryFragment, "AddHouseContentFragment")
                .commit();

        updatePageTitle(R.string.house_map_entry_page_title);

        TextView indicatorTextView1 = (TextView) findViewById(R.id.add_house_indicator1);
        if (indicatorTextView1 != null) {
            indicatorTextView1.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView1.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView2 = (TextView) findViewById(R.id.add_house_indicator2);
        if (indicatorTextView2 != null) {
            indicatorTextView2.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView2.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_orange));
        }
    }

    private void goToHouseTypeSection() {
        HouseEntryBaseFragment houseEntryFragment = new HouseTypeEntryFragment();
        initNextFragment(houseEntryFragment);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.add_house_content, houseEntryFragment, "AddHouseContentFragment")
                .commit();

        updatePageTitle(R.string.house_type_entry_page_title);

        TextView indicatorTextView1 = (TextView) findViewById(R.id.add_house_indicator1);
        if (indicatorTextView1 != null) {
            indicatorTextView1.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView1.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView2 = (TextView) findViewById(R.id.add_house_indicator2);
        if (indicatorTextView2 != null) {
            indicatorTextView2.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView2.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView3 = (TextView) findViewById(R.id.add_house_indicator3);
        if (indicatorTextView3 != null) {
            indicatorTextView3.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView3.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_orange));
        }
    }

    private void goToHouseRoomSection() {
        HouseEntryBaseFragment houseEntryFragment = new HouseRoomEntryFragment();
        initNextFragment(houseEntryFragment);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.add_house_content, houseEntryFragment, "AddHouseContentFragment")
                .commit();

        updatePageTitle(R.string.house_room_entry_page_title);

        TextView indicatorTextView1 = (TextView) findViewById(R.id.add_house_indicator1);
        if (indicatorTextView1 != null) {
            indicatorTextView1.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView1.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView2 = (TextView) findViewById(R.id.add_house_indicator2);
        if (indicatorTextView2 != null) {
            indicatorTextView2.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView2.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView3 = (TextView) findViewById(R.id.add_house_indicator3);
        if (indicatorTextView3 != null) {
            indicatorTextView3.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView3.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView4 = (TextView) findViewById(R.id.add_house_indicator4);
        if (indicatorTextView4 != null) {
            indicatorTextView4.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView4.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_orange));
        }
    }

    private void goToGuestsSection() {
        HouseEntryBaseFragment houseEntryFragment = new HouseGuestEntryFragment();
        initNextFragment(houseEntryFragment);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.add_house_content, houseEntryFragment, "AddHouseContentFragment")
                .commit();

        updatePageTitle(R.string.house_guest_entry_page_title);

        TextView indicatorTextView1 = (TextView) findViewById(R.id.add_house_indicator1);
        if (indicatorTextView1 != null) {
            indicatorTextView1.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView1.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView2 = (TextView) findViewById(R.id.add_house_indicator2);
        if (indicatorTextView2 != null) {
            indicatorTextView2.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView2.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView3 = (TextView) findViewById(R.id.add_house_indicator3);
        if (indicatorTextView3 != null) {
            indicatorTextView3.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView3.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView4 = (TextView) findViewById(R.id.add_house_indicator4);
        if (indicatorTextView4 != null) {
            indicatorTextView4.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView4.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView5 = (TextView) findViewById(R.id.add_house_indicator5);
        if (indicatorTextView5 != null) {
            indicatorTextView5.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView5.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_orange));
        }
    }

    private void goToFeaturesSection() {
        HouseEntryBaseFragment houseEntryFragment = new HouseFeaturesEntryFragment();
        initNextFragment(houseEntryFragment);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.add_house_content, houseEntryFragment, "AddHouseContentFragment")
                .commit();

        updatePageTitle(R.string.house_features_entry_page_title);

        TextView indicatorTextView1 = (TextView) findViewById(R.id.add_house_indicator1);
        if (indicatorTextView1 != null) {
            indicatorTextView1.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView1.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView2 = (TextView) findViewById(R.id.add_house_indicator2);
        if (indicatorTextView2 != null) {
            indicatorTextView2.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView2.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView3 = (TextView) findViewById(R.id.add_house_indicator3);
        if (indicatorTextView3 != null) {
            indicatorTextView3.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView3.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView4 = (TextView) findViewById(R.id.add_house_indicator4);
        if (indicatorTextView4 != null) {
            indicatorTextView4.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView4.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView5 = (TextView) findViewById(R.id.add_house_indicator5);
        if (indicatorTextView5 != null) {
            indicatorTextView5.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView5.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView6 = (TextView) findViewById(R.id.add_house_indicator6);
        if (indicatorTextView6 != null) {
            indicatorTextView6.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView6.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_orange));
        }
    }

    private void goToImagesSection() {
        HouseEntryBaseFragment houseEntryFragment = new HouseImagesEntryFragment();
        initNextFragment(houseEntryFragment);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.add_house_content, houseEntryFragment, "AddHouseContentFragment")
                .commit();

        updatePageTitle(R.string.house_images_entry_page_title);

        TextView indicatorTextView1 = (TextView) findViewById(R.id.add_house_indicator1);
        if (indicatorTextView1 != null) {
            indicatorTextView1.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView1.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView2 = (TextView) findViewById(R.id.add_house_indicator2);
        if (indicatorTextView2 != null) {
            indicatorTextView2.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView2.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView3 = (TextView) findViewById(R.id.add_house_indicator3);
        if (indicatorTextView3 != null) {
            indicatorTextView3.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView3.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView4 = (TextView) findViewById(R.id.add_house_indicator4);
        if (indicatorTextView4 != null) {
            indicatorTextView4.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView4.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView5 = (TextView) findViewById(R.id.add_house_indicator5);
        if (indicatorTextView5 != null) {
            indicatorTextView5.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView5.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView6 = (TextView) findViewById(R.id.add_house_indicator6);
        if (indicatorTextView6 != null) {
            indicatorTextView6.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView6.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView7 = (TextView) findViewById(R.id.add_house_indicator7);
        if (indicatorTextView7 != null) {
            indicatorTextView7.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView7.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_orange));
        }
    }

    private void goToDatesSection() {
        HouseEntryBaseFragment houseEntryFragment = new HouseDatesEntryFragment();
        initNextFragment(houseEntryFragment);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.add_house_content, houseEntryFragment, "AddHouseContentFragment")
                .commit();

        updatePageTitle(R.string.house_dates_entry_page_title);

        TextView indicatorTextView1 = (TextView) findViewById(R.id.add_house_indicator1);
        if (indicatorTextView1 != null) {
            indicatorTextView1.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView1.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView2 = (TextView) findViewById(R.id.add_house_indicator2);
        if (indicatorTextView2 != null) {
            indicatorTextView2.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView2.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView3 = (TextView) findViewById(R.id.add_house_indicator3);
        if (indicatorTextView3 != null) {
            indicatorTextView3.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView3.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView4 = (TextView) findViewById(R.id.add_house_indicator4);
        if (indicatorTextView4 != null) {
            indicatorTextView4.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView4.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView5 = (TextView) findViewById(R.id.add_house_indicator5);
        if (indicatorTextView5 != null) {
            indicatorTextView5.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView5.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView6 = (TextView) findViewById(R.id.add_house_indicator6);
        if (indicatorTextView6 != null) {
            indicatorTextView6.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView6.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView7 = (TextView) findViewById(R.id.add_house_indicator7);
        if (indicatorTextView7 != null) {
            indicatorTextView7.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView7.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView8 = (TextView) findViewById(R.id.add_house_indicator8);
        if (indicatorTextView8 != null) {
            indicatorTextView8.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView8.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_orange));
        }
    }

    private void backToMapSection() {
        HouseEntryBaseFragment houseEntryFragment = new HouseMapEntryFragment();
        initNextFragment(houseEntryFragment);

        readCurrentFragmentHouse();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.add_house_content, houseEntryFragment, "AddHouseContentFragment")
                .commit();

        updatePageTitle(R.string.house_map_entry_page_title);

        TextView indicatorTextView1 = (TextView) findViewById(R.id.add_house_indicator1);
        if (indicatorTextView1 != null) {
            indicatorTextView1.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView1.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView2 = (TextView) findViewById(R.id.add_house_indicator2);
        if (indicatorTextView2 != null) {
            indicatorTextView2.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView2.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_orange));
        }

        TextView indicatorTextView3 = (TextView) findViewById(R.id.add_house_indicator3);
        if (indicatorTextView3 != null) {
            indicatorTextView3.setTextColor(getResources().getColor(R.color.text_gray_dark));
            indicatorTextView3.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_gray_light));
        }
    }

    private void backToHouseTypeSection() {
        HouseEntryBaseFragment houseEntryFragment = new HouseTypeEntryFragment();
        initNextFragment(houseEntryFragment);

        readCurrentFragmentHouse();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.add_house_content, houseEntryFragment, "AddHouseContentFragment")
                .commit();

        updatePageTitle(R.string.house_type_entry_page_title);

        TextView indicatorTextView1 = (TextView) findViewById(R.id.add_house_indicator1);
        if (indicatorTextView1 != null) {
            indicatorTextView1.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView1.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView2 = (TextView) findViewById(R.id.add_house_indicator2);
        if (indicatorTextView2 != null) {
            indicatorTextView2.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView2.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView3 = (TextView) findViewById(R.id.add_house_indicator3);
        if (indicatorTextView3 != null) {
            indicatorTextView3.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView3.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_orange));
        }

        TextView indicatorTextView4 = (TextView) findViewById(R.id.add_house_indicator4);
        if (indicatorTextView4 != null) {
            indicatorTextView4.setTextColor(getResources().getColor(R.color.text_gray_dark));
            indicatorTextView4.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_gray_light));
        }
    }

    private void backToRoomSection() {
        HouseEntryBaseFragment houseEntryFragment = new HouseRoomEntryFragment();
        initNextFragment(houseEntryFragment);

        readCurrentFragmentHouse();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.add_house_content, houseEntryFragment, "AddHouseContentFragment")
                .commit();

        updatePageTitle(R.string.house_room_entry_page_title);

        TextView indicatorTextView1 = (TextView) findViewById(R.id.add_house_indicator1);
        if (indicatorTextView1 != null) {
            indicatorTextView1.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView1.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView2 = (TextView) findViewById(R.id.add_house_indicator2);
        if (indicatorTextView2 != null) {
            indicatorTextView2.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView2.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView3 = (TextView) findViewById(R.id.add_house_indicator3);
        if (indicatorTextView3 != null) {
            indicatorTextView3.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView3.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView4 = (TextView) findViewById(R.id.add_house_indicator4);
        if (indicatorTextView4 != null) {
            indicatorTextView4.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView4.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_orange));
        }

        TextView indicatorTextView5 = (TextView) findViewById(R.id.add_house_indicator5);
        if (indicatorTextView5 != null) {
            indicatorTextView5.setTextColor(getResources().getColor(R.color.text_gray_dark));
            indicatorTextView5.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_gray_light));
        }
    }

    private void backToGuestsSection() {
        HouseEntryBaseFragment houseEntryFragment = new HouseGuestEntryFragment();
        initNextFragment(houseEntryFragment);

        readCurrentFragmentHouse();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.add_house_content, houseEntryFragment, "AddHouseContentFragment")
                .commit();

        updatePageTitle(R.string.house_guest_entry_page_title);

        TextView indicatorTextView1 = (TextView) findViewById(R.id.add_house_indicator1);
        if (indicatorTextView1 != null) {
            indicatorTextView1.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView1.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView2 = (TextView) findViewById(R.id.add_house_indicator2);
        if (indicatorTextView2 != null) {
            indicatorTextView2.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView2.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView3 = (TextView) findViewById(R.id.add_house_indicator3);
        if (indicatorTextView3 != null) {
            indicatorTextView3.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView3.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView4 = (TextView) findViewById(R.id.add_house_indicator4);
        if (indicatorTextView4 != null) {
            indicatorTextView4.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView4.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView5 = (TextView) findViewById(R.id.add_house_indicator5);
        if (indicatorTextView5 != null) {
            indicatorTextView5.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView5.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_orange));
        }

        TextView indicatorTextView6 = (TextView) findViewById(R.id.add_house_indicator6);
        if (indicatorTextView6 != null) {
            indicatorTextView6.setTextColor(getResources().getColor(R.color.text_gray_dark));
            indicatorTextView6.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_gray_light));
        }
    }

    private void backToFeaturesSection() {
        HouseEntryBaseFragment houseEntryFragment = new HouseFeaturesEntryFragment();
        initNextFragment(houseEntryFragment);

        readCurrentFragmentHouse();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.add_house_content, houseEntryFragment, "AddHouseContentFragment")
                .commit();

        updatePageTitle(R.string.house_features_entry_page_title);

        TextView indicatorTextView1 = (TextView) findViewById(R.id.add_house_indicator1);
        if (indicatorTextView1 != null) {
            indicatorTextView1.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView1.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView2 = (TextView) findViewById(R.id.add_house_indicator2);
        if (indicatorTextView2 != null) {
            indicatorTextView2.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView2.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView3 = (TextView) findViewById(R.id.add_house_indicator3);
        if (indicatorTextView3 != null) {
            indicatorTextView3.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView3.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView4 = (TextView) findViewById(R.id.add_house_indicator4);
        if (indicatorTextView4 != null) {
            indicatorTextView4.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView4.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView5 = (TextView) findViewById(R.id.add_house_indicator5);
        if (indicatorTextView5 != null) {
            indicatorTextView5.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView5.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView6 = (TextView) findViewById(R.id.add_house_indicator6);
        if (indicatorTextView6 != null) {
            indicatorTextView6.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView6.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_orange));
        }

        TextView indicatorTextView7 = (TextView) findViewById(R.id.add_house_indicator7);
        if (indicatorTextView7 != null) {
            indicatorTextView7.setTextColor(getResources().getColor(R.color.text_gray_dark));
            indicatorTextView7.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_gray_light));
        }
    }

    private void backToImagesSection() {
        HouseEntryBaseFragment houseEntryFragment = new HouseImagesEntryFragment();
        initNextFragment(houseEntryFragment);

        readCurrentFragmentHouse();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.add_house_content, houseEntryFragment, "AddHouseContentFragment")
                .commit();

        updatePageTitle(R.string.house_images_entry_page_title);

        TextView indicatorTextView1 = (TextView) findViewById(R.id.add_house_indicator1);
        if (indicatorTextView1 != null) {
            indicatorTextView1.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView1.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView2 = (TextView) findViewById(R.id.add_house_indicator2);
        if (indicatorTextView2 != null) {
            indicatorTextView2.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView2.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView3 = (TextView) findViewById(R.id.add_house_indicator3);
        if (indicatorTextView3 != null) {
            indicatorTextView3.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView3.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView4 = (TextView) findViewById(R.id.add_house_indicator4);
        if (indicatorTextView4 != null) {
            indicatorTextView4.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView4.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView5 = (TextView) findViewById(R.id.add_house_indicator5);
        if (indicatorTextView5 != null) {
            indicatorTextView5.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView5.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView6 = (TextView) findViewById(R.id.add_house_indicator6);
        if (indicatorTextView6 != null) {
            indicatorTextView6.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView6.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green));
        }

        TextView indicatorTextView7 = (TextView) findViewById(R.id.add_house_indicator7);
        if (indicatorTextView7 != null) {
            indicatorTextView7.setTextColor(getResources().getColor(android.R.color.white));
            indicatorTextView7.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_orange));
        }

        TextView indicatorTextView8 = (TextView) findViewById(R.id.add_house_indicator8);
        if (indicatorTextView8 != null) {
            indicatorTextView8.setTextColor(getResources().getColor(R.color.text_gray_dark));
            indicatorTextView8.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_gray_light));
        }
    }

    @Override
    public void onGoToNextSection(House house) {
        this.house = house;
        showProgress();
        addUpdateHouse();
    }

    @Override
    public void onBackToPreviousSection(House house) {
        backToPreviousSection(house);
    }

    private void openAddHouseConfirm() {
        Intent intent = new Intent(this, AddHouseConfirmActivity.class);
        startActivityForResult(intent, 2001);
    }

    private void setupIndicatorsSize() {

        char[] digits = Utils.getInstance().preferredDigits(this);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        float indicatorWidth = (dpWidth - 8 * 8 - 2 * 8) / 8;
        indicatorWidth = indicatorWidth * displayMetrics.density;
        float indicatorHeight = indicatorWidth;

        int margin = 8;
        int actionBarHeight;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            if (actionBarHeight > 0) {
                indicatorWidth = actionBarHeight / 2;
                indicatorHeight = actionBarHeight / 2;
                margin = (displayMetrics.widthPixels - actionBarHeight * 4) / 16;
            }
        }

        TextView indicatorTextView1 = (TextView) findViewById(R.id.add_house_indicator1);
        if (indicatorTextView1 != null && indicatorTextView1.getLayoutParams() != null) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) indicatorTextView1.getLayoutParams();
            params.width = (int) indicatorWidth;
            params.height = (int) indicatorHeight;
            params.gravity = Gravity.CENTER;
            params.setMargins(margin, margin, margin, margin);
            indicatorTextView1.setLayoutParams(params);
            indicatorTextView1.setGravity(Gravity.CENTER);
            indicatorTextView1.setText(Utils.formatNumber(1, digits));
        }

        TextView indicatorTextView2 = (TextView) findViewById(R.id.add_house_indicator2);
        if (indicatorTextView2 != null && indicatorTextView2.getLayoutParams() != null) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) indicatorTextView2.getLayoutParams();
            params.width = (int) indicatorWidth;
            params.height = (int) indicatorHeight;
            params.gravity = Gravity.CENTER;
            params.setMargins(margin, margin, margin, margin);
            indicatorTextView2.setLayoutParams(params);
            indicatorTextView2.setGravity(Gravity.CENTER);
            indicatorTextView2.setText(Utils.formatNumber(2, digits));
        }

        TextView indicatorTextView3 = (TextView) findViewById(R.id.add_house_indicator3);
        if (indicatorTextView3 != null && indicatorTextView3.getLayoutParams() != null) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) indicatorTextView3.getLayoutParams();
            params.width = (int) indicatorWidth;
            params.height = (int) indicatorHeight;
            params.gravity = Gravity.CENTER;
            params.setMargins(margin, margin, margin, margin);
            indicatorTextView3.setLayoutParams(params);
            indicatorTextView3.setGravity(Gravity.CENTER);
            indicatorTextView3.setText(Utils.formatNumber(3, digits));
        }

        TextView indicatorTextView4 = (TextView) findViewById(R.id.add_house_indicator4);
        if (indicatorTextView4 != null && indicatorTextView4.getLayoutParams() != null) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) indicatorTextView4.getLayoutParams();
            params.width = (int) indicatorWidth;
            params.height = (int) indicatorHeight;
            params.gravity = Gravity.CENTER;
            params.setMargins(margin, margin, margin, margin);
            indicatorTextView4.setLayoutParams(params);
            indicatorTextView4.setGravity(Gravity.CENTER);
            indicatorTextView4.setText(Utils.formatNumber(4, digits));
        }

        TextView indicatorTextView5 = (TextView) findViewById(R.id.add_house_indicator5);
        if (indicatorTextView5 != null && indicatorTextView5.getLayoutParams() != null) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) indicatorTextView5.getLayoutParams();
            params.width = (int) indicatorWidth;
            params.height = (int) indicatorHeight;
            params.gravity = Gravity.CENTER;
            params.setMargins(margin, margin, margin, margin);
            indicatorTextView5.setLayoutParams(params);
            indicatorTextView5.setGravity(Gravity.CENTER);
            indicatorTextView5.setText(Utils.formatNumber(5, digits));
        }

        TextView indicatorTextView6 = (TextView) findViewById(R.id.add_house_indicator6);
        if (indicatorTextView6 != null && indicatorTextView6.getLayoutParams() != null) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) indicatorTextView6.getLayoutParams();
            params.width = (int) indicatorWidth;
            params.height = (int) indicatorHeight;
            params.gravity = Gravity.CENTER;
            params.setMargins(margin, margin, margin, margin);
            indicatorTextView6.setLayoutParams(params);
            indicatorTextView6.setGravity(Gravity.CENTER);
            indicatorTextView6.setText(Utils.formatNumber(6, digits));
        }

        TextView indicatorTextView7 = (TextView) findViewById(R.id.add_house_indicator7);
        if (indicatorTextView7 != null && indicatorTextView7.getLayoutParams() != null) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) indicatorTextView7.getLayoutParams();
            params.width = (int) indicatorWidth;
            params.height = (int) indicatorHeight;
            params.gravity = Gravity.CENTER;
            params.setMargins(margin, margin, margin, margin);
            indicatorTextView7.setLayoutParams(params);
            indicatorTextView7.setGravity(Gravity.CENTER);
            indicatorTextView7.setText(Utils.formatNumber(7, digits));
        }

        TextView indicatorTextView8 = (TextView) findViewById(R.id.add_house_indicator8);
        if (indicatorTextView8 != null && indicatorTextView8.getLayoutParams() != null) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) indicatorTextView8.getLayoutParams();
            params.width = (int) indicatorWidth;
            params.height = (int) indicatorHeight;
            params.gravity = Gravity.CENTER;
            params.setMargins(margin, margin, margin, margin);
            indicatorTextView8.setLayoutParams(params);
            indicatorTextView8.setGravity(Gravity.CENTER);
            indicatorTextView8.setText(Utils.formatNumber(8, digits));
        }
    }

    public List<Uri> getImageUris() {
        return imageUris;
    }

    public void setImageUris(List<Uri> imageUris) {
        this.imageUris = imageUris;
    }

    public Map<String, List<Day>> getSelectedDaysMap() {
        return selectedDaysMap;
    }

    public void setSelectedDaysMap(Map<String, List<Day>> selectedDaysMap) {
        this.selectedDaysMap = selectedDaysMap;
    }

    public ImageInfo[] getImageInfoArray() {
        return imageInfoArray;
    }

    public void setImageInfoArray(ImageInfo[] imageInfoArray) {
        this.imageInfoArray = imageInfoArray;
    }
}
