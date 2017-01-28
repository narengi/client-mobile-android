package xyz.narengi.android.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import info.semsamot.actionbarrtlizer.ActionBarRtlizer;
import info.semsamot.actionbarrtlizer.RtlizeEverything;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.AccountVerification;
import xyz.narengi.android.common.dto.RequestVerification;
import xyz.narengi.android.armin.model.network.RetrofitApiEndpoints;
import xyz.narengi.android.service.RetrofitService;

/**
 * @author Siavash Mahmoudpour
 */
public class MobileInputActivity extends AppCompatActivity {


    private ActionBarRtlizer rtlizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_input);
        setupToolbar();
        initViews();
    }

    private void setPageTitle(String title) {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.mobile_input_toolbar);
        if (toolbar != null) {
            TextView titleTextView = (TextView) toolbar.findViewById(R.id.text_toolbar_title);
            titleTextView.setText(title);
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            setResult(101);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 102 || resultCode == 102 || resultCode == 101) {
            setResult(101);
            finish();
        }
    }

    private void setupToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.mobile_input_toolbar);

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

        setPageTitle(getString(R.string.mobile_input_page_title));

        if (toolbar != null) {
            ImageButton backButton = (ImageButton) toolbar.findViewById(R.id.icon_toolbar_back);
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
        }
    }

    private void initViews() {
        Button sendButton = (Button) findViewById(R.id.mobile_input_sendButton);
        final EditText mobileNoEditText = (EditText) findViewById(R.id.mobile_input_mobileNumber);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestVerification(mobileNoEditText.getText().toString());
            }
        });
    }


    private void requestVerification(String mobileNo) {
        final RequestVerification requestVerification = new RequestVerification();
        requestVerification.setHandle(mobileNo);

        Retrofit retrofit = RetrofitService.getInstance().getRetrofit();

        RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);
        Call<AccountVerification> call = apiEndpoints.requestVerification("SMS", requestVerification);

        call.enqueue(new Callback<AccountVerification>() {
            @Override
            public void onResponse(Call<AccountVerification> call, Response<AccountVerification> response) {
                int statusCode = response.code();
                AccountVerification accountVerification = response.body();
                if (accountVerification != null && accountVerification.getCode() != null) {
                    Toast.makeText(MobileInputActivity.this, "Request verification, code : " + accountVerification.getCode(), Toast.LENGTH_LONG).show();
                    openMobileVerification(accountVerification.getCode());
                }
            }

            @Override
            public void onFailure(Call<AccountVerification> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void openMobileVerification(String verificationCode) {

        Intent intent = new Intent(this, MobileVerificationActivity.class);
        intent.putExtra("verificationCode", verificationCode);
        startActivityForResult(intent, 102);
    }
}
