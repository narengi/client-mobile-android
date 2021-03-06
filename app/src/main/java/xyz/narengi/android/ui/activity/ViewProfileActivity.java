package xyz.narengi.android.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;

import info.semsamot.actionbarrtlizer.ActionBarRtlizer;
import info.semsamot.actionbarrtlizer.RtlizeEverything;
import xyz.narengi.android.R;
import xyz.narengi.android.common.Constants;
import xyz.narengi.android.common.dto.AccessToken;
import xyz.narengi.android.common.dto.AccountProfile;
import xyz.narengi.android.common.dto.AccountVerification;
import xyz.narengi.android.common.dto.Profile;
import xyz.narengi.android.service.WebService;

/**
 * @author Siavash Mahmoudpour
 */
public class ViewProfileActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 123;
    public static final int RESULT_LOGOUT = 122;

    private ActionBarRtlizer rtlizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        setupToolbar();
        showProgress();
        getProfile();
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

    private void setPageTitle(String title) {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.view_profile_toolbar);
        if (toolbar != null) {
            TextView titleTextView = (TextView) toolbar.findViewById(R.id.text_toolbar_title);
            titleTextView.setText(title);
        }
    }

    private void setupToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.view_profile_toolbar);

        /*Drawable backButtonDrawable = getResources().getDrawable(R.drawable.ic_action_back);
        backButtonDrawable.setColorFilter(getResources().getColor(android.R.color.holo_orange_dark), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationIcon(backButtonDrawable);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });*/

        setSupportActionBar(toolbar);

        if (toolbar != null) {
            ImageView backButton = (ImageView) toolbar.findViewById(R.id.icon_toolbar_back);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
//            actionBar.setHomeButtonEnabled(true);
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setDisplayShowHomeEnabled(true);
//            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

//            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.view_profile_collapse_toolbar);
//            collapsingToolbarLayout.setTitle("");
//            collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
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


    private void openEditProfile() {
        Intent intent = new Intent(this, EditProfileActivity.class);
        intent.putExtra("openedFromViewProfile", true);
        startActivityForResult(intent, 103);
    }

    private void getProfile() {


        WebService service = new WebService();
        service.setToken(AccountProfile.getLoggedInAccountProfile(this).getToken().getAuthString());
        service.setResponseHandler(new WebService.ResponseHandler() {
            @Override
            public void onPreRequest(String requestUrl) {
                showProgress();
            }

            @Override
            public void onSuccess(String requestUrl, Object response) {
                hideProgress();
                JSONObject responseObject = (JSONObject) response;
                AccountProfile profile = AccountProfile.fromJsonObject(responseObject);
                if (profile != null) {
                    setProfile(profile);
                }
            }

            @Override
            public void onError(String requestUrl, VolleyError error) {
                hideProgress();
            }
        });
    }

    private void showProgress() {
        LinearLayout progressBarLayout = (LinearLayout) findViewById(R.id.view_profile_progressLayout);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.view_profile_progressBar);

        if (progressBar != null && progressBarLayout != null) {
            progressBar.setVisibility(View.VISIBLE);
            progressBarLayout.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgress() {
        LinearLayout progressBarLayout = (LinearLayout) findViewById(R.id.view_profile_progressLayout);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.view_profile_progressBar);

        if (progressBar != null && progressBarLayout != null) {
            progressBar.setVisibility(View.GONE);
            progressBarLayout.setVisibility(View.GONE);
        }

    }

    private void setProfile(final AccountProfile accountProfile) {

        setPageTitle(accountProfile.getDisplayName());

        TextView nameTextView = (TextView) findViewById(R.id.view_profile_name);
        TextView locationTextView = (TextView) findViewById(R.id.view_profile_location);
        TextView bioTextView = (TextView) findViewById(R.id.view_profile_bio);
        TextView memberFromTextView = (TextView) findViewById(R.id.view_profile_memberFrom);

        Button verificationButton = (Button) findViewById(R.id.view_profile_verifyButton);

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

        findViewById(R.id.view_profile_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccessToken.removeAccessToken(ViewProfileActivity.this);
                setResult(RESULT_LOGOUT);
                finish();
            }
        });

        if (accountProfile.getVerifications() == null || accountProfile.getVerifications().size() <= 1) {
            verificationButton.setText(getString(R.string.view_profile_verification_unverified_button));
            verificationButton.setTextColor(getResources().getColor(R.color.green));
            Drawable[] compoundDrawables = verificationButton.getCompoundDrawables();
            if (compoundDrawables != null && compoundDrawables.length > 0) {
                for (Drawable drawable : compoundDrawables) {
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
            if (accountProfile.getVerifications().size() == 3) {
                for (AccountVerification verification : accountProfile.getVerifications()) {
                    if (!verification.isVerified())
                        isVerified = false;
                }

                if (isVerified) {
                    verificationButton.setText(getString(R.string.view_profile_verification_verified_button));
                    verificationButton.setTextColor(getResources().getColor(android.R.color.black));
                    Drawable[] compoundDrawables = verificationButton.getCompoundDrawables();
                    if (compoundDrawables != null && compoundDrawables.length > 0) {
                        for (Drawable drawable : compoundDrawables) {
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
                        for (Drawable drawable : compoundDrawables) {
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
                    for (Drawable drawable : compoundDrawables) {
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

        ImageView profileImageView = (ImageView) findViewById(R.id.view_profile_profileImage);
        Picasso.with(this).load(Constants.SERVER_BASE_URL + "/v1" + accountProfile.getAvatar()).into(profileImageView);
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
