package xyz.narengi.android.ui.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.WindowCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.narengi.android.R;
import xyz.narengi.android.common.Constants;
import xyz.narengi.android.common.dto.Host;
import xyz.narengi.android.common.dto.HostProfile;
import xyz.narengi.android.common.dto.House;
import xyz.narengi.android.content.HostProfileDeserializer;
import xyz.narengi.android.content.HouseDeserializer;
import xyz.narengi.android.service.RetrofitApiEndpoints;
import xyz.narengi.android.ui.adapter.HostContentRecyclerAdapter;

public class HostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);
        setupToolbar();

//        showProgress();
        if (getIntent() != null && getIntent().getStringExtra("hostUrl") != null) {
            String hostUrl = getIntent().getStringExtra("hostUrl");
            getHost(hostUrl);
        }
    }

    private void getHost(String url) {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(HostProfile.class, new HostProfileDeserializer()).create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.SERVER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);

        Call<HostProfile> call = apiEndpoints.getHostProfile(url);
        call.enqueue(new Callback<HostProfile>() {
            @Override
            public void onResponse(Response<HostProfile> response, Retrofit retrofit) {
//                int statusCode = response.code();
//                hideProgress();
                HostProfile hostProfile = response.body();
                if (hostProfile != null) {
                    setHost(hostProfile);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                // Log error here since request failed
                t.printStackTrace();
                Log.d("HostActivity", "getHost onFailure : " + t.getMessage(), t);
                hideProgress();
                Toast.makeText(HostActivity.this, "Error getting host profile data!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.host_toolbar);

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
        }
    }

    private void setHost(HostProfile hostProfile) {

        if (hostProfile.getImageUrl() != null)
            setProfileImage(hostProfile.getImageUrl());

        setupContentRecyclerView(hostProfile);

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.host_collapse_toolbar);
        collapsingToolbarLayout.setTitle(hostProfile.getDisplayName());
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
    }

    private void setProfileImage(String imageUrl) {
        ImageView profileImageView = (ImageView)findViewById(R.id.host_profileImage);
        Picasso.with(this).load(imageUrl).into(profileImageView);
    }

    private void setupContentRecyclerView(HostProfile host) {
        RecyclerView mRecyclerView = (RecyclerView)findViewById(R.id.host_contentRecyclerView);

//        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        HostContentRecyclerAdapter recyclerAdapter = new HostContentRecyclerAdapter(this, host);
        mRecyclerView.setAdapter(recyclerAdapter);
    }

    private void showProgress() {
        LinearLayout progressBarLayout = (LinearLayout)findViewById(R.id.house_progressLayout);
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.house_progressBar);

        if (progressBar != null && progressBarLayout != null) {
            progressBar.setVisibility(View.VISIBLE);
            progressBarLayout.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgress() {
        LinearLayout progressBarLayout = (LinearLayout)findViewById(R.id.house_progressLayout);
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.house_progressBar);

        if (progressBar != null && progressBarLayout != null) {
            progressBar.setVisibility(View.GONE);
            progressBarLayout.setVisibility(View.GONE);
        }

    }

}
