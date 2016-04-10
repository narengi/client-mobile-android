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
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.narengi.android.R;
import xyz.narengi.android.common.Constants;
import xyz.narengi.android.common.dto.AccountProfile;
import xyz.narengi.android.common.dto.AccountVerification;
import xyz.narengi.android.common.dto.AroundLocation;
import xyz.narengi.android.common.dto.AroundPlaceAttraction;
import xyz.narengi.android.common.dto.AroundPlaceCity;
import xyz.narengi.android.common.dto.AroundPlaceHouse;
import xyz.narengi.android.common.dto.Credential;
import xyz.narengi.android.common.dto.Profile;
import xyz.narengi.android.content.AroundLocationDeserializer;
import xyz.narengi.android.content.AroundPlaceAttractionDeserializer;
import xyz.narengi.android.content.AroundPlaceCityDeserializer;
import xyz.narengi.android.content.AroundPlaceHouseDeserializer;
import xyz.narengi.android.content.CredentialDeserializer;
import xyz.narengi.android.service.RetrofitApiEndpoints;
import xyz.narengi.android.ui.fragment.SignInFragment;
import xyz.narengi.android.ui.fragment.SignUpFragment;

/**
 * @author Siavash Mahmoudpour
 */
public class SignInSignUpActivity extends AppCompatActivity implements SignUpFragment.OnRegisterButtonClickListener, SignInFragment.OnLoginButtonClickListener {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_sign_up);
        setupToolbar();
//        initViews();

        viewPager = (ViewPager) findViewById(R.id.login_viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.login_tabs);
        tabLayout.setupWithViewPager(viewPager);
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
        }
        return super.onOptionsItemSelected(item);
    }


    private void setupToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.login_toolbar);

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

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SignUpFragment(), getString(R.string.register_button));
        adapter.addFragment(new SignInFragment(), getString(R.string.login_button));
        viewPager.setAdapter(adapter);

        viewPager.setCurrentItem(1);
    }

    private void initViews() {

//        Button loginButton = (Button)findViewById(R.id.login_loginButton);
//        Button registerButton = (Button)findViewById(R.id.login_registerButton);

//        loginButton.setOnClickListener(this);
//        registerButton.setOnClickListener(this);
    }

//    @Override
    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.login_loginButton:
//                break;
//            case R.id.login_registerButton:
//                register();
//                break;
//        }
    }

    private void login(String email, String password) {
        Credential credential = new Credential();

        credential.setUsername(email);
        credential.setPassword(password);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Credential.class, new CredentialDeserializer())
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.SERVER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);
        Call<AccountProfile> call = apiEndpoints.login(credential);

        call.enqueue(new Callback<AccountProfile>() {
            @Override
            public void onResponse(Response<AccountProfile> response, Retrofit retrofit) {
                int statusCode = response.code();
                AccountProfile accountProfile = response.body();
                if (accountProfile != null && accountProfile.getToken() != null) {
                    loginUser(accountProfile);
                } else {
                    try {
                        if (response.errorBody() != null) {
                            String failureMessage = response.errorBody().string();
                            Toast.makeText(SignInSignUpActivity.this, "Login failure, status code : " + String.valueOf(statusCode) + "\n" + failureMessage, Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


                if (statusCode != 200) {
                    Toast.makeText(SignInSignUpActivity.this, "Login failure, status code : " + String.valueOf(statusCode), Toast.LENGTH_LONG).show();
                    //Failure
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void register(String email, String password) {

        Credential credential = new Credential();

        credential.setEmail(email);
        credential.setPassword(password);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Credential.class, new CredentialDeserializer())
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.SERVER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);
        Call<AccountProfile> call = apiEndpoints.register(credential);

        call.enqueue(new Callback<AccountProfile>() {
            @Override
            public void onResponse(Response<AccountProfile> response, Retrofit retrofit) {
                int statusCode = response.code();
                if (statusCode != 201) {
                    Toast.makeText(SignInSignUpActivity.this, "Register failure, status code : " + String.valueOf(statusCode), Toast.LENGTH_LONG).show();
                    //Failure
                } else {
                    AccountProfile accountProfile = response.body();
                    if (accountProfile != null && accountProfile.getToken() != null) {
//                        Toast.makeText(SignInSignUpActivity.this, "Register success, getCreatedAt : " + accountProfile.getCreatedAt(), Toast.LENGTH_LONG).show();
                        createUser(accountProfile);
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void loginUser(AccountProfile accountProfile) {
        SharedPreferences preferences = getSharedPreferences("profile", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("accessToken", accountProfile.getToken().getToken());
        editor.putString("username", accountProfile.getToken().getUsername());
        editor.commit();

        if (accountProfile.getProfile() != null) {
            Profile profile = accountProfile.getProfile();
            String displayName = "";
            if (profile.getFirstName() != null)
                displayName += profile.getFirstName();
            if (profile.getLastName() != null)
                displayName += " " + profile.getLastName();

            editor.putString("displayName", displayName);
            editor.commit();

            boolean isPictureUploaded = true;
            if (profile.getStatus() != null && profile.getStatus().getFields() != null) {
                for (String field : profile.getStatus().getFields()) {
                    if (field.equalsIgnoreCase("picture")) {
                        isPictureUploaded = false;
                        break;
                    }
                }
            }

            if (isPictureUploaded) {
                //TODO : get profile picture form server.
            }
        }

        setResult(303);
        finish();

        /*if (accountProfile.getProfile() != null && accountProfile.getProfile().getStatus() != null &&
                accountProfile.getProfile().getStatus().getCompleted() == 100) {
            //TODO : finish();
        } else {
            if (accountProfile.getVerification() == null || accountProfile.getVerification().length == 0) {
                //TODO : open mobile verification
            } else {
                AccountVerification mobileVerification = null;
                for (AccountVerification verification : accountProfile.getVerification()) {
                    if (verification.getVerificationType() != null && verification.getVerificationType().equalsIgnoreCase("SMS")) {
                        mobileVerification = verification;
                        break;
                    }
                }

                if (mobileVerification != null && mobileVerification.isVerified()) {
                    //TODO : open edit profile
                } else {
                    //TODO : open mobile verification
                }
            }
        }

        Intent intent = new Intent(this, MobileInputActivity.class);
        startActivityForResult(intent, 101);*/
    }

    private void createUser(AccountProfile accountProfile) {
        SharedPreferences preferences = getSharedPreferences("profile", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("accessToken", accountProfile.getToken().getToken());
        editor.putString("username", accountProfile.getToken().getUsername());
        editor.commit();

        setResult(302);
        finish();

//        Intent intent = new Intent(this, SignUpConfirmActivity.class);
//        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 || resultCode == 101) {
            finish();
        }
    }

    @Override
    public void onRegisterButtonPressed(String email, String password) {
        register(email, password);

//        Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Registration Successful!", Snackbar.LENGTH_LONG);
//        View view = snack.getView();
//        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
//        params.gravity = Gravity.CENTER;
//        params.rightMargin = 100;
//        params.leftMargin = 100;
//        view.setLayoutParams(params);
//        snack.show();


//        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "", Snackbar.LENGTH_LONG);
//        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
//        TextView textView = (TextView) layout.findViewById(android.support.design.R.id.snackbar_text);
//        textView.setVisibility(View.INVISIBLE);
//
//        LayoutInflater inflater = LayoutInflater.from(this);
//        View snackView = inflater.inflate(R.layout.dialog_sign_up_success, null);
//        layout.setGravity(Gravity.CENTER);
//
//        layout.addView(snackView, 0);
//
//        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)layout.getLayoutParams();
//        params.gravity = Gravity.CENTER;
//        params.rightMargin = 100;
//        params.leftMargin = 100;
//        layout.setLayoutParams(params);
//        layout.setBackgroundResource(R.drawable.snackbar_bg);
//
//        snackbar.show();
    }

    @Override
    public void onLoginButtonPressed(String email, String password) {
        login(email, password);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
