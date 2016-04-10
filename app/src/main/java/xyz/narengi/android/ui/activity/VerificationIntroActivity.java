package xyz.narengi.android.ui.activity;

import android.content.Intent;
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
import android.widget.Button;

import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.AccountProfile;
import xyz.narengi.android.common.dto.AccountVerification;

/**
 * @author Siavash Mahmoudpour
 */
public class VerificationIntroActivity extends AppCompatActivity {

    private boolean isMobileVerified = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_intro);

        if (getIntent() != null && getIntent().getSerializableExtra("accountProfile") != null) {
            AccountProfile accountProfile = (AccountProfile)getIntent().getSerializableExtra("accountProfile");
            if (accountProfile.getVerification() != null && accountProfile.getVerification().length >= 1) {
                for (AccountVerification verification : accountProfile.getVerification()) {
                    if (verification.getVerificationType() != null && verification.getVerificationType().equalsIgnoreCase("sms") &&
                            verification.isVerified()) {
                        isMobileVerified = true;
                    }
                }
            }
        }

        setupToolbar();

        Button startButton = (Button)findViewById(R.id.verification_intro_startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isMobileVerified) {
                    Intent intent = new Intent(VerificationIntroActivity.this, IdentityCardActivity.class);
                    startActivityForResult(intent, 101);
                } else {
                    Intent intent = new Intent(VerificationIntroActivity.this, MobileInputActivity.class);
                    startActivityForResult(intent, 101);
                }
            }
        });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 || resultCode == 101) {
            setResult(101);
            finish();
        }
    }

    private void setupToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.verification_intro_toolbar);

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

}
