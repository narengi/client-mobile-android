package xyz.narengi.android.ui.activity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import xyz.narengi.android.R;
import xyz.narengi.android.common.Constants;
import xyz.narengi.android.common.dto.AccountProfile;
import xyz.narengi.android.common.dto.AccountVerification;
import xyz.narengi.android.common.dto.Authorization;

/**
 * @author Siavash Mahmoudpour
 */
public class VerificationViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_view);
        setupToolbar();
        initViews();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.verification_view_toolbar);

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
        }
    }

    private void initViews() {

        if (getIntent() != null && getIntent().getSerializableExtra("accountProfile") != null) {
            AccountProfile accountProfile = (AccountProfile) getIntent().getSerializableExtra("accountProfile");
            if (accountProfile.getVerification() != null && accountProfile.getVerification().length >0) {

                TextView emailTextView = (TextView)findViewById(R.id.verification_view_email);
                TextView cellNumberTextView = (TextView)findViewById(R.id.verification_view_cellNumber);
                final ImageView identityCardImageView = (ImageView)findViewById(R.id.verification_view_identityCardImage);

                for (AccountVerification verification:accountProfile.getVerification()) {
                    if (verification.getVerificationType() != null && verification.getHandle() != null) {
                        if (verification.getVerificationType().equalsIgnoreCase("Email")) {
                            emailTextView.setText(verification.getHandle());
                        } else if (verification.getVerificationType().equalsIgnoreCase("SMS")) {
                            cellNumberTextView.setText(verification.getHandle());
                        } else if (verification.getVerificationType().equalsIgnoreCase("ID")) {
                            getIdentityCardImage(identityCardImageView);

//                            identityCardImageView.getViewTreeObserver()
//                                    .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                                        // Wait until layout to call Picasso
//                                        @Override
//                                        public void onGlobalLayout() {
//                                            // Ensure we call this only once
////                                            identityCardImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                                            getIdentityCardImage(identityCardImageView);
//                                        }
//                                    });

                        }
                    }
                }
            }
        }
    }

    private void getIdentityCardImage(ImageView identityCardImageView) {

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

        final float scale = getResources().getDisplayMetrics().density;
        int pixels = (int) (96 * scale);
        int width = 0, height = 0;
        if (identityCardImageView.getLayoutParams() != null) {
            width = identityCardImageView.getLayoutParams().width;
            height = identityCardImageView.getLayoutParams().height;
        }

        if (width <= 0 || height <= 0) {
            width = pixels;
            height = pixels;
        }

        Picasso picasso = new Picasso.Builder(this).downloader(new OkHttpDownloader(picassoClient)).build();

        picasso.load(Constants.SERVER_BASE_URL + "/api/v1/accounts/id-card").resize(pixels, pixels).centerCrop().into(identityCardImageView);

    }
}
