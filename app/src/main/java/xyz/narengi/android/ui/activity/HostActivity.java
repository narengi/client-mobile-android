package xyz.narengi.android.ui.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import info.semsamot.actionbarrtlizer.ActionBarRtlizer;
import info.semsamot.actionbarrtlizer.RtlizeEverything;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.HostProfile;
import xyz.narengi.android.content.HostProfileDeserializer;
import xyz.narengi.android.service.RetrofitApiEndpoints;
import xyz.narengi.android.service.RetrofitService;
import xyz.narengi.android.ui.adapter.HostContentRecyclerAdapter;

public class HostActivity extends AppCompatActivity {

    private ActionBarRtlizer rtlizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);
        setupToolbar();

        showProgress();
        if (getIntent() != null && getIntent().getStringExtra("hostUrl") != null) {
            String hostUrl = getIntent().getStringExtra("hostUrl");
            getHost(hostUrl);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void getHost(String url) {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(HostProfile.class, new HostProfileDeserializer()).create();

        Retrofit retrofit = RetrofitService.getInstance().getRetrofit();

        RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);

        Call<HostProfile> call = apiEndpoints.getHostProfile(url);
        call.enqueue(new Callback<HostProfile>() {
            @Override
            public void onResponse(Call<HostProfile> call, Response<HostProfile> response) {
//                int statusCode = response.code();
                hideProgress();
                HostProfile hostProfile = response.body();
                if (hostProfile != null) {
                    setHost(hostProfile);
                }
            }

            @Override
            public void onFailure(Call<HostProfile> call, Throwable t) {
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
            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.host_collapse_toolbar);
            collapsingToolbarLayout.setTitle("");
            collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        }
    }

    private void setPageTitle(String title) {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.host_toolbar);
        if (toolbar != null) {
            TextView titleTextView = (TextView) toolbar.findViewById(R.id.text_toolbar_title);
            titleTextView.setText(title);
        }
    }

    private void setHost(HostProfile hostProfile) {

        if (hostProfile.getImageUrl() != null)
            setProfileImage(hostProfile.getImageUrl());

        setupContentRecyclerView(hostProfile);

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.host_collapse_toolbar);
//        collapsingToolbarLayout.setTitle(hostProfile.getDisplayName());
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        setPageTitle(hostProfile.getDisplayName());
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


    private void setProfileImage(String imageUrl) {
        ImageView profileImageView = (ImageView) findViewById(R.id.host_profileImage);
        Picasso.with(this).load(imageUrl).into(profileImageView);
    }

    private void setupContentRecyclerView(HostProfile host) {
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.host_contentRecyclerView);

//        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        HostContentRecyclerAdapter recyclerAdapter = new HostContentRecyclerAdapter(this, host);
        mRecyclerView.setAdapter(recyclerAdapter);
    }

    private void showProgress() {
        LinearLayout progressBarLayout = (LinearLayout) findViewById(R.id.host_progressLayout);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.host_progressBar);

        if (progressBar != null && progressBarLayout != null) {
            progressBar.setVisibility(View.VISIBLE);
            progressBarLayout.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgress() {
        LinearLayout progressBarLayout = (LinearLayout) findViewById(R.id.host_progressLayout);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.host_progressBar);

        if (progressBar != null && progressBarLayout != null) {
            progressBar.setVisibility(View.GONE);
            progressBarLayout.setVisibility(View.GONE);
        }
    }

}
