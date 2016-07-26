package xyz.narengi.android.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.byagowi.persiancalendar.Entity.Day;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.narengi.android.R;
import xyz.narengi.android.common.Constants;
import xyz.narengi.android.common.HouseEntryStep;
import xyz.narengi.android.common.dto.Authorization;
import xyz.narengi.android.common.dto.Credential;
import xyz.narengi.android.common.dto.House;
import xyz.narengi.android.common.dto.HouseAvailableDates;
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
import xyz.narengi.android.ui.util.AlertUtils;
import xyz.narengi.android.util.DateUtils;

/**
 * @author Siavash Mahmoudpour
 */
public class EditHouseDetailActivity extends AppCompatActivity {

    private HouseEntryStep houseEntryStep;
    private House house;
    private List<Uri> imageUris;
    private ImageInfo[] imageInfoArray;
    private HashMap<String,List<Day>> selectedDaysMap;
    private AlertDialog progressDialog;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private Call<House> updateHouseRetrofitCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_house_detail);
        setupToolbar();

        if (getIntent() != null) {

            if (getIntent().getSerializableExtra("imageInfoArray") != null) {
                imageInfoArray = (ImageInfo[]) getIntent().getSerializableExtra("imageInfoArray");
            }

            if (getIntent().getSerializableExtra("selectedDaysMap") != null) {
                selectedDaysMap = (HashMap<String,List<Day>>) getIntent().getSerializableExtra("selectedDaysMap");
            }

            if (getIntent().getSerializableExtra("house") != null && getIntent().getStringExtra("houseEntrySection") != null) {
                house = (House) getIntent().getSerializableExtra("house");
                houseEntryStep = getHouseEntryStep(Integer.parseInt(getIntent().getStringExtra("houseEntrySection")));

//                if (imageInfoArray != null) {
//                    showProgressBar();
//                }

                HouseEntryBaseFragment houseEntryFragment = getHouseEntryFragment();
                initFragment(houseEntryFragment);

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.edit_house_detail_content, houseEntryFragment, "EditHouseContentFragment")
                        .commit();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_house_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.edit_house_detail_save) {
            updateHouseRequested();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();

        if (updateHouseRetrofitCall != null)
            updateHouseRetrofitCall.cancel();

        super.onBackPressed();
    }

    private void setupToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.edit_house_detail_toolbar);

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
        }
    }

    private void updatePageTitle(int titleResId) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(titleResId));
            actionBar.setWindowTitle(getString(titleResId));
        }
    }

    private House readCurrentFragmentHouse() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.edit_house_detail_content);
        if (currentFragment != null && currentFragment instanceof HouseEntryBaseFragment) {
            house = ((HouseEntryBaseFragment) currentFragment).getHouse();
        }
        return house;
    }

    private void updateHouseRequested() {
        readCurrentFragmentHouse();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.edit_house_detail_content);

        if (fragment instanceof HouseImagesEntryFragment) {
            ((HouseImagesEntryFragment)fragment).uploadHouseImages();
        } else {
            showProgress();
            updateHouse();
        }
    }

    private HouseEntryBaseFragment getHouseEntryFragment() {

        HouseEntryBaseFragment houseEntryFragment = null;

        switch (houseEntryStep) {
            case HOUSE_INFO:
                houseEntryFragment = new HouseInfoEntryFragment();
                updatePageTitle(R.string.house_info_entry_page_title);
                break;
            case HOUSE_MAP:
                houseEntryFragment = new HouseMapEntryFragment();
                updatePageTitle(R.string.house_map_entry_page_title);
                break;
            case HOUSE_TYPE:
                houseEntryFragment = new HouseTypeEntryFragment();
                updatePageTitle(R.string.house_type_entry_page_title);
                break;
            case HOUSE_ROOMS:
                houseEntryFragment = new HouseRoomEntryFragment();
                updatePageTitle(R.string.house_room_entry_page_title);
                break;
            case HOUSE_GUESTS:
                houseEntryFragment = new HouseGuestEntryFragment();
                updatePageTitle(R.string.house_guest_entry_page_title);
                break;
            case HOUSE_FEATURES:
                houseEntryFragment = new HouseFeaturesEntryFragment();
                updatePageTitle(R.string.house_features_entry_page_title);
                break;
            case HOUSE_IMAGES:
                houseEntryFragment = new HouseImagesEntryFragment();
                updatePageTitle(R.string.house_images_entry_page_title);
                break;
            case HOUSE_DATES:
                houseEntryFragment = new HouseDatesEntryFragment();
                updatePageTitle(R.string.house_dates_entry_page_title);
                break;
        }

        if (houseEntryStep != HouseEntryStep.HOUSE_MAP && houseEntryStep != HouseEntryStep.HOUSE_FEATURES)
            requestDisallowInterceptTouchEvent(false);

        return houseEntryFragment;
    }

    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        NestedScrollView scrollView = (NestedScrollView)findViewById(R.id.edit_house_detail_scrollview);
        if (scrollView != null)
            scrollView.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    private void initFragment(HouseEntryBaseFragment houseEntryFragment) {
        houseEntryFragment.setHouse(house);
        houseEntryFragment.setEntryMode(HouseEntryBaseFragment.EntryMode.EDIT);
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
        updateHouseRetrofitCall = apiEndpoints.updateHouse(authorizationJson, house.getURL(), houseEntryInput);

        updateHouseRetrofitCall.enqueue(new Callback<House>() {
            @Override
            public void onResponse(Response<House> response, Retrofit retrofit) {
                hideProgress();
                int statusCode = response.code();
                House resultHouse = response.body();
                if (resultHouse == null) {
                    try {
                        if (response.errorBody() != null) {
                            Toast.makeText(EditHouseDetailActivity.this, response.errorBody().string(), Toast.LENGTH_LONG).show();
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
            public void onFailure(Throwable t) {
                hideProgress();
                Toast.makeText(EditHouseDetailActivity.this, "Exception : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
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
        toast.setDuration(Toast.LENGTH_LONG);

        int margin = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        toast.setGravity(Gravity.CENTER, 0, margin);
        toast.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(EditHouseDetailActivity.this, EditHouseActivity.class);
                intent.putExtra("updatedHouse", house);
                intent.putExtra("updatedImageInfoArray", imageInfoArray);
                setResult(2002, intent);
                finish();
            }
        }, 2500);
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

    public void showProgressBar() {
        LinearLayout progressBarLayout = (LinearLayout)findViewById(R.id.edit_house_detail_progressLayout);
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.edit_house_detail_progressBar);

        if (progressBar != null && progressBarLayout != null) {
            progressBar.setVisibility(View.VISIBLE);
            progressBarLayout.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgressBar() {
        LinearLayout progressBarLayout = (LinearLayout)findViewById(R.id.edit_house_detail_progressLayout);
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.edit_house_detail_progressBar);

        if (progressBar != null && progressBarLayout != null) {
            progressBar.setVisibility(View.GONE);
            progressBarLayout.setVisibility(View.GONE);
        }
    }

    private void showProgress() {
//        LinearLayout progressBarLayout = (LinearLayout)findViewById(R.id.edit_house_detail_progressLayout);
//        ProgressBar progressBar = (ProgressBar)findViewById(R.id.edit_house_detail_progressBar);
//
//        if (progressBar != null && progressBarLayout != null) {
//            progressBar.setVisibility(View.VISIBLE);
//            progressBarLayout.setVisibility(View.VISIBLE);
//        }

        if (progressDialog == null)
            progressDialog = AlertUtils.getInstance().createModelProgress(this);
        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                dialogInterface.dismiss();

                if (updateHouseRetrofitCall != null)
                    updateHouseRetrofitCall.cancel();
            }
        });

        progressDialog.show();
    }

    private void hideProgress() {
//        LinearLayout progressBarLayout = (LinearLayout)findViewById(R.id.edit_house_detail_progressLayout);
//        ProgressBar progressBar = (ProgressBar)findViewById(R.id.edit_house_detail_progressBar);
//
//        if (progressBar != null && progressBarLayout != null) {
//            progressBar.setVisibility(View.GONE);
//            progressBarLayout.setVisibility(View.GONE);
//        }

        if (progressDialog != null)
            progressDialog.dismiss();
    }


    private HouseEntryStep getHouseEntryStep(int ordinal) {
        if (ordinal > HouseEntryStep.values().length)
            return HouseEntryStep.HOUSE_INFO;

        return HouseEntryStep.values()[ordinal];
    }

    public House getHouse() {
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
    }

    public List<Uri> getImageUris() {
        return imageUris;
    }

    public void setImageUris(List<Uri> imageUris) {
        this.imageUris = imageUris;
    }

    public ImageInfo[] getImageInfoArray() {
        return imageInfoArray;
    }

    public void setImageInfoArray(ImageInfo[] imageInfoArray) {
        this.imageInfoArray = imageInfoArray;
    }

    public HashMap<String, List<Day>> getSelectedDaysMap() {
        return selectedDaysMap;
    }

    public void setSelectedDaysMap(HashMap<String, List<Day>> selectedDaysMap) {
        this.selectedDaysMap = selectedDaysMap;
    }
}