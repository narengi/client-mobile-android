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
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.viewpagerindicator.CirclePageIndicator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import xyz.narengi.android.R;
import xyz.narengi.android.ui.activity.AddHouseActivity;
import xyz.narengi.android.ui.adapter.HouseEntryImageThumbnailsRecyclerAdapter;
import xyz.narengi.android.ui.adapter.HouseImageEntryViewPagerAdapter;

/**
 * @author Siavash Mahmoudpour
 */
public class HouseImagesEntryFragment extends HouseEntryBaseFragment implements HouseEntryImageThumbnailsRecyclerAdapter.OnAddImageClickListener {

    protected static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 1002;
    private static final int REQUEST_SELECT_PICTURE = 0x02;
    static final int REQUEST_IMAGE_CAPTURE = 2002;
    private Uri mDestinationUri;
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "HouseCropImage.jpeg";
    private ViewPager viewPager;
    private HouseImageEntryViewPagerAdapter viewPagerAdapter;
    private List<Uri> imageUris;
    private ArrayList<Bitmap> imageThumbnailBitmaps;
    private RecyclerView thumbnailsRecyclerView;
    private HouseEntryImageThumbnailsRecyclerAdapter thumbnailsRecyclerAdapter;
    private ImageButton removeImageButton;

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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() instanceof AddHouseActivity)
            imageUris = ((AddHouseActivity)getActivity()).getImageUris();

        setupViewPager(view);
        setupThumbnailsRecyclerView(view);

        Button nextButton = (Button)view.findViewById(R.id.house_images_entry_nextButton);
        Button previousButton = (Button)view.findViewById(R.id.house_images_entry_previousButton);

        switch (getEntryMode()) {
            case ADD:
                if (nextButton != null) {
                    nextButton.setVisibility(View.VISIBLE);
                    nextButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (validate() && getOnInteractionListener() != null) {
                                if (getActivity() instanceof AddHouseActivity)
                                    ((AddHouseActivity)getActivity()).setImageUris(imageUris);
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
                                if (getActivity() instanceof AddHouseActivity)
                                    ((AddHouseActivity)getActivity()).setImageUris(imageUris);
                                getOnInteractionListener().onBackToPreviousSection(getHouse());
                            }
                        }
                    });
                }
                break;
            case EDIT:
                if (nextButton != null)
                    nextButton.setVisibility(View.GONE);
                break;
        }
    }

    private void setupViewPager(View view) {
        viewPager = (ViewPager)view.findViewById(R.id.house_images_entry_imageViewpager);

        Display display= ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int height = display.getHeight();
        viewPager.getLayoutParams().height = height/3;
//        viewPagerLayout.getLayoutParams().height = height/2;

        if (imageUris == null)
            imageUris = new ArrayList<Uri>();
        viewPagerAdapter = new HouseImageEntryViewPagerAdapter(getContext(), imageUris);
        viewPager.setAdapter(viewPagerAdapter);

        CirclePageIndicator pageIndicator = (CirclePageIndicator)view.findViewById(R.id.house_images_entry_imagePageIndicator);
        pageIndicator.setViewPager(viewPager);

        removeImageButton = (ImageButton)view.findViewById(R.id.house_images_entry_removeButton);
        removeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeImage();
            }
        });
        updateRemoveButtonVisibility();
    }

    private void updateRemoveButtonVisibility() {
        if (imageUris == null || imageUris.size() == 0)
            removeImageButton.setVisibility(View.INVISIBLE);
        else
            removeImageButton.setVisibility(View.VISIBLE);
    }

    private void removeImage() {

        int selectedImagePosition = viewPager.getCurrentItem();
        imageUris.remove(selectedImagePosition);

        viewPagerAdapter = new HouseImageEntryViewPagerAdapter(getContext(), imageUris);
        viewPager.setAdapter(viewPagerAdapter);
        if (viewPagerAdapter.getCount() > 0)
            viewPager.setCurrentItem(viewPagerAdapter.getCount() - 1);
        viewPager.invalidate();

        updateRemoveButtonVisibility();

        updateThumbnailsRecyclerView();

        Toast.makeText(getContext(), R.string.house_images_entry_house_removed_alert, Toast.LENGTH_SHORT).show();
    }

    private void updateViewPager(Uri resultUri) {
        if (imageUris == null)
            imageUris = new ArrayList<Uri>();

        imageUris.add(resultUri);
        viewPagerAdapter = new HouseImageEntryViewPagerAdapter(getContext(), imageUris);
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
        imageThumbnailBitmaps =  new ArrayList<Bitmap>();
        if (imageUris != null && imageUris.size() > 0) {

            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
            float thumbnailWidthDp = (dpWidth - 28) / 3;
            int thumbnailWidthPx = (int)(thumbnailWidthDp * displayMetrics.density);
            int thumbnailHeightPx = (int)(80 * displayMetrics.density);

            for (Uri imageUri:imageUris) {

                Bitmap thumbnailBitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imageUri.getPath()),
                        thumbnailWidthPx, thumbnailHeightPx);
                imageThumbnailBitmaps.add(thumbnailBitmap);
            }
        }

        thumbnailsRecyclerAdapter = new HouseEntryImageThumbnailsRecyclerAdapter(getActivity(), imageThumbnailBitmaps, this);
        thumbnailsRecyclerView.setAdapter(thumbnailsRecyclerAdapter);
    }

    private void addThumbnail(Uri resultUri) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        float thumbnailWidthDp = (dpWidth - 28) / 3;
        int thumbnailWidthPx = (int)(thumbnailWidthDp * displayMetrics.density);
        int thumbnailHeightPx = (int)(80 * displayMetrics.density);

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

        imageThumbnailBitmaps =  new ArrayList<Bitmap>();
        if (imageUris != null && imageUris.size() > 0) {

            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
            float thumbnailWidthDp = (dpWidth - 28) / 3;
            int thumbnailWidthPx = (int)(thumbnailWidthDp * displayMetrics.density);
            int thumbnailHeightPx = (int)(80 * displayMetrics.density);

            for (Uri imageUri:imageUris) {


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
            if (requestCode == REQUEST_SELECT_PICTURE || requestCode == REQUEST_IMAGE_CAPTURE) {
                final Uri selectedUri = data.getData();
                if (selectedUri != null) {
                    startCropActivity(data.getData());
                } else {
                    Toast.makeText(getActivity(), "toast_cannot_retrieve_selected_image", Toast.LENGTH_SHORT).show();
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
//            startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_picture)), REQUEST_SELECT_PICTURE);
            startActivityForResult(Intent.createChooser(intent, "label_select_picture"), REQUEST_SELECT_PICTURE);
        }
    }

    private void dispatchTakePictureIntent() {
//
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
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
}
