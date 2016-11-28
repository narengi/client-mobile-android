package xyz.narengi.android.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.CirclePageIndicator;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import xyz.narengi.android.BuildConfig;
import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.ImageInfo;
import xyz.narengi.android.common.dto.RemoveHouseImagesInfo;
import xyz.narengi.android.service.RetrofitApiEndpoints;
import xyz.narengi.android.service.RetrofitService;
import xyz.narengi.android.ui.activity.AddHouseActivity;
import xyz.narengi.android.ui.activity.EditHouseDetailActivity;
import xyz.narengi.android.ui.activity.EditProfileActivity;
import xyz.narengi.android.ui.adapter.HouseEntryImageThumbnailsRecyclerAdapter;
import xyz.narengi.android.ui.adapter.HouseImageEntryViewPagerAdapter;
import xyz.narengi.android.ui.util.AlertUtils;

/**
 * @author Siavash Mahmoudpour
 */
public class HouseImagesEntryFragment extends HouseEntryBaseFragment implements HouseEntryImageThumbnailsRecyclerAdapter.OnAddImageClickListener {

    protected static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 1002;
    static final int REQUEST_IMAGE_CAPTURE = 2002;
    private static final int REQUEST_SELECT_PICTURE = 0x02;
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "HouseCropImage.jpeg";
    private Uri mDestinationUri;
    private ViewPager viewPager;
    private HouseImageEntryViewPagerAdapter viewPagerAdapter;
    private List<Uri> imageUris;
    private ImageInfo[] imageInfoArray;
    private ArrayList<Bitmap> imageThumbnailBitmaps;
    private RecyclerView thumbnailsRecyclerView;
    private HouseEntryImageThumbnailsRecyclerAdapter thumbnailsRecyclerAdapter;
    private ImageButton removeImageButton;
    private AlertDialog progressDialog;
    private String mCurrentPhotoPath;

    public HouseImagesEntryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_house_images_entry, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        mDestinationUri = Uri.fromFile(new File(getActivity().getCacheDir(), SAMPLE_CROPPED_IMAGE_NAME));
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() instanceof AddHouseActivity) {
            imageInfoArray = ((AddHouseActivity) getActivity()).getImageInfoArray();
            imageUris = ((AddHouseActivity) getActivity()).getImageUris();
        } else if (getActivity() instanceof EditHouseDetailActivity) {
            imageInfoArray = ((EditHouseDetailActivity) getActivity()).getImageInfoArray();
            imageUris = ((EditHouseDetailActivity) getActivity()).getImageUris();
        }

//        Handler pagerHandler = new Handler();
//        pagerHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                setupViewPager(getView());
//            }
//        });
//
//        Handler thumbnailsHandler = new Handler();
//        thumbnailsHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                setupThumbnailsRecyclerView(getView());
//            }
//        });


        removeImageButton = (ImageButton) view.findViewById(R.id.house_images_entry_removeButton);
        removeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRemoveImageAlert();
            }
        });

//        if (getActivity() != null && getActivity() instanceof EditHouseDetailActivity) {
//            ((EditHouseDetailActivity) getActivity()).showProgressBar();
//        }
//        showProgressBar();
        showProgress();

        setupViewPager(view);
//        updateRemoveButtonVisibility();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                setupViewPager(view);
//                updateRemoveButtonVisibility();
                setupThumbnailsRecyclerView(view);
            }
        }, 100);
//
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                setupThumbnailsRecyclerView(view);
//            }
//        }, 100);

        Button nextButton = (Button) view.findViewById(R.id.house_images_entry_nextButton);
        Button previousButton = (Button) view.findViewById(R.id.house_images_entry_previousButton);

        switch (getEntryMode()) {
            case ADD:
                if (nextButton != null) {
                    nextButton.setVisibility(View.VISIBLE);
                    nextButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (validate() && getOnInteractionListener() != null) {
                                if (getActivity() instanceof AddHouseActivity)
                                    ((AddHouseActivity) getActivity()).setImageUris(imageUris);

                                if (imageUris != null && imageUris.size() > 0)
                                    uploadHouseImages();
                                else
                                    getOnInteractionListener().onGoToNextSection(getHouse());
                            }
                        }
                    });
                }

                if (previousButton != null) {
                    previousButton.setVisibility(View.VISIBLE);
                    previousButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (validate() && getOnInteractionListener() != null) {
                                if (getActivity() instanceof AddHouseActivity) {
                                    ((AddHouseActivity) getActivity()).setImageUris(imageUris);
                                    ((AddHouseActivity) getActivity()).setImageInfoArray(imageInfoArray);
                                }
                                getOnInteractionListener().onBackToPreviousSection(getHouse());
                            }
                        }
                    });
                }
                break;
            case EDIT:
                if (nextButton != null)
                    nextButton.setVisibility(View.GONE);
                if (previousButton != null)
                    previousButton.setVisibility(View.GONE);

//                if (getActivity() != null && getActivity() instanceof EditHouseDetailActivity) {
//                    ((EditHouseDetailActivity)getActivity()).hideProgressBar();
//                }

                break;
        }
    }

    public void showProgressBar() {
//        LinearLayout progressBarLayout = (LinearLayout)getActivity().findViewById(R.id.house_images_entry_progressLayout);
//        ProgressBar progressBar = (ProgressBar)getActivity().findViewById(R.id.house_images_entry_progressBar);
//
//        if (progressBar != null && progressBarLayout != null) {
//            progressBar.setVisibility(View.VISIBLE);
//            progressBarLayout.setVisibility(View.VISIBLE);
//        }
    }

    public void hideProgressBar() {
//        LinearLayout progressBarLayout = (LinearLayout)getActivity().findViewById(R.id.house_images_entry_progressLayout);
//        ProgressBar progressBar = (ProgressBar)getActivity().findViewById(R.id.house_images_entry_progressBar);
//
//        if (progressBar != null && progressBarLayout != null) {
//            progressBar.setVisibility(View.GONE);
//            progressBarLayout.setVisibility(View.GONE);
//        }
    }

    public void uploadHouseImages() {
        showProgress();

        if (imageUris == null || imageUris.size() == 0) {
            if (getOnInteractionListener() != null) {
                getOnInteractionListener().onGoToNextSection(getHouse());
            }
            return;
        }

        Retrofit retrofit = RetrofitService.getInstance().getRetrofit();


        final MediaType MEDIA_TYPE = MediaType.parse("image/png");
        HashMap<String, RequestBody> map = new HashMap<>(imageUris.size());


        MultipartBuilder builder = new MultipartBuilder();
        builder.type(MultipartBuilder.FORM);

        for (Uri uri : imageUris) {
            File file = new File(uri.getPath());
            RequestBody photoRequestBody = RequestBody.create(MEDIA_TYPE, file);
            map.put("pictures\"; filename=\"" + file.getName(), photoRequestBody);
        }


        RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);
        Call<ImageInfo[]> call = apiEndpoints.uploadHouseImages(getHouse().getDetailUrl() + "/pictures", map);

        call.enqueue(new Callback<ImageInfo[]>() {
            @Override
            public void onResponse(Call<ImageInfo[]> call, Response<ImageInfo[]> response) {
                hideProgress();
                int statusCode = response.code();
                ImageInfo[] result = response.body();
//                if (statusCode == 201 || statusCode == 204) {
                if (result != null) {
                    imageInfoArray = result;
//                    Toast.makeText(getActivity(), "Upload image success... :  " + String.valueOf(statusCode), Toast.LENGTH_LONG).show();
                    imageUris = null;
                    if (getActivity() instanceof AddHouseActivity) {
                        ((AddHouseActivity) getActivity()).setImageInfoArray(imageInfoArray);
                        ((AddHouseActivity) getActivity()).setImageUris(imageUris);
                    } else if (getActivity() instanceof EditHouseDetailActivity) {
                        ((EditHouseDetailActivity) getActivity()).setImageInfoArray(imageInfoArray);
                        ((EditHouseDetailActivity) getActivity()).setImageUris(imageUris);
                        ((EditHouseDetailActivity) getActivity()).showUpdateHouseResultDialog();
                    }
                    if (getOnInteractionListener() != null)
                        getOnInteractionListener().onGoToNextSection(getHouse());
                } else {
                    try {
                        if (response.errorBody() != null) {
                            Toast.makeText(getActivity(), response.errorBody().string(), Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ImageInfo[]> call, Throwable t) {
                hideProgress();
                Toast.makeText(getActivity(), "Upload image exception : " + t.getMessage(), Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    private void removeHouseImage(ImageInfo imageInfo) {

        showProgress();

        Retrofit retrofit = RetrofitService.getInstance().getRetrofit();

        RemoveHouseImagesInfo removeHouseImagesInfo = new RemoveHouseImagesInfo();
        String[] imageNames = new String[1];
        imageNames[0] = imageInfo.getFilename();
        removeHouseImagesInfo.setPicture_names(imageNames);

        RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);
        Call<ImageInfo[]> call = apiEndpoints.removeHouseImages(getHouse().getDetailUrl() + "/pictures", removeHouseImagesInfo);
        call.enqueue(new Callback<ImageInfo[]>() {
            @Override
            public void onResponse(Call<ImageInfo[]> call, Response<ImageInfo[]> response) {
                hideProgress();
                int statusCode = response.code();
                ImageInfo[] result = response.body();
//                if (statusCode == 201 || statusCode == 204) {
                if (result != null || statusCode == 201 || statusCode == 204) {
                    imageInfoArray = result;
                    imageRemoved();
                    Toast.makeText(getActivity(), "Remove image success... :  " + String.valueOf(statusCode), Toast.LENGTH_LONG).show();
                    if (getActivity() instanceof AddHouseActivity) {
                        ((AddHouseActivity) getActivity()).setImageInfoArray(imageInfoArray);
                    } else if (getActivity() instanceof EditHouseDetailActivity) {
                        ((EditHouseDetailActivity) getActivity()).setImageInfoArray(imageInfoArray);
                    }
                } else {
                    try {
                        if (response.errorBody() != null) {
                            Toast.makeText(getActivity(), response.errorBody().string(), Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ImageInfo[]> call, Throwable t) {
                hideProgress();
                Toast.makeText(getActivity(), "Remove image exception : " + t.getMessage(), Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    private void setupViewPager(View view) {

        viewPager = (ViewPager) view.findViewById(R.id.house_images_entry_imageViewpager);

        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int height = display.getHeight();
        viewPager.getLayoutParams().height = height / 3;
//        viewPagerLayout.getLayoutParams().height = height/2;

        if (imageUris == null)
            imageUris = new ArrayList<Uri>();
        viewPagerAdapter = new HouseImageEntryViewPagerAdapter(getContext(), imageUris, imageInfoArray);
        viewPager.setAdapter(viewPagerAdapter);

        CirclePageIndicator pageIndicator = (CirclePageIndicator) view.findViewById(R.id.house_images_entry_imagePageIndicator);
        pageIndicator.setViewPager(viewPager);

        updateRemoveButtonVisibility();
    }

    private void updateRemoveButtonVisibility() {

        if ((imageUris != null && imageUris.size() > 0) ||
                (imageInfoArray != null && imageInfoArray.length > 0))
            removeImageButton.setVisibility(View.VISIBLE);
        else removeImageButton.setVisibility(View.INVISIBLE);
    }

    private void showRemoveImageAlert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.house_images_entry_remove_alert_title);
        builder.setMessage(R.string.house_images_entry_remove_alert_message);

        builder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                removeImageOnClick();
            }
        });

        builder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
    }

    private void showProgress() {
        if (progressDialog == null)
            progressDialog = AlertUtils.getInstance().createModelProgress(getActivity());
        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                dialogInterface.dismiss();
            }
        });

        progressDialog.show();
    }

    private void hideProgress() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    private void removeImageOnClick() {

        if (viewPager == null)
            return;

        int selectedImagePosition = viewPager.getCurrentItem();

        if (imageInfoArray != null) {
            if (selectedImagePosition >= imageInfoArray.length) {
                imageUris.remove(selectedImagePosition - imageInfoArray.length);
                imageRemoved();
            } else {
                ImageInfo imageInfo = imageInfoArray[selectedImagePosition];
                removeHouseImage(imageInfo);
            }
        } else {
            imageUris.remove(selectedImagePosition);
            imageRemoved();
        }
    }

    private void imageRemoved() {
        viewPagerAdapter = new HouseImageEntryViewPagerAdapter(getContext(), imageUris, imageInfoArray);
        viewPager.setAdapter(viewPagerAdapter);
        if (viewPagerAdapter.getCount() > 0)
            viewPager.setCurrentItem(viewPagerAdapter.getCount() - 1);
        viewPager.invalidate();

        updateRemoveButtonVisibility();

        updateThumbnailsRecyclerView();

        Toast.makeText(getContext(), R.string.house_images_entry_image_removed_alert, Toast.LENGTH_SHORT).show();
    }

    private void updateViewPager(Uri resultUri) {
        if (imageUris == null)
            imageUris = new ArrayList<Uri>();

        imageUris.add(resultUri);
        viewPagerAdapter = new HouseImageEntryViewPagerAdapter(getContext(), imageUris, imageInfoArray);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(viewPagerAdapter.getCount() - 1);
        viewPager.invalidate();

        updateRemoveButtonVisibility();

        addThumbnail(resultUri);
    }

    private void setupThumbnailsRecyclerView(View view) {

        thumbnailsRecyclerView = (RecyclerView) view.findViewById(R.id.house_images_entry_imageThumbnailsRecyclerView);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false);
        thumbnailsRecyclerView.setLayoutManager(layoutManager);
        thumbnailsRecyclerView.setHasFixedSize(true);
        imageThumbnailBitmaps = new ArrayList<Bitmap>();

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        float thumbnailWidthDp = (dpWidth - 28) / 3;
        final int thumbnailWidthPx = (int) (thumbnailWidthDp * displayMetrics.density);
        final int thumbnailHeightPx = (int) (80 * displayMetrics.density);

        if (imageInfoArray != null && imageInfoArray.length > 0) {
            for (ImageInfo imageInfo : imageInfoArray) {
                final String url = imageInfo.getUrl();
                ImageDownloaderAsyncTask imageDownloaderAsyncTask = new ImageDownloaderAsyncTask(getActivity(), url, thumbnailWidthPx, thumbnailHeightPx);
                AsyncTask asyncTask = imageDownloaderAsyncTask.execute();
                try {
                    Bitmap thumbnailBitmap = (Bitmap) asyncTask.get();
                    if (thumbnailBitmap != null)
                        imageThumbnailBitmaps.add(thumbnailBitmap);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
//        else
        if (imageUris != null && imageUris.size() > 0) {

            for (Uri imageUri : imageUris) {

                Bitmap thumbnailBitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imageUri.getPath()),
                        thumbnailWidthPx, thumbnailHeightPx);
                imageThumbnailBitmaps.add(thumbnailBitmap);
            }
        }

        thumbnailsRecyclerAdapter = new HouseEntryImageThumbnailsRecyclerAdapter(getActivity(), imageThumbnailBitmaps, this);
        thumbnailsRecyclerView.setAdapter(thumbnailsRecyclerAdapter);

//        if (getActivity() != null && getActivity() instanceof EditHouseDetailActivity) {
//            ((EditHouseDetailActivity) getActivity()).hideProgressBar();
//        }
//        hideProgressBar();
        hideProgress();
    }

    private void addThumbnail(Uri resultUri) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        float thumbnailWidthDp = (dpWidth - 28) / 3;
        int thumbnailWidthPx = (int) (thumbnailWidthDp * displayMetrics.density);
        int thumbnailHeightPx = (int) (80 * displayMetrics.density);

        Bitmap thumbnailBitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(resultUri.getPath()),
                thumbnailWidthPx, thumbnailHeightPx);
        if (imageThumbnailBitmaps == null)
            imageThumbnailBitmaps = new ArrayList<Bitmap>();

        imageThumbnailBitmaps.add(thumbnailBitmap);
        thumbnailsRecyclerAdapter = new HouseEntryImageThumbnailsRecyclerAdapter(getActivity(), imageThumbnailBitmaps, this);
        thumbnailsRecyclerView.setAdapter(thumbnailsRecyclerAdapter);
        thumbnailsRecyclerView.invalidate();
    }

    private void updateThumbnailsRecyclerView() {

        imageThumbnailBitmaps = new ArrayList<Bitmap>();
        imageThumbnailBitmaps = new ArrayList<Bitmap>();

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        float thumbnailWidthDp = (dpWidth - 28) / 3;
        final int thumbnailWidthPx = (int) (thumbnailWidthDp * displayMetrics.density);
        final int thumbnailHeightPx = (int) (80 * displayMetrics.density);

        if (imageInfoArray != null && imageInfoArray.length > 0) {
            for (ImageInfo imageInfo : imageInfoArray) {
                final String url = imageInfo.getUrl();
                ImageDownloaderAsyncTask imageDownloaderAsyncTask = new ImageDownloaderAsyncTask(getActivity(), url, thumbnailWidthPx, thumbnailHeightPx);
                AsyncTask asyncTask = imageDownloaderAsyncTask.execute();
                try {
                    Bitmap thumbnailBitmap = (Bitmap) asyncTask.get();
                    if (thumbnailBitmap != null)
                        imageThumbnailBitmaps.add(thumbnailBitmap);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }

        if (imageUris != null && imageUris.size() > 0) {

            for (Uri imageUri : imageUris) {

                Bitmap thumbnailBitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imageUri.getPath()),
                        thumbnailWidthPx, thumbnailHeightPx);
                imageThumbnailBitmaps.add(thumbnailBitmap);
            }
        }

        thumbnailsRecyclerAdapter = new HouseEntryImageThumbnailsRecyclerAdapter(getActivity(), imageThumbnailBitmaps, this);
        thumbnailsRecyclerView.setAdapter(thumbnailsRecyclerAdapter);
        thumbnailsRecyclerView.invalidate();
    }

    @Override
    public void onAddImageClick() {
        selectImage();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                if (!TextUtils.isEmpty(mCurrentPhotoPath)) {
                    startCropActivity(Uri.fromFile(new File(mCurrentPhotoPath)));

                } else {
                    Toast.makeText(getContext(), "toast_cannot_retrieve_selected_image", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == REQUEST_SELECT_PICTURE) {
                if (data != null && data.getData() != null) {
                    startCropActivity(data.getData());
                } else {
                    Toast.makeText(getContext(), "toast_cannot_retrieve_selected_image", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == Crop.REQUEST_CROP) {
                handleCropResult(data);
            }
        } else {
            handleCropError(data);
        }

    }

    private void selectImage() {
        final CharSequence[] items = getResources().getStringArray(R.array.profile_capture_photo_array);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

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

    private void startCropActivity(@NonNull Uri uri) {
//        UCrop uCrop = UCrop.of(uri, mDestinationUri);
//        uCrop = basisConfig(uCrop);
//        uCrop.start(this);

        mDestinationUri = Uri.fromFile(new File(getActivity().getCacheDir(), "house_image_" + UUID.randomUUID().toString() + ".jpeg"));
        Crop.of(uri, mDestinationUri).asSquare().start(getContext(), this, Crop.REQUEST_CROP);

//        UCrop.of(uri, mDestinationUri)
//                .withAspectRatio(16, 9)
//                .withMaxResultSize(getScreenWidth(this), getScreenWidth(this))
//                .start(this);
    }

    private void handleCropResult(@NonNull Intent result) {
//        final Uri resultUri = UCrop.getOutput(result);
        final Uri resultUri = Crop.getOutput(result);
        if (resultUri != null) {
//            ResultActivity.startWithUri(this, resultUri);
            updateViewPager(resultUri);
        } else {
            Toast.makeText(getActivity(), "toast_cannot_retrieve_cropped_image", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private void handleCropError(@NonNull Intent result) {
//        final Throwable cropError = UCrop.getError(result);
        Toast.makeText(getActivity(), "Error getting image.", Toast.LENGTH_SHORT).show();
//        final Throwable cropError = Crop.getError(result);
//        if (cropError != null) {
//            Log.e(TAG, "handleCropError: ", cropError);
//            Toast.makeText(this, cropError.getMessage(), Toast.LENGTH_LONG).show();
//        } else {
//            Toast.makeText(this, "toast_unexpected_error", Toast.LENGTH_SHORT).show();
//        }
    }

    private void pickFromGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
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
            startActivityForResult(Intent.createChooser(intent, "label_select_picture"), REQUEST_SELECT_PICTURE);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                return;
            }
            Uri photoURI = FileProvider.getUriForFile(getContext(),
                    BuildConfig.APPLICATION_ID + ".provider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    protected void requestPermission(final String permission, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
//            showAlertDialog(getString(R.string.permission_title_rationale), rationale,
            showAlertDialog("permission_title_rationale", rationale,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{permission}, requestCode);
                        }
                    }, "Ok", null, "Cancel");
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);
        }
    }

    protected void showAlertDialog(@Nullable String title, @Nullable String message,
                                   @Nullable DialogInterface.OnClickListener onPositiveButtonClickListener,
                                   @NonNull String positiveText,
                                   @Nullable DialogInterface.OnClickListener onNegativeButtonClickListener,
                                   @NonNull String negativeText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveText, onPositiveButtonClickListener);
        builder.setNegativeButton(negativeText, onNegativeButtonClickListener);
        builder.show();
    }

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

    public class SetupViewPagerAsyncTask extends AsyncTask {

        private View view;

        public SetupViewPagerAsyncTask(View view) {
            this.view = view;
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            setupViewPager(view);
            updateRemoveButtonVisibility();
            setupThumbnailsRecyclerView(view);

            if (getActivity() != null && getActivity() instanceof EditHouseDetailActivity) {
                ((EditHouseDetailActivity) getActivity()).hideProgressBar();
            }

            return null;
        }
    }

    public class ImageDownloaderAsyncTask extends AsyncTask {
        private Context context;
        private String imageUrl;
        private int width, height;
        private String authorization;

        public ImageDownloaderAsyncTask(Context context, String imageUrl, int width, int height) {
            this.context = context;
            this.imageUrl = imageUrl;
            this.width = width;
            this.height = height;
        }

        public ImageDownloaderAsyncTask(Context context, String authorization, String imageUrl) {
            this.context = context;
            this.authorization = authorization;
            this.imageUrl = imageUrl;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            return getImageBitmap(imageUrl);
        }

        private Bitmap getImageBitmap(String url) {

            Picasso picasso;
            try {

                if (authorization != null && authorization.length() > 0) {

                    OkHttpClient picassoClient = new OkHttpClient();

                    picassoClient.networkInterceptors().add(new Interceptor() {

                        @Override
                        public com.squareup.okhttp.Response intercept(Chain chain) throws IOException {
                            Request newRequest = chain.request().newBuilder()
                                    .addHeader("access-token", authorization)
                                    .build();
                            return chain.proceed(newRequest);
                        }
                    });

                    picasso = new Picasso.Builder(context).downloader(new OkHttpDownloader(picassoClient)).build();
                } else {
                    picasso = Picasso.with(context);
                }

                if (width > 0 && height > 0) {
                    return picasso.load(imageUrl).resize(width, height).centerCrop().get();
                } else {
                    return picasso.load(imageUrl).resize(120, 80).centerCrop().get();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

//    @NotThreadSafe
//    public class HttpDeleteWithBody extends org.apache.http.client.methods.HttpEntityEnclosingRequestBase {
//        public static final String METHOD_NAME = "DELETE";
//        public String getMethod() { return METHOD_NAME; }
//
//        public HttpDeleteWithBody(final String uri) {
//            super();
//            setURI(URI.create(uri));
//        }
//        public HttpDeleteWithBody(final URI uri) {
//            super();
//            setURI(uri);
//        }
//        public HttpDeleteWithBody() { super(); }
//    }
}
