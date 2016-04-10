package xyz.narengi.android.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.narengi.android.R;
import xyz.narengi.android.common.Constants;
import xyz.narengi.android.common.dto.AccountProfile;
import xyz.narengi.android.common.dto.AccountVerification;
import xyz.narengi.android.common.dto.Authorization;
import xyz.narengi.android.common.dto.Credential;
import xyz.narengi.android.common.dto.Profile;
import xyz.narengi.android.content.CredentialDeserializer;
import xyz.narengi.android.service.RetrofitApiEndpoints;

/**
 * @author Siavash Mahmoudpour
 */
public class ViewProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        setupToolbar();
        showProgress();
        getProfile();
        getProfilePicture();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 401) {
            setResult(401);
            finish();
        }

//        if (resultCode == 101 ) {
//            getProfile();
//        }
        getProfile();
        getProfilePicture();

    }

    @Override
    public void onBackPressed() {
        setResult(102);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_profile_menu, menu);
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
        } else if (id == R.id.view_profile_edit) {
            openEditProfile();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.view_profile_toolbar);

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

//            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.view_profile_collapse_toolbar);
//            collapsingToolbarLayout.setTitle("");
//            collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        }
    }

    private void openEditProfile() {
        Intent intent = new Intent(this, EditProfileActivity.class);
        intent.putExtra("openedFromViewProfile", true);
        startActivityForResult(intent, 103);
    }

    private void getProfile() {
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
        Call<AccountProfile> call = apiEndpoints.getProfile(authorizationJson);

        call.enqueue(new Callback<AccountProfile>() {
            @Override
            public void onResponse(Response<AccountProfile> response, Retrofit retrofit) {
                hideProgress();
                int statusCode = response.code();
                AccountProfile accountProfile = response.body();
                if (accountProfile != null) {
                    setProfile(accountProfile);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                hideProgress();
                t.printStackTrace();
            }
        });
    }

    private void getProfilePicture() {

        ImageView profileImageView = (ImageView)findViewById(R.id.view_profile_profileImage);

        final SharedPreferences preferences = getSharedPreferences("profile", 0);
        String accessToken = preferences.getString("accessToken", "");
        String username = preferences.getString("username", "");

        Authorization authorization = new Authorization();
        authorization.setUsername(username);
        authorization.setToken(accessToken);

        Gson gson = new GsonBuilder().create();

        String authorizationJson = gson.toJson(authorization);
        if (authorizationJson != null) {
            authorizationJson = authorizationJson.replace("{", "");
            authorizationJson = authorizationJson.replace("}", "");
        }

        final String authorizationJsonHeader = authorizationJson;
        OkHttpClient picassoClient = new OkHttpClient();

        picassoClient.networkInterceptors().add(new Interceptor() {

            @Override
            public com.squareup.okhttp.Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder()
                        .addHeader("authorization", authorizationJsonHeader)
                        .build();
                return chain.proceed(newRequest);
            }
        });

        Picasso picasso = new Picasso.Builder(this).downloader(new OkHttpDownloader(picassoClient)).build();
        picasso.load(Constants.SERVER_BASE_URL + "/api/v1/user-profiles/picture").into(profileImageView);
    }

    private void showProgress() {
        LinearLayout progressBarLayout = (LinearLayout)findViewById(R.id.view_profile_progressLayout);
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.view_profile_progressBar);

        if (progressBar != null && progressBarLayout != null) {
            progressBar.setVisibility(View.VISIBLE);
            progressBarLayout.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgress() {
        LinearLayout progressBarLayout = (LinearLayout)findViewById(R.id.view_profile_progressLayout);
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.view_profile_progressBar);

        if (progressBar != null && progressBarLayout != null) {
            progressBar.setVisibility(View.GONE);
            progressBarLayout.setVisibility(View.GONE);
        }

    }

    private void setProfile(final AccountProfile accountProfile) {

        TextView nameTextView = (TextView)findViewById(R.id.view_profile_name);
        TextView locationTextView = (TextView)findViewById(R.id.view_profile_location);
        TextView bioTextView = (TextView)findViewById(R.id.view_profile_bio);
        TextView memberFromTextView = (TextView)findViewById(R.id.view_profile_memberFrom);

        Button verificationButton = (Button)findViewById(R.id.view_profile_verifyButton);

        if (accountProfile.getProfile() != null) {
            Profile profile = accountProfile.getProfile();
            String name = "";
            if (profile.getFirstName() != null)
                name += profile.getFirstName();
            if (profile.getLastName() != null) {
                name += " ";
                name += profile.getLastName();
            }

            if (name.length() > 0)
                nameTextView.setText(name);

            String location = "";
            if (profile.getProvince() != null)
                location += profile.getCity();
            if (profile.getProvince() != null) {
                location += "، ";
                location += profile.getProvince();
            }

            if (location.length() > 0)
                locationTextView.setText(location);

            if (profile.getBio() != null)
                bioTextView.setText(profile.getBio());

            memberFromTextView.setText(getString(R.string.view_profile_member_from));
        }

        if (accountProfile.getVerification() == null || accountProfile.getVerification().length <= 1) {
            verificationButton.setText(getString(R.string.view_profile_verification_unverified_button));
            verificationButton.setTextColor(getResources().getColor(R.color.green));
            Drawable[] compoundDrawables = verificationButton.getCompoundDrawables();
            if (compoundDrawables != null && compoundDrawables.length > 0) {
                for (Drawable drawable:compoundDrawables) {
                    if (drawable != null)
                        drawable.setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_ATOP);
                }
            }
            verificationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openVerificationIntro(accountProfile);
                }
            });
        } else {

            boolean isVerified = true;
            if (accountProfile.getVerification().length == 3) {
                for (AccountVerification verification : accountProfile.getVerification()) {
                    if (!verification.isVerified())
                        isVerified = false;
                }

                if (isVerified) {
                    verificationButton.setText(getString(R.string.view_profile_verification_verified_button));
                    verificationButton.setTextColor(getResources().getColor(android.R.color.black));
                    Drawable[] compoundDrawables = verificationButton.getCompoundDrawables();
                    if (compoundDrawables != null && compoundDrawables.length > 0) {
                        for (Drawable drawable:compoundDrawables) {
                            if (drawable != null)
                                drawable.setColorFilter(getResources().getColor(android.R.color.black), PorterDuff.Mode.SRC_ATOP);
                        }
                    }
                    verificationButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openVerificationView(accountProfile);
                        }
                    });
                } else {
                    verificationButton.setText(getString(R.string.view_profile_verification_pending_button));
                    verificationButton.setTextColor(getResources().getColor(R.color.green));
                    Drawable[] compoundDrawables = verificationButton.getCompoundDrawables();
                    if (compoundDrawables != null && compoundDrawables.length > 0) {
                        for (Drawable drawable:compoundDrawables) {
                            if (drawable != null)
                                drawable.setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_ATOP);
                        }
                    }
                    verificationButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openVerificationView(accountProfile);
                        }
                    });
                }

            } else {

                verificationButton.setText(getString(R.string.view_profile_verification_unverified_button));
                verificationButton.setTextColor(getResources().getColor(R.color.green));
                Drawable[] compoundDrawables = verificationButton.getCompoundDrawables();
                if (compoundDrawables != null && compoundDrawables.length > 0) {
                    for (Drawable drawable:compoundDrawables) {
                        if (drawable != null)
                            drawable.setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_ATOP);
                    }
                }
                verificationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openVerificationIntro(accountProfile);
                    }
                });

            }

        }
    }

    private void openVerificationIntro(AccountProfile accountProfile) {
        Intent intent = new Intent(this, VerificationIntroActivity.class);
        intent.putExtra("accountProfile", accountProfile);
        startActivityForResult(intent, 101);
    }

    private void openVerificationView(AccountProfile accountProfile) {
        Intent intent = new Intent(this, VerificationViewActivity.class);
        intent.putExtra("accountProfile", accountProfile);
        startActivity(intent);
    }
}
