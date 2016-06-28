package xyz.narengi.android.ui.activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.byagowi.persiancalendar.Entity.Day;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ir.smartlab.persindatepicker.util.PersianCalendar;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.narengi.android.R;
import xyz.narengi.android.common.Constants;
import xyz.narengi.android.common.dto.Authorization;
import xyz.narengi.android.common.dto.Credential;
import xyz.narengi.android.common.dto.House;
import xyz.narengi.android.common.dto.HouseEntryInput;
import xyz.narengi.android.common.dto.HouseEntryPrice;
import xyz.narengi.android.common.dto.ImageInfo;
import xyz.narengi.android.common.dto.Location;
import xyz.narengi.android.content.CredentialDeserializer;
import xyz.narengi.android.service.RetrofitApiEndpoints;
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
import xyz.narengi.android.util.SecurityUtils;

public class AddHouseActivity extends AppCompatActivity implements HouseEntryBaseFragment.OnInteractionListener {

    private House house;
    private HouseEntryStep currentStep;
    private List<Uri> imageUris;
    private ImageInfo[] imageInfoArray;
    private Map<String,List<Day>> selectedDaysMap;

    public enum HouseEntryStep {
        HOUSE_INFO,
        HOUSE_MAP,
        HOUSE_TYPE,
        HOUSE_ROOMS,
        HOUSE_GUESTS,
        HOUSE_FEATURES,
        HOUSE_IMAGES,
        HOUSE_DATES
    }

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

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
        indicatorTextView1.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_orange));
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
        if (house == null || house.getURL() == null) {
            super.onBackPressed();
        } else {
            setResult(2001);
            finish();
        }
    }

    private void addHouse() {

        //TODO : user test1003@test.com has more than 224 houses. use it for list test.
        HouseEntryInput houseEntryInput = getHouseEntryInput();
        if (houseEntryInput == null)
            return;


        final SharedPreferences preferences = getSharedPreferences("profile", 0);
        String accessToken = preferences.getString("accessToken", "");
        String username = preferences.getString("username", "");

        Authorization authorization = new Authorization();
        authorization.setUsername(username);
        authorization.setToken(accessToken);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Credential.class, new CredentialDeserializer())
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .create();

        String authorizationJson = gson.toJson(authorization);
        if (authorizationJson != null) {
            authorizationJson = authorizationJson.replace("{", "");
            authorizationJson = authorizationJson.replace("}", "");
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.SERVER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);
        Call<House> call = apiEndpoints.addHouse(authorizationJson, houseEntryInput);

        call.enqueue(new Callback<House>() {
            @Override
            public void onResponse(Response<House> response, Retrofit retrofit) {
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
                    Toast.makeText(AddHouseActivity.this, "Added House : " + resultHouse.getName(), Toast.LENGTH_SHORT).show();
                    goToNextSection();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                hideProgress();
                Toast.makeText(AddHouseActivity.this, "Exception : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void updateHouse() {
        HouseEntryInput houseEntryInput = getHouseEntryInput();
        if (houseEntryInput == null)
            return;


        final SharedPreferences preferences = getSharedPreferences("profile", 0);
        String accessToken = preferences.getString("accessToken", "");
        String username = preferences.getString("username", "");

        Authorization authorization = new Authorization();
        authorization.setUsername(username);
        authorization.setToken(accessToken);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Credential.class, new CredentialDeserializer())
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .create();

        String authorizationJson = gson.toJson(authorization);
        if (authorizationJson != null) {
            authorizationJson = authorizationJson.replace("{", "");
            authorizationJson = authorizationJson.replace("}", "");
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.SERVER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);
        Call<House> call = apiEndpoints.updateHouse(authorizationJson, house.getURL(), houseEntryInput);

        call.enqueue(new Callback<House>() {
            @Override
            public void onResponse(Response<House> response, Retrofit retrofit) {
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
                    Toast.makeText(AddHouseActivity.this, "Added House : " + resultHouse.getName(), Toast.LENGTH_SHORT).show();
                    goToNextSection();
                }

            }

            @Override
            public void onFailure(Throwable t) {
                hideProgress();
                Toast.makeText(AddHouseActivity.this, "Exception : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void addUpdateHouse() {

        if (currentStep == HouseEntryStep.HOUSE_INFO) {
            addHouse();
        } else if (currentStep == HouseEntryStep.HOUSE_IMAGES) {
            hideProgress();
            goToNextSection();
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
        for (Map.Entry<String,List<Day>> mapEntry:selectedDaysMap.entrySet()) {
            List<Day> selectedDays = mapEntry.getValue();
            if (selectedDays == null)
                continue;
            for (Day day:selectedDays) {
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

//            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.add_house_collapse_toolbar);
//            collapsingToolbarLayout.setTitle("");
//            collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        }
    }

    private void showProgress() {
        LinearLayout progressBarLayout = (LinearLayout)findViewById(R.id.add_house_progressLayout);
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.add_house_progressBar);

        if (progressBar != null && progressBarLayout != null) {
            progressBar.setVisibility(View.VISIBLE);
            progressBarLayout.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgress() {
        LinearLayout progressBarLayout = (LinearLayout)findViewById(R.id.add_house_progressLayout);
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.add_house_progressBar);

        if (progressBar != null && progressBarLayout != null) {
            progressBar.setVisibility(View.GONE);
            progressBarLayout.setVisibility(View.GONE);
        }
    }

    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        NestedScrollView scrollView = (NestedScrollView)findViewById(R.id.add_house_scrollview);
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
            actionBar.setTitle(getString(titleResId));
            actionBar.setWindowTitle(getString(titleResId));

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
//        goToNextSection();
        showProgress();
        addUpdateHouse();
    }

    @Override
    public void onBackToPreviousSection(House house) {
        backToPreviousSection(house);
    }

    private void setupIndicatorsSize() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        float indicatorWidth = (dpWidth - 4 * 8 - 2 * 8) / 8;
        indicatorWidth = indicatorWidth * displayMetrics.density;
        float indicatorHeight = indicatorWidth;

        TextView indicatorTextView1 = (TextView) findViewById(R.id.add_house_indicator1);
        if (indicatorTextView1 != null && indicatorTextView1.getLayoutParams() != null) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) indicatorTextView1.getLayoutParams();
            params.width = (int) indicatorWidth;
            params.height = (int) indicatorHeight;
            indicatorTextView1.setLayoutParams(params);
        }

        TextView indicatorTextView2 = (TextView) findViewById(R.id.add_house_indicator2);
        if (indicatorTextView2 != null && indicatorTextView2.getLayoutParams() != null) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) indicatorTextView2.getLayoutParams();
            params.width = (int) indicatorWidth;
            params.height = (int) indicatorHeight;
            indicatorTextView2.setLayoutParams(params);
        }

        TextView indicatorTextView3 = (TextView) findViewById(R.id.add_house_indicator3);
        if (indicatorTextView3 != null && indicatorTextView3.getLayoutParams() != null) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) indicatorTextView3.getLayoutParams();
            params.width = (int) indicatorWidth;
            params.height = (int) indicatorHeight;
            indicatorTextView3.setLayoutParams(params);
        }

        TextView indicatorTextView4 = (TextView) findViewById(R.id.add_house_indicator4);
        if (indicatorTextView4 != null && indicatorTextView4.getLayoutParams() != null) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) indicatorTextView4.getLayoutParams();
            params.width = (int) indicatorWidth;
            params.height = (int) indicatorHeight;
            indicatorTextView4.setLayoutParams(params);
        }

        TextView indicatorTextView5 = (TextView) findViewById(R.id.add_house_indicator5);
        if (indicatorTextView5 != null && indicatorTextView5.getLayoutParams() != null) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) indicatorTextView5.getLayoutParams();
            params.width = (int) indicatorWidth;
            params.height = (int) indicatorHeight;
            indicatorTextView5.setLayoutParams(params);
        }

        TextView indicatorTextView6 = (TextView) findViewById(R.id.add_house_indicator6);
        if (indicatorTextView6 != null && indicatorTextView6.getLayoutParams() != null) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) indicatorTextView6.getLayoutParams();
            params.width = (int) indicatorWidth;
            params.height = (int) indicatorHeight;
            indicatorTextView6.setLayoutParams(params);
        }

        TextView indicatorTextView7 = (TextView) findViewById(R.id.add_house_indicator7);
        if (indicatorTextView7 != null && indicatorTextView7.getLayoutParams() != null) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) indicatorTextView7.getLayoutParams();
            params.width = (int) indicatorWidth;
            params.height = (int) indicatorHeight;
            indicatorTextView7.setLayoutParams(params);
        }

        TextView indicatorTextView8 = (TextView) findViewById(R.id.add_house_indicator8);
        if (indicatorTextView8 != null && indicatorTextView8.getLayoutParams() != null) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) indicatorTextView8.getLayoutParams();
            params.width = (int) indicatorWidth;
            params.height = (int) indicatorHeight;
            indicatorTextView8.setLayoutParams(params);
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
