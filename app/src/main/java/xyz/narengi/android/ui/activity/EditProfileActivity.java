package xyz.narengi.android.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.soundcloud.android.crop.Crop;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import info.semsamot.actionbarrtlizer.ActionBarRtlizer;
import info.semsamot.actionbarrtlizer.RtlizeEverything;
import ir.smartlab.persindatepicker.PersianDatePicker;
import ir.smartlab.persindatepicker.util.PersianCalendar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import xyz.narengi.android.R;
import xyz.narengi.android.common.Constants;
import xyz.narengi.android.common.dto.AccountProfile;
import xyz.narengi.android.common.dto.Profile;
import xyz.narengi.android.common.dto.ProvinceCity;
import xyz.narengi.android.service.RetrofitApiEndpoints;
import xyz.narengi.android.service.RetrofitService;
import xyz.narengi.android.service.WebService;
import xyz.narengi.android.service.WebServiceConstants;
import xyz.narengi.android.ui.adapter.SpinnerArrayAdapter;
import xyz.narengi.android.util.SecurityUtils;

/**
 * @author Siavash Mahmoudpour
 */
public class EditProfileActivity extends AppCompatActivity {

    protected static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 1001;
    protected static final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 1002;
    static final int REQUEST_IMAGE_CAPTURE = 2001;
    private static final String TAG = EditProfileActivity.class.getName();
    private static final int REQUEST_SELECT_PICTURE = 0x01;
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage.jpeg";
    private ActionBarRtlizer rtlizer;
    private AccountProfile accountProfile;
    private ImageView profileImageView;
    private Uri mDestinationUri;
    private Date selectedBirthDate;
    private Map<String, ProvinceCity[]> provincesMap;

    private AlertDialog mAlertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mDestinationUri = Uri.fromFile(new File(getCacheDir(), SAMPLE_CROPPED_IMAGE_NAME));

        setupToolbar();
        initViews();
        getProvinces();
        getProfilePicture();
        getProfile();
    }

    @Override
    public void onBackPressed() {
        setResult(102);
        finish();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 102 || resultCode == 102) {
            setResult(102);
            finish();
        } else {
            if (resultCode == RESULT_OK) {
                if (requestCode == REQUEST_SELECT_PICTURE || requestCode == REQUEST_IMAGE_CAPTURE) {
                    final Uri selectedUri = data.getData();
                    if (selectedUri != null) {
                        startCropActivity(data.getData());
                    } else {
                        Toast.makeText(EditProfileActivity.this, "toast_cannot_retrieve_selected_image", Toast.LENGTH_SHORT).show();
                    }
                } else if (requestCode == Crop.REQUEST_CROP) {
                    handleCropResult(data);
                }
            } else {
                handleCropError(data);
            }
        }
    }

    /**
     * In most cases you need only to set crop aspect ration and max size for resulting image.
     *
     * @param uCrop - ucrop builder instance
     * @return - ucrop builder instance
     */
    private UCrop basisConfig(@NonNull UCrop uCrop) {
//        switch (mRadioGroupAspectRatio.getCheckedRadioButtonId()) {
//            case R.id.radio_origin:
//                uCrop = uCrop.useSourceImageAspectRatio();
//                break;
//            case R.id.radio_square:
//                uCrop = uCrop.withAspectRatio(1, 1);
//                break;
//            case R.id.radio_dynamic:
//                // do nothing
//                break;
//            default:
//                try {
//                    float ratioX = Float.valueOf(mEditTextRatioX.getText().toString().trim());
//                    float ratioY = Float.valueOf(mEditTextRatioY.getText().toString().trim());
//                    if (ratioX > 0 && ratioY > 0) {
//                        uCrop = uCrop.withAspectRatio(ratioX, ratioY);
//                    }
//                } catch (NumberFormatException e) {
//                    Log.e(TAG, "Number please", e);
//                }
//                break;
//        }
//
//        if (mCheckBoxMaxSize.isChecked()) {
//            try {
//                int maxWidth = Integer.valueOf(mEditTextMaxWidth.getText().toString().trim());
//                int maxHeight = Integer.valueOf(mEditTextMaxHeight.getText().toString().trim());
//                if (maxWidth > 0 && maxHeight > 0) {
//                    uCrop = uCrop.withMaxResultSize(maxWidth, maxHeight);
//                }
//            } catch (NumberFormatException e) {
//                Log.e(TAG, "Number please", e);
//            }
//        }


//        uCrop = uCrop.withAspectRatio(1, 1);
        uCrop = uCrop.useSourceImageAspectRatio();

        return uCrop;
    }

    /**
     * Sometimes you want to adjust more options, it's done via {@link com.yalantis.ucrop.UCrop.Options} class.
     *
     * @param uCrop - ucrop builder instance
     * @return - ucrop builder instance
     */
    private UCrop advancedConfig(@NonNull UCrop uCrop) {
        UCrop.Options options = new UCrop.Options();
        return uCrop.withOptions(options);
    }

    private void startCropActivity(@NonNull Uri uri) {
        Crop.of(uri, mDestinationUri).asSquare().start(this, Crop.REQUEST_CROP);
    }

    private void handleCropResult(@NonNull Intent result) {
//        final Uri resultUri = UCrop.getOutput(result);
        final Uri resultUri = Crop.getOutput(result);
        if (resultUri != null) {
//            ResultActivity.startWithUri(this, resultUri);
            setCapturedImage(resultUri);
        } else {
            Toast.makeText(this, "toast_cannot_retrieve_cropped_image", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private void handleCropError(@NonNull Intent result) {
//        final Throwable cropError = UCrop.getError(result);
        Toast.makeText(this, "Error getting image.", Toast.LENGTH_SHORT).show();
//        final Throwable cropError = Crop.getError(result);
//        if (cropError != null) {
//            Log.e(TAG, "handleCropError: ", cropError);
//            Toast.makeText(this, cropError.getMessage(), Toast.LENGTH_LONG).show();
//        } else {
//            Toast.makeText(this, "toast_unexpected_error", Toast.LENGTH_SHORT).show();
//        }
    }

    private void setupToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.edit_profile_toolbar);

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
        }
    }

    private void setPageTitle(String title) {
        TextView titleTextView = (TextView) findViewById(R.id.text_toolbar_title);
        titleTextView.setText(title);
    }

    private void initViews() {

        profileImageView = (ImageView) findViewById(R.id.edit_profile_image);

        final Spinner genderSpinner = (Spinner) findViewById(R.id.edit_profile_gender);
        String[] genderArray = getResources().getStringArray(R.array.gender_array);

        SpinnerArrayAdapter<CharSequence> arrayAdapter = new SpinnerArrayAdapter<CharSequence>(this,
                android.R.layout.simple_spinner_item, genderArray);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(arrayAdapter);
        genderSpinner.setPromptId(R.string.edit_profile_gender_hint);

        findViewById(R.id.llBirthdayContainer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBirthDateDialog();
            }
        });
        findViewById(R.id.edit_profile_birthDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.llBirthdayContainer).performClick();
            }
        });
        findViewById(R.id.llGenderContainer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                genderSpinner.performClick();
            }
        });
        findViewById(R.id.llProvinceContainer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.edit_profile_province).performClick();
            }
        });
        findViewById(R.id.llCityContainer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.edit_profile_city).performClick();
            }
        });

        Button addImageButton = (Button) findViewById(R.id.edit_profile_addPhotoButton);
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                dispatchTakePictureIntent();
                selectImage();
            }
        });

        findViewById(R.id.tvSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });
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


    private void showBirthDateDialog() {
        LayoutInflater inflater = getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = inflater.inflate(R.layout.dialog_birth_date, null);

        final PersianDatePicker birthDatePicker = (PersianDatePicker) view.findViewById(R.id.edit_profile_birthDatePicker);
        birthDatePicker.setDisplayDate(new Date());

        builder.setView(view);


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                EditText birthDateEditText = (EditText) findViewById(R.id.edit_profile_birthDate);
                birthDateEditText.setText(birthDatePicker.getDisplayPersianDate().getPersianShortDate());
                selectedBirthDate = birthDatePicker.getDisplayDate();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
    }

    private void setCapturedImage(Uri resultUri) {

        // Get the dimensions of the View
        int targetW = profileImageView.getWidth();
        int targetH = profileImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(resultUri.getPath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        if (targetW > 0 && targetH > 0) {
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
            bmOptions.inSampleSize = scaleFactor;

        }
        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(resultUri.getPath(), bmOptions);
        profileImageView.setImageBitmap(bitmap);


//        profileImageView.setImageURI(resultUri);
        uploadProfilePicture(resultUri);


//            profileImageView.setImageBitmap(photo);

//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(new File(getIntent().getData().getPath()).getAbsolutePath(), options);
    }

    private void selectImage() {
        final CharSequence[] items = getResources().getStringArray(R.array.profile_capture_photo_array);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                switch (item) {
                    case 0:
                        dispatchTakePictureIntent();
                        break;
                    case 1:
                        pickFromGallery();
                        break;
                    case 2:
                        dialog.dismiss();
                        break;
                }
            }
        });

//        builder.show();

        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
    }

    private void dispatchTakePictureIntent() {
//
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void pickFromGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
//                    getString(R.string.permission_read_storage_rationale),
                    "permission_read_storage_rationale",
                    REQUEST_STORAGE_READ_ACCESS_PERMISSION);
        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
//            startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_picture)), REQUEST_SELECT_PICTURE);
            startActivityForResult(Intent.createChooser(intent, "label_select_picture"), REQUEST_SELECT_PICTURE);
        }
    }

    /**
     * Requests given permission.
     * If the permission has been denied previously, a Dialog will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    protected void requestPermission(final String permission, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
//            showAlertDialog(getString(R.string.permission_title_rationale), rationale,
            showAlertDialog("permission_title_rationale", rationale,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(EditProfileActivity.this,
                                    new String[]{permission}, requestCode);
                        }
                    }, "Ok", null, "Cancel");
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }

    /**
     * This method shows dialog with given title & message.
     * Also there is an option to pass onClickListener for positive & negative button.
     *
     * @param title                         - dialog title
     * @param message                       - dialog message
     * @param onPositiveButtonClickListener - listener for positive button
     * @param positiveText                  - positive button text
     * @param onNegativeButtonClickListener - listener for negative button
     * @param negativeText                  - negative button text
     */
    protected void showAlertDialog(@Nullable String title, @Nullable String message,
                                   @Nullable DialogInterface.OnClickListener onPositiveButtonClickListener,
                                   @NonNull String positiveText,
                                   @Nullable DialogInterface.OnClickListener onNegativeButtonClickListener,
                                   @NonNull String negativeText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveText, onPositiveButtonClickListener);
        builder.setNegativeButton(negativeText, onNegativeButtonClickListener);
        mAlertDialog = builder.show();
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE_READ_ACCESS_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickFromGallery();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void getProfilePicture() {

        final String authorizationJsonHeader = AccountProfile.getLoggedInAccountProfile(this).getToken().getAuthString();
        OkHttpClient picassoClient = new OkHttpClient();

        picassoClient.networkInterceptors().add(new Interceptor() {

            @Override
            public com.squareup.okhttp.Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder()
                        .addHeader("access-token", authorizationJsonHeader)
                        .build();
                return chain.proceed(newRequest);
            }
        });

        Picasso picasso = new Picasso.Builder(this).downloader(new OkHttpDownloader(picassoClient)).build();
        picasso.load(Constants.SERVER_BASE_URL + "/api/v1/user-profiles/picture").into(profileImageView);
    }

    private void getProvinces() {

        provincesMap = new HashMap<>();
        provincesMap.clear();
        WebService service = new WebService();
        service.setResponseHandler(new WebService.ResponseHandler() {
            @Override
            public void onPreRequest(String requestUrl) {

            }

            @Override
            public void onSuccess(String requestUrl, Object response) {
                JSONObject responseObject = (JSONObject) response;
                Iterator<String> keys = responseObject.keys();
                while (keys.hasNext()) {
                    try {
                        String provinceName = keys.next();
                        JSONArray provinceCitiesArray = responseObject.getJSONArray(provinceName);
                        ProvinceCity[] cities = new ProvinceCity[provinceCitiesArray.length()];
                        for (int i = 0; i < provinceCitiesArray.length(); i++) {
                            JSONObject cityObject = provinceCitiesArray.getJSONObject(i);
                            String cityName = cityObject.getString("city");
                            ProvinceCity result = new ProvinceCity();
                            result.setCity(cityName);

                            cities[i] = result;
                        }

                        provincesMap.put(provinceName, cities);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (provincesMap != null && !provincesMap.isEmpty()) {
                    initProvinceSpinner(provincesMap);
                }

            }

            @Override
            public void onError(String requestUrl, VolleyError error) {

            }
        });

        service.getJsonObject(WebServiceConstants.ProvinceCity.GET_PROVINCE_CITIES);
    }

    private void initProvinceSpinner(final Map<String, ProvinceCity[]> provincesMap) {
        Spinner provinceSpinner = (Spinner) findViewById(R.id.edit_profile_province);
        final Spinner citySpinner = (Spinner) findViewById(R.id.edit_profile_city);
        final String[] provinceArray = new String[provincesMap.keySet().size()];
        provincesMap.keySet().toArray(provinceArray);

        SpinnerArrayAdapter<CharSequence> provinceArrayAdapter = new SpinnerArrayAdapter<CharSequence>(this,
                android.R.layout.simple_spinner_item, provinceArray);
        provinceArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        provinceSpinner.setAdapter(provinceArrayAdapter);
        provinceSpinner.setPromptId(R.string.edit_profile_province_hint);

        provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (provinceArray.length > position) {
                    String province = provinceArray[position];
                    ProvinceCity[] cityArray = provincesMap.get(province);
                    if (cityArray != null && cityArray.length > 0) {
                        SpinnerArrayAdapter<ProvinceCity> cityArrayAdapter = new SpinnerArrayAdapter<ProvinceCity>(EditProfileActivity.this,
                                android.R.layout.simple_spinner_item, cityArray);
                        cityArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        citySpinner.setAdapter(cityArrayAdapter);
                        citySpinner.setPromptId(R.string.edit_profile_city_hint);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getProfile() {
        WebService service = new WebService();
        service.setResponseHandler(new WebService.ResponseHandler() {
            @Override
            public void onPreRequest(String requestUrl) {

            }

            @Override
            public void onSuccess(String requestUrl, Object response) {
                JSONObject responseObject = (JSONObject) response;
                AccountProfile loggedInProfile = AccountProfile.fromJsonObject(responseObject);
                if (loggedInProfile != null) {
                    loggedInProfile.saveToSharedPref(EditProfileActivity.this);
                    setProfile(loggedInProfile);
                } else {
                    // TODO: 11/3/2016 AD show error
                    Log.d("User", "logged in user is empty");
                }
            }

            @Override
            public void onError(String requestUrl, VolleyError error) {
                error.printStackTrace();
            }
        });
        service.setToken(AccountProfile.getLoggedInAccountProfile(this).getToken().getAuthString());
        service.getJsonObject(WebServiceConstants.Accounts.ME);
    }

    private void uploadProfilePicture(Uri resultUri) {
        Retrofit retrofit = RetrofitService.getInstance().getRetrofit();

        File file = new File(resultUri.getPath());
//        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);


        RequestBody photoRequestBody = RequestBody.create(MediaType.parse("application/image"), file);
        RequestBody requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart("picture", file.getName(), photoRequestBody)
                .build();

        RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);
        Call<AccountProfile> call = apiEndpoints.uploadProfilePicture(requestBody);

        call.enqueue(new Callback<AccountProfile>() {
            @Override
            public void onResponse(Call<AccountProfile> call, Response<AccountProfile> response) {
                int statusCode = response.code();
                Toast.makeText(EditProfileActivity.this, "Status : " + String.valueOf(statusCode), Toast.LENGTH_LONG).show();
                if (statusCode == 201 || statusCode == 204) {
                    Toast.makeText(EditProfileActivity.this, "Upload image success... :  " + String.valueOf(statusCode), Toast.LENGTH_LONG).show();

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
            public void onFailure(Call<AccountProfile> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Upload image exception : " + t.getMessage(), Toast.LENGTH_LONG).show();
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
        Spinner provinceSpinner = (Spinner) findViewById(R.id.edit_profile_province);
        Spinner citySpinner = (Spinner) findViewById(R.id.edit_profile_city);
        EditText bioEditText = (EditText) findViewById(R.id.edit_profile_bio);

        profile.setFirstName(nameEditText.getText().toString());
        profile.setLastName(familyEditText.getText().toString());
        if (genderSpinner.getSelectedItemPosition() == 0) {
            profile.setGender("Male");
        } else {
            profile.setGender("Female");
        }

        if (provinceSpinner.getSelectedItem() != null && provinceSpinner.getSelectedItem().toString() != null)
            profile.setProvince(provinceSpinner.getSelectedItem().toString());
        if (citySpinner.getSelectedItem() != null && citySpinner.getSelectedItem().toString() != null)
            profile.setCity(citySpinner.getSelectedItem().toString());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        String dateString = dateFormat.format(new Date());
        if (selectedBirthDate != null) {
            String dateString = dateFormat.format(selectedBirthDate);
            profile.setBirthDate(dateString);
        }

        profile.setBio(bioEditText.getText().toString());
        Retrofit retrofit = RetrofitService.getInstance().getRetrofit();

        RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);
        Call call = apiEndpoints.updateProfile(profile);

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
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
            public void onFailure(Call call, Throwable t) {
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
        editor.commit();
    }

    private void showUpdateProfileResultDialog(String message, final boolean isSuccessful) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//        builder.setTitle("Update Profile Result");
//        if (message != null) {
//            builder.setMessage(message);
//        } else {
//            builder.setMessage("Profile updated successfully!");
//        }
//
//        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                if (isSuccessful) {
//                    setResult(102);
//                    finish();
//                }
//            }
//        });
//
//        AlertDialog dialog = builder.create();
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.show();


        Toast toast = new Toast(getApplicationContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_sign_up_success, null);
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
//                openViewProfile();
                setResult(401);
                finish();
                //??? finish this, and go to explore activity, and start view profile activity from there ???
            }
        }, 3000);
    }

    private void openViewProfile() {

        if (getIntent() != null && getIntent().getBooleanExtra("openedFromViewProfile", false)) {
            finish();
        } else {
            Intent intent = new Intent(this, ViewProfileActivity.class);
            startActivityForResult(intent, 104);
        }
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

    private void setProfile(AccountProfile accountProfile) {
        this.accountProfile = accountProfile;

        setPageTitle(accountProfile.getDisplayName());

        EditText nameEditText = (EditText) findViewById(R.id.edit_profile_name);
        EditText familyEditText = (EditText) findViewById(R.id.edit_profile_family);
        Spinner genderSpinner = (Spinner) findViewById(R.id.edit_profile_gender);
        if (accountProfile.getProfile().getBirthDate() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            try {
                Date birthDate = dateFormat.parse(accountProfile.getProfile().getBirthDate());
                if (birthDate != null) {
                    selectedBirthDate = birthDate;
                    EditText birthDateEditText = (EditText) findViewById(R.id.edit_profile_birthDate);
                    PersianCalendar calendar = new PersianCalendar(birthDate.getTime());
                    birthDateEditText.setText(calendar.getPersianShortDate());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Spinner provinceSpinner = (Spinner) findViewById(R.id.edit_profile_province);
        Spinner citySpinner = (Spinner) findViewById(R.id.edit_profile_city);
        EditText bioEditText = (EditText) findViewById(R.id.edit_profile_bio);

//        if (accountProfile.getVerifications() != null && accountProfile.getVerifications().length > 0) {
//            for (AccountVerification verification : accountProfile.getVerifications()) {
//                if (verification.getVerificationType() != null) {
//                    if (verification.getVerificationType().equals("SMS")) {
//                        citySpinner.setText(verification.getHandle());
//                    } else if (verification.getVerificationType().equals("Email")) {
//                        provinceSpinner.setText(verification.getHandle());
//                    }
//                }
//            }
//        }

        if (accountProfile.getProfile().getFirstName() != null)
            nameEditText.setText(accountProfile.getProfile().getFirstName());
        if (accountProfile.getProfile().getLastName() != null)
            familyEditText.setText(accountProfile.getProfile().getLastName());

        if (provincesMap != null && !provincesMap.isEmpty()) {
            ProvinceCity[] cityArray = null;

            if (accountProfile.getProfile().getProvince() != null && accountProfile.getProfile().getProvince().length() > 0) {
                if (provincesMap.containsKey(accountProfile.getProfile().getProvince())) {
                    final String[] provinceArray = new String[provincesMap.keySet().size()];
                    provincesMap.keySet().toArray(provinceArray);
                    for (int i = 0; i < provinceArray.length; i++) {
                        if (accountProfile.getProfile().getProvince().equalsIgnoreCase(provinceArray[i])) {
                            provinceSpinner.setSelection(i);
                            cityArray = provincesMap.get(accountProfile.getProfile().getProvince());
                        }
                    }
                }
            }

            if (cityArray != null && accountProfile.getProfile().getCity() != null &&
                    accountProfile.getProfile().getCity().length() > 0) {
                for (int i = 0; i < cityArray.length; i++) {
                    ProvinceCity provinceCity = cityArray[i];
                    if (accountProfile.getProfile().getCity().equalsIgnoreCase(provinceCity.getCity())) {
                        citySpinner.setSelection(i);
                    }
                }
            }
        }

//        if (accountProfile.getProfile().getBirthDate() != null)
//            birthDatePicker.setText(accountProfile.getProfile().getBirthDate());
        if (accountProfile.getProfile().getBio() != null)
            bioEditText.setText(accountProfile.getProfile().getBio());

        if (accountProfile.getProfile().getGender() != null) {
            if (accountProfile.getProfile().getGender().equals("Male")) {
                genderSpinner.setSelection(0);
            } else {
                genderSpinner.setSelection(1);
            }
        }

        //TODO : set province and city values.
    }
}
