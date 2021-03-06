package xyz.narengi.android.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
import xyz.narengi.android.common.dto.House;
import xyz.narengi.android.common.dto.HouseAvailableDates;
import xyz.narengi.android.common.dto.ImageInfo;
import xyz.narengi.android.service.RetrofitApiEndpoints;
import xyz.narengi.android.service.RetrofitService;
import xyz.narengi.android.ui.adapter.HostHousesContentRecyclerAdapter;

public class HostHousesActivity extends AppCompatActivity implements HostHousesContentRecyclerAdapter.RemoveHouseListener {

    private ActionBarRtlizer rtlizer;
    private House[] hostHouses;
    //    private List<ImageInfo[]> imageInfoList;
    private Map<String, ImageInfo[]> allImageInfoArraysMap;
    //    private List<HouseAvailableDates> houseAvailableDatesList;
    private Map<String, HouseAvailableDates> allHouseAvailableDatesMap;
    private RecyclerView mRecyclerView;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_houses);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.host_houses_swipeRefreshLayout);

//        swipeRefreshLayout.setActivated(true);
//        swipeRefreshLayout.dispatchSetActivated(true);
//        swipeRefreshLayout.getViewTreeObserver()
//                .addOnGlobalLayoutListener(
//                        new ViewTreeObserver.OnGlobalLayoutListener() {
//                            @Override
//                            public void onGlobalLayout() {
//                                swipeRefreshLayout.setRefreshing(true);
//                                swipeRefreshLayout
//                                        .getViewTreeObserver()
//                                        .removeGlobalOnLayoutListener(this);
//                            }
//                        });


        TypedValue typed_value = new TypedValue();
        getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, typed_value, true);
        swipeRefreshLayout.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(typed_value.resourceId));
        swipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        }, 100);

        setupToolbar();
//        getHostHouses();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getHostHouses();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.host_houses_addHouseFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddHouse();
            }
        });
    }

    private void setPageTitle(String title) {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.host_houses_toolbar);
        if (toolbar != null) {
            TextView titleTextView = (TextView) toolbar.findViewById(R.id.text_toolbar_title);
            titleTextView.setText(title);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getHostHouses();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
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


    private void setupToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.host_houses_toolbar);

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

//            actionBar.setTitle(getString(R.string.host_houses_page_title));
//            actionBar.setWindowTitle(getString(R.string.host_houses_page_title));
            setPageTitle(getString(R.string.host_houses_page_title));
        }
    }

    private void setupContentRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.host_houses_recyclerView);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

//        HostHousesContentRecyclerAdapter contentRecyclerAdapter = new HostHousesContentRecyclerAdapter(this,
//                hostHouses, imageInfoList, houseAvailableDatesList, this);
        HostHousesContentRecyclerAdapter contentRecyclerAdapter = new HostHousesContentRecyclerAdapter(this,
                hostHouses, /*null, null,*/ this/*, allImageInfoArraysMap, allHouseAvailableDatesMap*/);
        mRecyclerView.setAdapter(contentRecyclerAdapter);
    }

    private void showProgress() {
//        LinearLayout progressBarLayout = (LinearLayout) findViewById(R.id.host_houses_progressLayout);
//        ProgressBar progressBar = (ProgressBar) findViewById(R.id.host_houses_progressBar);
//
//        if (progressBarLayout != null && progressBar != null) {
//            progressBar.setVisibility(View.VISIBLE);
//            progressBarLayout.setVisibility(View.VISIBLE);
//        }

//        swipeRefreshLayout.setRefreshing(true);


        TypedValue typed_value = new TypedValue();
        getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, typed_value, true);
        swipeRefreshLayout.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(typed_value.resourceId));
        swipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        }, 100);
    }

    private void hideProgress() {
//        LinearLayout progressBarLayout = (LinearLayout) findViewById(R.id.host_houses_progressLayout);
//        ProgressBar progressBar = (ProgressBar) findViewById(R.id.host_houses_progressBar);
//
//        if (progressBarLayout != null && progressBar != null) {
//            progressBar.setVisibility(View.GONE);
//            progressBarLayout.setVisibility(View.GONE);
//        }


        swipeRefreshLayout.setRefreshing(false);
    }

    private void getHostHouses() {
		showProgress();
        Retrofit retrofit = RetrofitService.getInstance().getRetrofit();

        RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);
        Call<House[]> call = apiEndpoints.getHostHouses();

        call.enqueue(new Callback<House[]>() {
            @Override
            public void onResponse(Call<House[]> call, Response<House[]> response) {
				hideProgress();
				if (response.isSuccessful()) {
					hostHouses = response.body();

//                ImagesAndDatesAsyncTask imagesAndDatesAsyncTask = new ImagesAndDatesAsyncTask();
//                AsyncTask asyncTask = imagesAndDatesAsyncTask .execute();
//                try {
//                    Object asyncTaskResult = asyncTask.get();
//                    setupContentRecyclerView();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }

					if (hostHouses == null || hostHouses.length == 0) {
						setupContentRecyclerView();
					} else {
//						readDatesAndImages();

						setupContentRecyclerView();
					}
				}
			}

            @Override
            public void onFailure(Call<House[]> call, Throwable t) {
                hideProgress();
                t.printStackTrace();
            }
        });
    }
//
//    private void readImageInfo(final House house) {
//        Retrofit retrofit = RetrofitService.getInstance().getRetrofit();
//
//        RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);
//        Call<ImageInfo[]> imagesCall = apiEndpoints.getHouseImages(house.getDetailUrl() + "/pictures");
//
//        imagesCall.enqueue(new Callback<ImageInfo[]>() {
//            @Override
//            public void onResponse(Call<ImageInfo[]> call, Response<ImageInfo[]> response) {
//				hideProgress();
//                ImageInfo[] result = response.body();
//                allImageInfoArraysMap.put(house.getDetailUrl(), result);
//
//                if (allImageInfoArraysMap.size() == hostHouses.length) {
//                    HostHousesContentRecyclerAdapter contentRecyclerAdapter = new HostHousesContentRecyclerAdapter(HostHousesActivity.this,
//                            hostHouses, null, null, HostHousesActivity.this, allImageInfoArraysMap, allHouseAvailableDatesMap);
//
//                    if (mRecyclerView == null)
//                        mRecyclerView = (RecyclerView) findViewById(R.id.host_houses_recyclerView);
//                    mRecyclerView.setAdapter(contentRecyclerAdapter);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ImageInfo[]> call, Throwable t) {
//				hideProgress();
//                allImageInfoArraysMap.put(house.getDetailUrl(), null);
//                if (allImageInfoArraysMap.size() == hostHouses.length) {
//                    HostHousesContentRecyclerAdapter contentRecyclerAdapter = new HostHousesContentRecyclerAdapter(HostHousesActivity.this,
//                            hostHouses, null, null, HostHousesActivity.this, allImageInfoArraysMap, allHouseAvailableDatesMap);
//
//                    if (mRecyclerView == null)
//                        mRecyclerView = (RecyclerView) findViewById(R.id.host_houses_recyclerView);
//                    mRecyclerView.setAdapter(contentRecyclerAdapter);
//                }
//
//                t.printStackTrace();
//
//            }
//        });
//    }
//
//    private void readAvailableDates(House house) {
//
//    }

//    private void readDatesAndImages() {
//
//        if (hostHouses == null || hostHouses.length == 0)
//            return;
//
//        allImageInfoArraysMap = new HashMap<String, ImageInfo[]>();

//        final SharedPreferences preferences = getSharedPreferences("profile", 0);
//        String accessToken = preferences.getString("accessToken", "");
//        String username = preferences.getString("username", "");
//
//        Authorization authorization = new Authorization();
//        authorization.setUsername(username);
//        authorization.setToken(accessToken);
//
//        Gson gson = new GsonBuilder()
//                .registerTypeAdapter(Credential.class, new CredentialDeserializer())
//                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
//                .create();
//
//        String authorizationJson = gson.toJson(authorization);
//        if (authorizationJson != null) {
//            authorizationJson = authorizationJson.replace("{", "");
//            authorizationJson = authorizationJson.replace("}", "");
//        }
//
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        Date startDate = new Date();
//        final Calendar calendar = Calendar.getInstance();
//        calendar.setTime(startDate);
//        calendar.add(Calendar.MONTH, 3);
//        Date endDate = calendar.getTime();
//        String startDateString = simpleDateFormat.format(startDate);
//        String endDateString = simpleDateFormat.format(endDate);

//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(Constants.SERVER_BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .build();
//
//        RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);

//        imageInfoList = new ArrayList<ImageInfo[]>();
//        houseAvailableDatesList = new ArrayList<HouseAvailableDates>();

//        for (House house : hostHouses) {

//            readImageInfo(house);

//            Call<ImageInfo[]> imagesCall = apiEndpoints.getHouseImages(house.getDetailUrl() + "/pictures");
//
//            try {
//                Response<ImageInfo[]> imageCallResponse = imagesCall.execute();
//                ImageInfo[] result = imageCallResponse.body();
//                imageInfoList.add(result);
//            } catch (IOException e) {
//                imageInfoList.add(null);
//                e.printStackTrace();
//            }

            /*String url = house.getDetailUrl();
            url = url + "/available-dates/start-" + startDateString + "/end-" + endDateString;

            Call<HouseAvailableDates> datesCall = apiEndpoints.getHouseAvailableDates(url);

            try {
                Response<HouseAvailableDates> datesResponse = datesCall.execute();
                HouseAvailableDates houseAvailableDates = datesResponse.body();
                houseAvailableDatesList.add(houseAvailableDates);
            } catch (IOException e) {
                houseAvailableDatesList.add(null);
                e.printStackTrace();
            }*/
//        }
//    }

    public List<HouseAvailableDates> getHouseAvailableDatesList() {

//        if (hostHouses == null || hostHouses.length == 0)
        return null;

//        return houseAvailableDatesList;
    }

    private void openAddHouse() {
        Intent intent = new Intent(this, AddHouseActivity.class);
        startActivityForResult(intent, 2001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 2001 || resultCode == 2002) {
            getHostHouses();
        }
    }

    private void removeHouse(House house) {
		showProgress();
        Retrofit retrofit = RetrofitService.getInstance().getRetrofit();

        RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);
        Call<Object> call = apiEndpoints.removeHouse(house.getId());

        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                hideProgress();
                if (response.isSuccessful()) {
                    getHostHouses();
                } else {
                    Toast.makeText(HostHousesActivity.this, R.string.error_alert_title, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                hideProgress();
                Toast.makeText(HostHousesActivity.this, R.string.error_alert_title, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRemoveHouse(House house) {
        showRemoveHouseAlert(house);
    }

    private void showRemoveHouseAlert(final House house) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.host_houses_remove_alert_title);
        builder.setMessage(R.string.host_houses_remove_alert_message);

        builder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                removeHouse(house);
            }
        });

        builder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getHostHouses();
    }
}
