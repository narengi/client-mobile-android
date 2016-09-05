package xyz.narengi.android.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import info.semsamot.actionbarrtlizer.ActionBarRtlizer;
import info.semsamot.actionbarrtlizer.RtlizeEverything;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.narengi.android.R;
import xyz.narengi.android.common.Constants;
import xyz.narengi.android.common.dto.AccountVerification;
import xyz.narengi.android.common.dto.Authorization;
import xyz.narengi.android.common.dto.Credential;
import xyz.narengi.android.common.dto.RequestVerification;
import xyz.narengi.android.content.CredentialDeserializer;
import xyz.narengi.android.service.RetrofitApiEndpoints;


/**
 * @author Siavash Mahmoudpour
 */
public class MobileVerificationActivity extends AppCompatActivity {

    private ActionBarRtlizer rtlizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_verification);
        setupToolbar();
        initViews();

        if (getIntent() != null && getIntent().getStringExtra("verificationCode") != null) {
            String verificationCode = getIntent().getStringExtra("verificationCode");
            EditText codeEditText = (EditText) findViewById(R.id.mobile_verification_verificationCode);
            codeEditText.setText(verificationCode);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 101) {
            setResult(101);
            finish();
        } else if (requestCode == 102 || resultCode == 102) {
            finish();
        }
    }

    private void setPageTitle(String title) {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.mobile_verification_toolbar);
        if (toolbar != null) {
            TextView titleTextView = (TextView)toolbar.findViewById(R.id.text_toolbar_title);
            titleTextView.setText(title);
        }
    }

    private void setupToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.mobile_verification_toolbar);

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

        setPageTitle(getString(R.string.mobile_verification_page_title));

        if (toolbar != null) {
            ImageButton backButton = (ImageButton)toolbar.findViewById(R.id.icon_toolbar_back);
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

    protected void rtlizeActionBar() {
        if (getSupportActionBar() != null) {
//            rtlizer = new ActionBarRtlizer(this, "toolbar_actionbar");
            rtlizer = new ActionBarRtlizer(this);;
            ViewGroup homeView = (ViewGroup) rtlizer.getHomeView();
            RtlizeEverything.rtlize(rtlizer.getActionBarView());
            if (rtlizer.getHomeViewContainer() instanceof ViewGroup) {
                RtlizeEverything.rtlize((ViewGroup) rtlizer.getHomeViewContainer());
            }
            RtlizeEverything.rtlize(homeView);
            rtlizer.flipActionBarUpIconIfAvailable(homeView);
        }
    }


    private void initViews() {
        Button sendButton = (Button)findViewById(R.id.mobile_verification_sendButton);
        final EditText codeEditText = (EditText)findViewById(R.id.mobile_verification_verificationCode);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyAccount(codeEditText.getText().toString());
            }
        });
    }

    private void verifyAccount(String verificationCode) {

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
        Call<AccountVerification> call = apiEndpoints.verifyAccount("SMS", verificationCode, authorizationJson);

        call.enqueue(new Callback<AccountVerification>() {
            @Override
            public void onResponse(Response<AccountVerification> response, Retrofit retrofit) {
                int statusCode = response.code();
                AccountVerification accountVerification = response.body();
                if (accountVerification != null && accountVerification.isVerified()) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("mobileVerified", true);
                    editor.commit();
                    showResultDialog("", true);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private int getScreenWidth(Context context) {
        int measuredWidth;
        Point size = new Point();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            wm.getDefaultDisplay().getSize(size);
            measuredWidth = size.x;
        } else {
            Display d = wm.getDefaultDisplay();
            measuredWidth = d.getHeight();
        }

        return measuredWidth;
    }

    private void showResultDialog(String message, final boolean isSuccessful) {

        final View contentView = findViewById(android.R.id.content);
        contentView.setBackgroundColor(getResources().getColor(android.R.color.black));
        contentView.getBackground().setAlpha(80);

        Toast toast = new Toast(getApplicationContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_sign_up_success, null);

        TextView messageTextView = (TextView)view.findViewById(R.id.success_dialog_message);
        messageTextView.setText(R.string.mobile_verification_success_message);

        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params != null) {
            int width = (getScreenWidth(this) * 3 / 5);
            params.width = width;
            params.height = width;
            view.setLayoutParams(params);
        }

        toast.setView(view);
        toast.setDuration(Toast.LENGTH_LONG);

        int margin = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        toast.setGravity(Gravity.CENTER, 0, margin);
        toast.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                openIdentityCard();
            }
        }, 3000);
    }

    private void openIdentityCard() {
        Intent intent = new Intent(this, IdentityCardActivity.class);
        startActivityForResult(intent, 102);
    }

}
