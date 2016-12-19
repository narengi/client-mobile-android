package xyz.narengi.android.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import xyz.narengi.android.ui.dialog.BetaDialog;
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

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private ActionBarRtlizer rtlizer;
    private HouseEntryStep houseEntryStep;
    private House house;
    private List<Uri> imageUris;
    private ImageInfo[] imageInfoArray;
    private HashMap<String, List<Day>> selectedDaysMap;
    private AlertDialog progressDialog;
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
                selectedDaysMap = (HashMap<String, List<Day>>) getIntent().getSerializableExtra("selectedDaysMap");
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

    private void setPageTitle(String title) {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.edit_house_detail_toolbar);
        if (toolbar != null) {
            TextView titleTextView = (TextView) toolbar.findViewById(R.id.text_toolbar_title);
            titleTextView.setText(title);
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


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_edit_house_detail, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
//        } else if (id == R.id.edit_house_detail_save) {
//            updateHouseRequested();
//            return true;
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

        View view = findViewById(R.id.tvSave);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateHouseRequested();

            }
        });

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
        }
    }

    private void updatePageTitle(int titleResId) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
//            actionBar.setTitle(getString(titleResId));
//            actionBar.setWindowTitle(getString(titleResId));
            String title = getString(titleResId);
            setPageTitle(title);
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
            ((HouseImagesEntryFragment) fragment).uploadHouseImages();
        } else {
//            showProgress();
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

                new BetaDialog(this).show();
//                houseEntryFragment = new HouseDatesEntryFragment();
//                updatePageTitle(R.string.house_dates_entry_page_title);
                break;
        }

        if (houseEntryStep != HouseEntryStep.HOUSE_MAP && houseEntryStep != HouseEntryStep.HOUSE_FEATURES)
            requestDisallowInterceptTouchEvent(false);

        return houseEntryFragment;
    }

    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        NestedScrollView scrollView = (NestedScrollView) findViewById(R.id.edit_house_detail_scrollview);
        if (scrollView != null)
            scrollView.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    private void initFragment(HouseEntryBaseFragment houseEntryFragment) {
        houseEntryFragment.setHouse(house);
        houseEntryFragment.setEntryMode(HouseEntryBaseFragment.EntryMode.EDIT);
    }


    private void updateHouse() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        HouseEntryInput houseEntryInput = getHouseEntryInput();
        if (houseEntryInput == null)
            return;
        Retrofit retrofit = RetrofitService.getInstance().getRetrofit();

        RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);
        updateHouseRetrofitCall = apiEndpoints.updateHouse(house.getDetailUrl(), houseEntryInput);

        updateHouseRetrofitCall.enqueue(new Callback<House>() {
            @Override
            public void onResponse(Call<House> call, Response<House> response) {
//                hideProgress();
                progressDialog.dismiss();

                if (response.isSuccessful()) {
                    house = response.body();
                    Intent intent = new Intent();
                    intent.putExtra("updatedHouse", house);
                    setResult(2002, intent);
                    finish();
                } else {
                    Toast.makeText(EditHouseDetailActivity.this, R.string.error_alert_title, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<House> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(EditHouseDetailActivity.this, R.string.error_alert_title, Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

//    public void showUpdateHouseResultDialog() {
//
//        Toast toast = new Toast(getApplicationContext());
//        View view = getLayoutInflater().inflate(R.layout.dialog_sign_up_success, null);
//        ViewGroup.LayoutParams params = view.getLayoutParams();
//        if (params != null) {
//            int width = (getScreenWidth(this) * 3 / 5);
//            params.width = width;
//            params.height = width;
//            view.setLayoutParams(params);
//        }
//
//        toast.setView(view);
//        toast.setDuration(Toast.LENGTH_LONG);
//
//        int margin = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
//        toast.setGravity(Gravity.CENTER, 0, margin);
//        toast.show();
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent intent = new Intent(EditHouseDetailActivity.this, EditHouseActivity.class);
//                intent.putExtra("updatedHouse", house);
//                intent.putExtra("updatedImageInfoArray", imageInfoArray);
//                setResult(2002, intent);
//                finish();
//            }
//        }, 2500);
//    }

//    private int getScreenWidth(Context context) {
//        int measuredWidth;
//        Point size = new Point();
//        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
//            wm.getDefaultDisplay().getSize(size);
//            measuredWidth = size.x;
//        } else {
//            Display d = wm.getDefaultDisplay();
//            measuredWidth = d.getHeight();
//        }
//
//        return measuredWidth;
//    }

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

        if (house.getLocation() != null){
            Location location = new Location();
            location.setProvince(house.getLocation().getProvince());
            location.setCity(house.getLocation().getCity());
            location.setAddress(house.getLocation().getAddress());
            houseEntryInput.setLocation(location);
        }

        if (house.getType()!=null) {
            houseEntryInput.setType(house.getType().getKey());
        }
        houseEntryInput.setSpec(house.getSpec());
        houseEntryInput.setAvailableDates(getSelectedDates());

		String[] features = new String[house.getFeatureList().length];
		for (int i = 0; i < house.getFeatureList().length ; i++) {
			features[i]= house.getFeatureList()[i].getKey();
		}
        houseEntryInput.setFeatureList(features);

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

//    public void showProgressBar() {
//        LinearLayout progressBarLayout = (LinearLayout) findViewById(R.id.edit_house_detail_progressLayout);
//        ProgressBar progressBar = (ProgressBar) findViewById(R.id.edit_house_detail_progressBar);
//
//        if (progressBar != null && progressBarLayout != null) {
//            progressBar.setVisibility(View.VISIBLE);
//            progressBarLayout.setVisibility(View.VISIBLE);
//        }
//    }

    public void hideProgressBar() {
        LinearLayout progressBarLayout = (LinearLayout) findViewById(R.id.edit_house_detail_progressLayout);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.edit_house_detail_progressBar);

        if (progressBar != null && progressBarLayout != null) {
            progressBar.setVisibility(View.GONE);
            progressBarLayout.setVisibility(View.GONE);
        }
    }

//    private void showProgress() {
////        LinearLayout progressBarLayout = (LinearLayout)findViewById(R.id.edit_house_detail_progressLayout);
////        ProgressBar progressBar = (ProgressBar)findViewById(R.id.edit_house_detail_progressBar);
////
////        if (progressBar != null && progressBarLayout != null) {
////            progressBar.setVisibility(View.VISIBLE);
////            progressBarLayout.setVisibility(View.VISIBLE);
////        }
//
//        if (progressDialog == null)
//            progressDialog = AlertUtils.getInstance().createModelProgress(this);
//        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialogInterface) {
//                dialogInterface.dismiss();
//
//                if (updateHouseRetrofitCall != null)
//                    updateHouseRetrofitCall.cancel();
//            }
//        });
//
//        progressDialog.show();
//    }

//    private void hideProgress() {
////        LinearLayout progressBarLayout = (LinearLayout)findViewById(R.id.edit_house_detail_progressLayout);
////        ProgressBar progressBar = (ProgressBar)findViewById(R.id.edit_house_detail_progressBar);
////
////        if (progressBar != null && progressBarLayout != null) {
////            progressBar.setVisibility(View.GONE);
////            progressBarLayout.setVisibility(View.GONE);
////        }
//
//        if (progressDialog != null)
//            progressDialog.dismiss();
//    }


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

//    public void setImageUris(List<Uri> imageUris) {
//        this.imageUris = imageUris;
//    }

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
