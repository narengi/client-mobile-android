package xyz.narengi.android.ui.activity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import xyz.narengi.android.ui.adapter.SpinnerArrayAdapter;
import xyz.narengi.android.util.SecurityUtils;

/**
 * @author Siavash Mahmoudpour
 */
public class EditProfileActivity extends AppCompatActivity {

    private AccountProfile accountProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        setupToolbar();
        initViews();
        getProfile();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_profile_menu, menu);
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
        } else if (id == R.id.edit_profile_save) {
            Toast.makeText(this, "Saved, hehe...", Toast.LENGTH_LONG).show();
            updateProfile();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.edit_profile_toolbar);

        Drawable backButtonDrawable = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
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

        Spinner genderSpinner = (Spinner) findViewById(R.id.edit_profile_gender);
        String[] genderArray = getResources().getStringArray(R.array.gender_array);

        SpinnerArrayAdapter<CharSequence> arrayAdapter = new SpinnerArrayAdapter<CharSequence>(this,
                android.R.layout.simple_spinner_item, genderArray);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(arrayAdapter);
        genderSpinner.setPromptId(R.string.edit_profile_gender_hint);
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
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
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
                int statusCode = response.code();
                AccountProfile accountProfile = response.body();
                if (accountProfile != null && accountProfile.getProfile() != null) {
                    setProfile(accountProfile);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void updateProfile() {

        Profile profile;
        if (accountProfile != null && accountProfile.getProfile() != null) {
            profile = accountProfile.getProfile();
        } else {
            profile = new Profile();
        }

        final EditText nameEditText = (EditText) findViewById(R.id.edit_profile_name);
        final EditText familyEditText = (EditText) findViewById(R.id.edit_profile_family);
        Spinner genderSpinner = (Spinner) findViewById(R.id.edit_profile_gender);
        EditText birthDateEditText = (EditText) findViewById(R.id.edit_profile_birthDate);
        EditText emailEditText = (EditText) findViewById(R.id.edit_profile_email);
        EditText mobileNoEditText = (EditText) findViewById(R.id.edit_profile_mobileNumber);
        EditText bioEditText = (EditText) findViewById(R.id.edit_profile_bio);

        profile.setFirstName(nameEditText.getText().toString());
        profile.setLastName(familyEditText.getText().toString());
        if (genderSpinner.getSelectedItemPosition() == 0) {
            profile.setGender("Male");
        } else {
            profile.setGender("Female");
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = dateFormat.format(new Date());
//        profile.setBirthDate(birthDateEditText.getText().toString());
        profile.setBirthDate(dateString);
        profile.setBio(bioEditText.getText().toString());

        final SharedPreferences preferences = getSharedPreferences("profile", 0);
        String accessToken = preferences.getString("accessToken", "");
        String username = preferences.getString("username", "");

        Authorization authorization = new Authorization();
        authorization.setUsername(username);
        authorization.setToken(accessToken);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Credential.class, new CredentialDeserializer())
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
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
        Call call = apiEndpoints.updateProfile(authorizationJson, profile);

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Response response, Retrofit retrofit) {
                int statusCode = response.code();
                if (statusCode == 201 || statusCode == 204) {
                    saveDisplayName(nameEditText.getText().toString(), familyEditText.getText().toString());
                    SecurityUtils.getInstance(EditProfileActivity.this).setUpdateUserTitleNeeded(true);
                    showUpdateProfileResultDialog(null, true);
                } else {
                    try {
                        if (response.errorBody() != null) {
                            Toast.makeText(EditProfileActivity.this, response.errorBody().string(), Toast.LENGTH_LONG).show();
                            showUpdateProfileResultDialog(response.errorBody().string(), false);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void saveDisplayName(String name, String family) {
        String displayName = "";
        if (name != null)
            displayName += name;
        if (family != null)
            displayName += " " + family;

        SharedPreferences preferences = getSharedPreferences("profile", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("displayName", displayName);
    }

    private void showUpdateProfileResultDialog(String message, final boolean isSuccessful) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Update Profile Result");
        if (message != null) {
            builder.setMessage(message);
        } else {
            builder.setMessage("Profile updated successfully!");
        }

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (isSuccessful) {
                    setResult(102);
                    finish();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void setProfile(AccountProfile accountProfile) {
        this.accountProfile = accountProfile;

        EditText nameEditText = (EditText) findViewById(R.id.edit_profile_name);
        EditText familyEditText = (EditText) findViewById(R.id.edit_profile_family);
        Spinner genderSpinner = (Spinner) findViewById(R.id.edit_profile_gender);
        EditText birthDateEditText = (EditText) findViewById(R.id.edit_profile_birthDate);
        EditText emailEditText = (EditText) findViewById(R.id.edit_profile_email);
        EditText mobileNoEditText = (EditText) findViewById(R.id.edit_profile_mobileNumber);
        EditText bioEditText = (EditText) findViewById(R.id.edit_profile_bio);

        if (accountProfile.getVerification() != null && accountProfile.getVerification().length > 0) {
            for (AccountVerification verification : accountProfile.getVerification()) {
                if (verification.getVerificationType() != null) {
                    if (verification.getVerificationType().equals("SMS")) {
                        mobileNoEditText.setText(verification.getHandle());
                    } else if (verification.getVerificationType().equals("Email")) {
                        emailEditText.setText(verification.getHandle());
                    }
                }
            }
        }

        if (accountProfile.getProfile().getFirstName() != null)
            nameEditText.setText(accountProfile.getProfile().getFirstName());
        if (accountProfile.getProfile().getLastName() != null)
            familyEditText.setText(accountProfile.getProfile().getLastName());
        if (accountProfile.getProfile().getBirthDate() != null)
            birthDateEditText.setText(accountProfile.getProfile().getBirthDate());
        if (accountProfile.getProfile().getBio() != null)
            bioEditText.setText(accountProfile.getProfile().getBio());

        if (accountProfile.getProfile().getGender() != null) {
            if (accountProfile.getProfile().getGender().equals("Male")) {
                genderSpinner.setSelection(0);
            } else {
                genderSpinner.setSelection(1);
            }
        }
    }
}
