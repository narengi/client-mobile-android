package xyz.narengi.android.ui.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import info.semsamot.actionbarrtlizer.ActionBarRtlizer;
import info.semsamot.actionbarrtlizer.RtlizeEverything;
import xyz.narengi.android.R;
import xyz.narengi.android.common.Constants;
import xyz.narengi.android.common.dto.AccessToken;
import xyz.narengi.android.common.dto.AccountProfile;
import xyz.narengi.android.common.dto.AccountVerification;

/**
 * @author Siavash Mahmoudpour
 */
public class VerificationViewActivity extends AppCompatActivity {


    private ActionBarRtlizer rtlizer;

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

        setPageTitle(getString(R.string.verification_view_page_title));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
//            actionBar.setHomeButtonEnabled(true);
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setDisplayShowHomeEnabled(true);
//            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    private void setPageTitle(String title) {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.verification_view_toolbar);
        if (toolbar != null) {
            TextView titleTextView = (TextView) toolbar.findViewById(R.id.text_toolbar_title);
            titleTextView.setText(title);
        }
    }

    private void initViews() {

        if (getIntent() != null && getIntent().getSerializableExtra("accountProfile") != null) {
            AccountProfile accountProfile = (AccountProfile) getIntent().getSerializableExtra("accountProfile");
            if (accountProfile.getVerifications() != null && accountProfile.getVerifications().size() > 0) {

                TextView emailTextView = (TextView) findViewById(R.id.verification_view_email);
                TextView cellNumberTextView = (TextView) findViewById(R.id.verification_view_cellNumber);
                final ImageView identityCardImageView = (ImageView) findViewById(R.id.verification_view_identityCardImage);

                for (AccountVerification verification : accountProfile.getVerifications()) {
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


    @SuppressWarnings("ConstantConditions")
    private void getIdentityCardImage(ImageView identityCardImageView) {

        final String authorizationJsonHeader = AccountProfile.getLoggedInAccountProfile(this).getToken().getAuthString();
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
