package xyz.narengi.android.ui.activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.Authorization;
import xyz.narengi.android.common.dto.BookRequest;
import xyz.narengi.android.common.dto.Credential;
import xyz.narengi.android.content.CredentialDeserializer;
import xyz.narengi.android.service.RetrofitApiEndpoints;
import xyz.narengi.android.service.RetrofitService;
import xyz.narengi.android.ui.actionbar.ActionBarRtlizer;
import xyz.narengi.android.ui.adapter.BookRequestContentRecyclerAdapter;

/**
 * @author Siavash Mahmoudpour
 */

public class BookRequestDetailActivity extends AppCompatActivity {

    private ActionBarRtlizer rtlizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_request);
        setupToolbar();

        showProgress();
        if (getIntent() != null && getIntent().getStringExtra("bookRequest") != null) {
            Serializable bookRequestSerializable = getIntent().getSerializableExtra("bookRequest");
            if (bookRequestSerializable != null) {
                BookRequest bookRequest = (BookRequest) bookRequestSerializable;
                setupContentRecyclerView(bookRequest);

                if (bookRequest.getGuestUrl() != null) {
                    setProfileImage(bookRequest.getGuestUrl());
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }


    private void setupToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.book_request_toolbar);

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
            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.book_request_collapse_toolbar);
            collapsingToolbarLayout.setTitle("");
            collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        }
    }

    private void setupContentRecyclerView(BookRequest bookRequest) {
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.book_request_contentRecyclerView);

//        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        BookRequestContentRecyclerAdapter recyclerAdapter = new BookRequestContentRecyclerAdapter(this, bookRequest);
        mRecyclerView.setAdapter(recyclerAdapter);
    }

    private void setPageTitle(String title) {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.book_request_toolbar);
        if (toolbar != null) {
            TextView titleTextView = (TextView) toolbar.findViewById(R.id.text_toolbar_title);
            titleTextView.setText(title);
        }
    }

    private void setProfileImage(String imageUrl) {
        ImageView profileImageView = (ImageView) findViewById(R.id.book_request_profileImage);
        Picasso.with(this).load(imageUrl).into(profileImageView);
    }

    private void acceptBookRequest(String requestId) {

        showProgress();

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

        Retrofit retrofit = RetrofitService.getInstance().getRetrofit();

        RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);
        Call<BookRequest> call = apiEndpoints.approveBookRequest(authorizationJson, requestId);

        call.enqueue(new Callback<BookRequest>() {
            @Override
            public void onResponse(Call<BookRequest> call, Response<BookRequest> response) {
                int statusCode = response.code();
                BookRequest bookRequest = response.body();
                if (statusCode != 204) {
                    try {
                        if (response.errorBody() != null) {
                            Toast.makeText(BookRequestDetailActivity.this, response.errorBody().string(), Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(BookRequestDetailActivity.this, "Book request accepted", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<BookRequest> call, Throwable t) {
                hideProgress();
                Toast.makeText(BookRequestDetailActivity.this, "Exception : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void rejectBookRequest(String requestId) {

        showProgress();

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

        Retrofit retrofit = RetrofitService.getInstance().getRetrofit();

        RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);
        Call<BookRequest> call = apiEndpoints.rejectBookRequest(authorizationJson, requestId);

        call.enqueue(new Callback<BookRequest>() {
            @Override
            public void onResponse(Call<BookRequest> call, Response<BookRequest> response) {
                int statusCode = response.code();
                BookRequest bookRequest = response.body();
                if (statusCode != 204) {
                    try {
                        if (response.errorBody() != null) {
                            Toast.makeText(BookRequestDetailActivity.this, response.errorBody().string(), Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(BookRequestDetailActivity.this, "Book request rejected", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<BookRequest> call, Throwable t) {
                hideProgress();
                Toast.makeText(BookRequestDetailActivity.this, "Exception : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void showProgress() {
        LinearLayout progressBarLayout = (LinearLayout) findViewById(R.id.book_request_progressLayout);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.book_request_progressBar);

        if (progressBar != null && progressBarLayout != null) {
            progressBar.setVisibility(View.VISIBLE);
            progressBarLayout.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgress() {
        LinearLayout progressBarLayout = (LinearLayout) findViewById(R.id.book_request_progressLayout);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.book_request_progressBar);

        if (progressBar != null && progressBarLayout != null) {
            progressBar.setVisibility(View.GONE);
            progressBarLayout.setVisibility(View.GONE);
        }
    }

}
