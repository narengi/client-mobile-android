package xyz.narengi.android.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
import xyz.narengi.android.content.CredentialDeserializer;
import xyz.narengi.android.service.RetrofitApiEndpoints;
import xyz.narengi.android.ui.adapter.HostHousesContentRecyclerAdapter;

public class HostHousesActivity extends AppCompatActivity implements HostHousesContentRecyclerAdapter.RemoveHouseListener {

    private House[] hostHouses;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_houses);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.host_houses_swipeRefreshLayout);
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


        /*TypedValue typed_value = new TypedValue();
        getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, typed_value, true);
        swipeRefreshLayout.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(typed_value.resourceId));
        swipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        }, 100);*/

        setupToolbar();
        showProgress();
        getHostHouses();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showProgress();
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        showProgress();
        getHostHouses();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.host_houses_toolbar);

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

            actionBar.setTitle(getString(R.string.host_houses_page_title));
            actionBar.setWindowTitle(getString(R.string.host_houses_page_title));
        }
    }

    private void setupContentRecyclerView() {
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.host_houses_recyclerView);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        HostHousesContentRecyclerAdapter contentRecyclerAdapter = new HostHousesContentRecyclerAdapter(this, hostHouses, this);
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
        Call<House[]> call = apiEndpoints.getHostHouses(authorizationJson);

        call.enqueue(new Callback<House[]>() {
            @Override
            public void onResponse(Response<House[]> response, Retrofit retrofit) {
                hideProgress();
                int statusCode = response.code();
                hostHouses = response.body();
//                if (hostHouses != null && hostHouses.length > 0) {
                setupContentRecyclerView();
//                }
            }

            @Override
            public void onFailure(Throwable t) {
                hideProgress();
                t.printStackTrace();
            }
        });
    }

    private void openAddHouse() {
        Intent intent = new Intent(this, AddHouseActivity.class);
        startActivityForResult(intent, 2001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 2001 || resultCode == 2002) {
            showProgress();
            getHostHouses();
        }
    }

    private void removeHouse(House house) {
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
        Call<Object> call = apiEndpoints.removeHouse(authorizationJson, house.getURL());

        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Response<Object> response, Retrofit retrofit) {
//                hideProgress();
                int statusCode = response.code();
                getHostHouses();
            }

            @Override
            public void onFailure(Throwable t) {
                hideProgress();
                t.printStackTrace();
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
                showProgress();
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

}
