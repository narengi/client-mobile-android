package xyz.narengi.android.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.byagowi.persiancalendar.Entity.Day;
import com.byagowi.persiancalendar.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import calendar.CivilDate;
import calendar.DateConverter;
import calendar.PersianDate;
import ir.smartlab.persindatepicker.util.PersianCalendar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import xyz.narengi.android.R;
import xyz.narengi.android.common.Constants;
import xyz.narengi.android.common.dto.AccountProfile;
import xyz.narengi.android.common.dto.House;
import xyz.narengi.android.common.dto.HouseAvailableDates;
import xyz.narengi.android.common.dto.ImageInfo;
import xyz.narengi.android.service.RetrofitApiEndpoints;
import xyz.narengi.android.service.RetrofitService;
import xyz.narengi.android.ui.activity.EditHouseActivity;
import xyz.narengi.android.ui.activity.HouseActivity;
import xyz.narengi.android.util.Util;

/**
 * @author Siavash Mahmoudpour
 */
public class HostHousesContentRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private House[] houses;
    private List<ImageInfo[]> imageInfoList;
    private List<HouseAvailableDates> houseAvailableDatesList;
    private RemoveHouseListener removeHouseListener;
    private Map<String, ImageInfo[]> imageInfoArraysMap;
    //    private Map<String,HouseAvailableDates> houseAvailableDatesMap;
    private HashMap<String, HashMap<String, List<Day>>> housesSelectedDaysMap;

    private Map<String, ImageInfo[]> allImageInfoArraysMap;
    private Map<String, HouseAvailableDates> allHouseAvailableDatesMap;

    //TODO : use AsyncQueryHandler
//    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
//    private DisplayImageOptions displayImageOptions;

    public HostHousesContentRecyclerAdapter(Context context, House[] houses, List<ImageInfo[]> imageInfoList,
                                            List<HouseAvailableDates> houseAvailableDatesList, RemoveHouseListener removeHouseListener,
                                            Map<String, ImageInfo[]> allImageInfoArraysMap,
                                            Map<String, HouseAvailableDates> allHouseAvailableDatesMap
    ) {
        this.context = context;
        this.houses = houses;
        this.imageInfoList = imageInfoList;
        this.houseAvailableDatesList = houseAvailableDatesList;
        this.removeHouseListener = removeHouseListener;
        if (houses != null) {
            imageInfoArraysMap = new HashMap<String, ImageInfo[]>();
//            houseAvailableDatesMap = new HashMap<String,HouseAvailableDates>();
            housesSelectedDaysMap = new HashMap<String, HashMap<String, List<Day>>>();

            this.allImageInfoArraysMap = allImageInfoArraysMap;
            this.allHouseAvailableDatesMap = allHouseAvailableDatesMap;
        }

//        imageLoader = ImageLoader.getInstance(); // Get singleton instance
//        imageLoader.init(ImageLoaderConfiguration.createDefault(context));

//        displayImageOptions = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.xml.progress)
//                .showImageForEmptyUri(new ColorDrawable(context.getResources().getColor(R.color.gray_light)))
//                .showImageOnFail(R.drawable.ic_action_remove_image)
//                .cacheInMemory(true)
//                .cacheOnDisk(true)
//                .considerExifParams(true)
//                .displayer(new SimpleBitmapDisplayer())
//                .build();

//        displayImageOptions = new DisplayImageOptions.Builder()
//                .showImageForEmptyUri(new ColorDrawable(context.getResources().getColor(R.color.gray_light)))
//                .showImageOnFail(R.drawable.ic_action_close)
//                .cacheInMemory(true)
//                .cacheOnDisk(true)
//                .considerExifParams(true)
//                .displayer(new SimpleBitmapDisplayer())
//                .build();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (houses == null || houses.length == 0) {
            View view = inflater.inflate(R.layout.host_my_houses_empty_data, parent, false);
            viewHolder = new EmptyDataViewHolder(view);
        } else {
            View itemView = inflater.inflate(R.layout.host_my_houses_item, parent, false);
//            HostHousesItemViewHolder housesItemViewHolder = new HostHousesItemViewHolder(itemView);
            viewHolder = new HostHousesItemViewHolder(itemView);

//            housesItemViewHolder.houseInfoLayout = (LinearLayout)itemView.findViewById(R.id.host_my_houses_itemInfoLayout);
//            housesItemViewHolder.houseImageView = (ImageView)itemView.findViewById(R.id.host_my_houses_itemImage);
//            housesItemViewHolder.houseTitleTextView = (TextView)itemView.findViewById(R.id.host_my_houses_itemTitle);
//            housesItemViewHolder.houseDatesTextView = (TextView)itemView.findViewById(R.id.host_my_houses_itemAvailableDate);
//            housesItemViewHolder.housePriceTextView = (TextView)itemView.findViewById(R.id.host_my_houses_itemPrice);
//            housesItemViewHolder.imageProgressBarLayout = (LinearLayout)itemView.findViewById(R.id.host_my_houses_progressBarLayout);
//            housesItemViewHolder.imageProgressBar = (ProgressBar)itemView.findViewById(R.id.host_my_houses_progressBar);
//
//            housesItemViewHolder.houseTypeTextView = (TextView)itemView.findViewById(R.id.host_my_houses_itemHouseType);
//            housesItemViewHolder.houseBedsTextView = (TextView)itemView.findViewById(R.id.host_my_houses_itemRemoveBeds);
//            housesItemViewHolder.houseRoomsTextView = (TextView)itemView.findViewById(R.id.host_my_houses_itemRooms);
//
//            housesItemViewHolder.viewHouseButton = (Button)itemView.findViewById(R.id.host_my_houses_itemViewButton);
//            housesItemViewHolder.editHouseButton = (Button)itemView.findViewById(R.id.host_my_houses_itemEditButton);
//            housesItemViewHolder.removeHouseButton = (Button)itemView.findViewById(R.id.host_my_houses_itemRemoveButton);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (houses != null && houses.length > 0) {
            HostHousesItemViewHolder housesItemViewHolder = (HostHousesItemViewHolder) viewHolder;
            setupHousesItem(housesItemViewHolder, position);
        } else {
            EmptyDataViewHolder emptyDataViewHolder = (EmptyDataViewHolder) viewHolder;
        }
    }

    @Override
    public int getItemCount() {

        if (houses != null && houses.length > 0)
            return houses.length;
        return 1;
    }

    private void setupHousesItem(HostHousesItemViewHolder viewHolder, int position) {

//        viewHolder.houseImageView.setImageBitmap(null);
//        viewHolder.houseImageView.setImageDrawable(null);
//        viewHolder.houseDatesTextView.setText("");

        if (houses == null || position > houses.length)
            return;

        final House house = houses[position];

        viewHolder.houseTitleTextView.setText(house.getName());
        if (house.getPrice() != null) {
            viewHolder.housePriceTextView.setText(Util.convertNumber((String.valueOf(house.getPrice().getPrice()) + " " + context.getString(R.string.currency_thousan_toman) /*house.getPrice().getCurrencyText()*/)));
        }

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		try {
			Date firstAvailableDate = dateFormat.parse(house.getDates()[0]);
			PersianCalendar persianCalendar = new PersianCalendar(firstAvailableDate.getTime());
			String dateString = persianCalendar.getPersianShortDate();
			viewHolder.houseDatesTextView.setText(context.getString(R.string.host_houses_first_available_date, Util.convertNumber(dateString)));
		} catch (ParseException e) {
			e.printStackTrace();
			viewHolder.houseDatesTextView.setText(context.getString(R.string.host_houses_first_available_date, ""));
		}


//        if (imageInfoList != null && imageInfoList.size() > position) {
//            ImageInfo[] imageInfoArray = imageInfoList.get(position);
//            if (imageInfoArray != null && imageInfoArray.length > 0) {
//                imageInfoArraysMap.put(house.getDetailUrl(), imageInfoArray);
//                getHouseImage(viewHolder, imageInfoArray[0]);
//            } else
//                getHouseImage(viewHolder, null);
//        } else {
//            getHouseImage(viewHolder, null);
//        }

        if (allImageInfoArraysMap != null && allImageInfoArraysMap.get(house.getDetailUrl()) != null) {
            ImageInfo[] imageInfoArray = allImageInfoArraysMap.get(house.getDetailUrl());
            if (imageInfoArray != null && imageInfoArray.length > 0) {
                imageInfoArraysMap.put(house.getDetailUrl(), imageInfoArray);
                getHouseImage(viewHolder, imageInfoArray[0]);
            } else
                getHouseImage(viewHolder, null);
        } else {
            getHouseImage(viewHolder, null);
        }

//        getHouseImages(house.getDetailUrl(), viewHolder);

		if (house.getType()!=null) {
			viewHolder.houseTypeTextView.setText(house.getType().getTitle());
		}
//        if (house.getType() != null) {
//            switch (house.getType()) {
//                case "apartment":
//                    viewHolder.houseTypeTextView.setText(context.getString(R.string.host_houses_type_apartment));
//                    break;
//                case "villa":
//                    viewHolder.houseTypeTextView.setText(context.getString(R.string.host_houses_type_villa));
//                    break;
//                case "house":
//                    viewHolder.houseTypeTextView.setText(context.getString(R.string.host_houses_type_house));
//                    break;
//                default:
//                    viewHolder.houseTypeTextView.setText(context.getString(R.string.host_houses_type_house));
//                    break;
//            }
//        } else {
//            viewHolder.houseTypeTextView.setText(context.getString(R.string.host_houses_type_house));
//        }

        int bedsCount = 0, roomsCount = 0;
        if (house.getSpec() != null) {
            bedsCount = house.getSpec().getBedCount();
            roomsCount = house.getSpec().getBedroomCount();
        }

        viewHolder.houseRoomsTextView.setText(context.getString(R.string.host_houses_spec_rooms, Util.convertNumber(String.valueOf(roomsCount))));
        viewHolder.houseBedsTextView.setText(context.getString(R.string.host_houses_spec_beds, Util.convertNumber(String.valueOf(bedsCount))));

//        if (houseAvailableDatesList != null && houseAvailableDatesList.size() > position) {
//            HouseAvailableDates houseAvailableDates = houseAvailableDatesList.get(position);
//            setFirstAvailableDate(viewHolder, houseAvailableDates, house.getDetailUrl());
//        } else {
//            viewHolder.houseDatesTextView.setText(context.getString(R.string.host_houses_first_available_date, ""));
//        }


//        getHouseAvailableDates(house, viewHolder);

        viewHolder.viewHouseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHouseDetail(house.getDetailUrl());
            }
        });

        viewHolder.editHouseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditHouse(house);
            }
        });

        viewHolder.removeHouseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (removeHouseListener != null) {
                    removeHouseListener.onRemoveHouse(house);
                }
            }
        });
    }

    private void getHouseImage(final HostHousesItemViewHolder viewHolder, ImageInfo imageInfo) {

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        float thumbnailWidthDp = dpWidth / 3;
        final int thumbnailWidthPx = (int) (thumbnailWidthDp * displayMetrics.density);
//                    final int thumbnailHeightPx = thumbnailWidthPx * 38 / 62;
        final int thumbnailHeightPx = thumbnailWidthPx * 7 / 10;

        RelativeLayout.LayoutParams imageLayoutParams = (RelativeLayout.LayoutParams) viewHolder.houseImageView.getLayoutParams();
        int imageWidth = imageLayoutParams.width;
        if (imageWidth <= 0) {
            imageWidth = thumbnailWidthPx;
            int imageHeight = imageWidth * 7 / 10;
            imageLayoutParams.width = imageWidth;
            imageLayoutParams.height = imageHeight;
            viewHolder.houseImageView.setLayoutParams(imageLayoutParams);

        }

        if (imageInfo != null && imageInfo.getUrl() != null) {
            /*Picasso picasso;
            if (authorizationJson != null && authorizationJson.length() > 0) {

                OkHttpClient picassoClient = new OkHttpClient();

                picassoClient.networkInterceptors().add(new Interceptor() {

                    @Override
                    public com.squareup.okhttp.Response intercept(Chain chain) throws IOException {
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("authorization", authorizationJson)
                                .build();
                        return chain.proceed(newRequest);
                    }
                });

                picasso = new Picasso.Builder(context).downloader(new OkHttpDownloader(picassoClient)).build();
            } else {
                picasso = Picasso.with(context);
            }*/

//            picasso.load(imageInfo.getUrl()).networkPolicy(NetworkPolicy.NO_STORE).into(viewHolder.houseImageView);

            Picasso.with(context).load(imageInfo.getUrl()).into(viewHolder.houseImageView);  //todo check image loader behdad
//            imageLoader.displayImage(imageInfo.getUrl(), viewHolder.houseImageView, displayImageOptions, animateFirstListener);

            // Load image, decode it to Bitmap and return Bitmap to callback
//            imageLoader.loadImage(imageInfo.getUrl(), null, displayImageOptions, new SimpleImageLoadingListener() {
//                @Override
//                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                    if (viewHolder.imageProgressBarLayout != null && viewHolder.imageProgressBar != null) {
//                        viewHolder.imageProgressBar.setVisibility(View.GONE);
//                        viewHolder.imageProgressBarLayout.setVisibility(View.GONE);
//                    }
//                    viewHolder.houseImageView.setImageBitmap(loadedImage);
//                }
//
//                @Override
//                public void onLoadingStarted(String imageUri, View view) {
//                    super.onLoadingStarted(imageUri, view);
//                    if (viewHolder.imageProgressBarLayout != null && viewHolder.imageProgressBar != null) {
//                        viewHolder.imageProgressBar.setVisibility(View.VISIBLE);
//                        viewHolder.imageProgressBarLayout.setVisibility(View.VISIBLE);
//                    }
//                }
//
//                @Override
//                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//                    super.onLoadingFailed(imageUri, view, failReason);
//                    if (viewHolder.imageProgressBarLayout != null && viewHolder.imageProgressBar != null) {
//                        viewHolder.imageProgressBar.setVisibility(View.GONE);
//                        viewHolder.imageProgressBarLayout.setVisibility(View.GONE);
//                    }
//                    //                viewHolder.houseImageView.setImageBitmap(null);
//                    //                viewHolder.houseImageView.setImageDrawable(new ColorDrawable(context.getResources().getColor(R.color.gray_light)));
//                }
//
//                @Override
//                public void onLoadingCancelled(String imageUri, View view) {
//                    super.onLoadingCancelled(imageUri, view);
//                    if (viewHolder.imageProgressBarLayout != null && viewHolder.imageProgressBar != null) {
//                        viewHolder.imageProgressBar.setVisibility(View.GONE);
//                        viewHolder.imageProgressBarLayout.setVisibility(View.GONE);
//                    }
////                    viewHolder.houseImageView.setImageBitmap(null);
////                    viewHolder.houseImageView.setImageDrawable(new ColorDrawable(context.getResources().getColor(R.color.gray_light)));
//                }
//            });

            /*ImageDownloaderAsyncTask imageDownloaderAsyncTask = new ImageDownloaderAsyncTask(context, viewHolder, authorizationJson, imageInfo.getUrl());
            AsyncTask asyncTask = imageDownloaderAsyncTask.execute();
            try {
                Object asyncTaskResult = asyncTask.get();
                if (viewHolder.imageProgressBarLayout != null && viewHolder.imageProgressBar != null) {
                    viewHolder.imageProgressBar.setVisibility(View.GONE);
                    viewHolder.imageProgressBarLayout.setVisibility(View.GONE);
                }
                if (asyncTaskResult != null) {
                    Bitmap bitmap = (Bitmap)asyncTaskResult;
                    viewHolder.houseImageView.setImageBitmap(bitmap);
                } else {
                    viewHolder.houseImageView.setImageBitmap(null);
                    viewHolder.houseImageView.setImageDrawable(new ColorDrawable(context.getResources().getColor(R.color.gray_light)));
                }
            } catch (InterruptedException | ExecutionException e) {
                viewHolder.houseImageView.setImageBitmap(null);
                viewHolder.houseImageView.setImageDrawable(new ColorDrawable(context.getResources().getColor(R.color.gray_light)));

                if (viewHolder.imageProgressBarLayout != null && viewHolder.imageProgressBar != null) {
                    viewHolder.imageProgressBar.setVisibility(View.GONE);
                    viewHolder.imageProgressBarLayout.setVisibility(View.GONE);
                }
                e.printStackTrace();
            }*/

            /*picasso.load(imageInfo.getUrl()).networkPolicy(NetworkPolicy.NO_CACHE).into(viewHolder.houseImageView, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    if (viewHolder.imageProgressBarLayout != null && viewHolder.imageProgressBar != null) {
                        viewHolder.imageProgressBar.setVisibility(View.GONE);
                        viewHolder.imageProgressBarLayout.setVisibility(View.GONE);
                    }
                    viewHolder.houseImageView.invalidate();
                }

                @Override
                public void onError() {
                    if (viewHolder.imageProgressBarLayout != null && viewHolder.imageProgressBar != null) {
                        viewHolder.imageProgressBar.setVisibility(View.GONE);
                        viewHolder.imageProgressBarLayout.setVisibility(View.GONE);
                    }
                    viewHolder.houseImageView.setImageBitmap(null);
                    viewHolder.houseImageView.setImageDrawable(new ColorDrawable(context.getResources().getColor(R.color.gray_light)));
                    viewHolder.houseImageView.invalidate();
                }
            });*/
        } else {
            if (viewHolder.imageProgressBarLayout != null && viewHolder.imageProgressBar != null) {
                viewHolder.imageProgressBar.setVisibility(View.GONE);
                viewHolder.imageProgressBarLayout.setVisibility(View.GONE);
            }
//            viewHolder.houseImageView.setImageBitmap(null);
//            viewHolder.houseImageView.setImageDrawable(new ColorDrawable(context.getResources().getColor(R.color.gray_light)));
//            viewHolder.houseImageView.invalidate();
//            imageLoader.displayImage("", viewHolder.houseImageView, displayImageOptions, animateFirstListener);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void getHouseImages(final String houseUrl, final HostHousesItemViewHolder viewHolder) {

        final String authorizationJson = AccountProfile.getLoggedInAccountProfile(context).getToken().getAuthString();

        Retrofit retrofit = RetrofitService.getInstance().getRetrofit();


        RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);
        Call<ImageInfo[]> call = apiEndpoints.getHouseImages(houseUrl + "/pictures");

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        float thumbnailWidthDp = dpWidth / 3;
        final int thumbnailWidthPx = (int) (thumbnailWidthDp * displayMetrics.density);
//                    final int thumbnailHeightPx = thumbnailWidthPx * 38 / 62;
        final int thumbnailHeightPx = thumbnailWidthPx * 7 / 10;

        RelativeLayout.LayoutParams imageLayoutParams = (RelativeLayout.LayoutParams) viewHolder.houseImageView.getLayoutParams();
        int imageWidth = imageLayoutParams.width;
        if (imageWidth <= 0) {
            imageWidth = thumbnailWidthPx;
            int imageHeight = imageWidth * 7 / 10;
            imageLayoutParams.width = imageWidth;
            imageLayoutParams.height = imageHeight;
            viewHolder.houseImageView.setLayoutParams(imageLayoutParams);

        }

        call.enqueue(new Callback<ImageInfo[]>() {
            @Override
            public void onResponse(Call<ImageInfo[]> call, Response<ImageInfo[]> response) {
                int statusCode = response.code();
                ImageInfo[] result = response.body();
                if (result != null && result.length > 0) {

                    imageInfoArraysMap.put(houseUrl, result);
//                    imageLayoutParams.width = thumbnailWidthPx;
//                    imageLayoutParams.height = thumbnailHeightPx;
//                    viewHolder.houseImageView.setLayoutParams(imageLayoutParams);

//                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)viewHolder.houseInfoLayout.getLayoutParams();
//                    layoutParams.height = thumbnailWidthPx * 7 / 10;
//                    viewHolder.houseInfoLayout.setLayoutParams(layoutParams);

//                    Picasso.with(context).load(result[0].getUrl()).resize(thumbnailWidthPx, thumbnailHeightPx).into(viewHolder.houseImageView, new com.squareup.picasso.Callback() {


                    /*ImageDownloaderAsyncTask imageDownloaderAsyncTask = new ImageDownloaderAsyncTask(context, viewHolder, authorizationJson, result[0].getUrl());
                    AsyncTask asyncTask = imageDownloaderAsyncTask.execute();
                    try {
                        Object asyncTaskResult = asyncTask.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }*/

                    Picasso.with(context).load(result[0].getUrl()).into(viewHolder.houseImageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            if (viewHolder.imageProgressBarLayout != null && viewHolder.imageProgressBar != null) {
                                viewHolder.imageProgressBar.setVisibility(View.GONE);
                                viewHolder.imageProgressBarLayout.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onError() {
                            if (viewHolder.imageProgressBarLayout != null && viewHolder.imageProgressBar != null) {
                                viewHolder.imageProgressBar.setVisibility(View.GONE);
                                viewHolder.imageProgressBarLayout.setVisibility(View.GONE);
                            }
                            viewHolder.houseImageView.setImageBitmap(null);
                            viewHolder.houseImageView.setImageDrawable(new ColorDrawable(context.getResources().getColor(R.color.gray_light)));
                            viewHolder.houseImageView.invalidate();
                        }
                    });

                } else {
                    if (viewHolder.imageProgressBarLayout != null && viewHolder.imageProgressBar != null) {
                        viewHolder.imageProgressBar.setVisibility(View.GONE);
                        viewHolder.imageProgressBarLayout.setVisibility(View.GONE);
                    }
                    viewHolder.houseImageView.setImageBitmap(null);
                    viewHolder.houseImageView.setImageDrawable(new ColorDrawable(context.getResources().getColor(R.color.gray_light)));
                    viewHolder.houseImageView.invalidate();
                }
            }

            @Override
            public void onFailure(Call<ImageInfo[]> call, Throwable t) {
                if (viewHolder.imageProgressBarLayout != null && viewHolder.imageProgressBar != null) {
                    viewHolder.imageProgressBar.setVisibility(View.GONE);
                    viewHolder.imageProgressBarLayout.setVisibility(View.GONE);
                }
                viewHolder.houseImageView.setImageBitmap(null);
                viewHolder.houseImageView.setImageDrawable(new ColorDrawable(context.getResources().getColor(R.color.gray_light)));
                viewHolder.houseImageView.invalidate();
                t.printStackTrace();
            }
        });
    }

    private void setFirstAvailableDate(final HostHousesItemViewHolder viewHolder, HouseAvailableDates houseAvailableDates, String houseUrl) {
        if (houseAvailableDates != null && houseAvailableDates.getStartDate() != null) {

            HashMap<String, List<Day>> selectedDaysMap = createSelectedDaysMap(houseAvailableDates);
            if (selectedDaysMap != null) {
                housesSelectedDaysMap.put(houseUrl, selectedDaysMap);
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            try {
                Date firstAvailableDate = dateFormat.parse(houseAvailableDates.getStartDate());
                PersianCalendar persianCalendar = new PersianCalendar(firstAvailableDate.getTime());
                String dateString = persianCalendar.getPersianShortDate();
                viewHolder.houseDatesTextView.setText(context.getString(R.string.host_houses_first_available_date, dateString));
            } catch (ParseException e) {
                e.printStackTrace();
                viewHolder.houseDatesTextView.setText(context.getString(R.string.host_houses_first_available_date, ""));
            }
        } else {
            viewHolder.houseDatesTextView.setText(context.getString(R.string.host_houses_first_available_date, ""));
        }
    }

    private void getHouseAvailableDates(final House house, final HostHousesItemViewHolder viewHolder) {

        Gson gson = new GsonBuilder().create();

        Retrofit retrofit = RetrofitService.getInstance().getRetrofit();

        RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = new Date();
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.MONTH, 3);
        Date endDate = calendar.getTime();
        String startDateString = simpleDateFormat.format(startDate);
        String endDateString = simpleDateFormat.format(endDate);

        String url = house.getDetailUrl();
        url = url + "/available-dates/start-" + startDateString + "/end-" + endDateString;

        Call<HouseAvailableDates> call = apiEndpoints.getHouseAvailableDates(url);
        call.enqueue(new Callback<HouseAvailableDates>() {
            @Override
            public void onResponse(Call<HouseAvailableDates> call, Response<HouseAvailableDates> response) {
                HouseAvailableDates houseAvailableDates = response.body();
                if (houseAvailableDates != null && houseAvailableDates.getStartDate() != null) {

                    HashMap<String, List<Day>> selectedDaysMap = createSelectedDaysMap(houseAvailableDates);
                    if (selectedDaysMap != null) {
                        housesSelectedDaysMap.put(house.getDetailUrl(), selectedDaysMap);
                    }

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    try {
                        Date firstAvailableDate = dateFormat.parse(houseAvailableDates.getStartDate());
                        PersianCalendar persianCalendar = new PersianCalendar(firstAvailableDate.getTime());
                        String dateString = persianCalendar.getPersianShortDate();
                        viewHolder.houseDatesTextView.setText(context.getString(R.string.host_houses_first_available_date, dateString));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        viewHolder.houseDatesTextView.setText(context.getString(R.string.host_houses_first_available_date, ""));
                    }
                } else {
                    viewHolder.houseDatesTextView.setText(context.getString(R.string.host_houses_first_available_date, ""));
                }
            }

            @Override
            public void onFailure(Call<HouseAvailableDates> call, Throwable t) {
                t.printStackTrace();
                Log.d("HostHousesContent", "getHouseAvailableDates onFailure : " + t.getMessage(), t);
                viewHolder.houseDatesTextView.setText(context.getString(R.string.host_houses_first_available_date, ""));
            }
        });
    }

    private HashMap<String, List<Day>> createSelectedDaysMap(HouseAvailableDates houseAvailableDates) {

        if (houseAvailableDates == null || houseAvailableDates.getDates() == null || houseAvailableDates.getDates().length == 0)
            return null;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        HashMap<String, List<Day>> selectedDaysMap = new HashMap<String, List<Day>>();
        char[] digits = Utils.getInstance().preferredDigits(context);
        PersianDate todayPersianDate = Utils.getToday();

        for (String dateString : houseAvailableDates.getDates()) {
            try {
                Date availableDate = dateFormat.parse(dateString);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(availableDate);
                CivilDate civilDate = new CivilDate(calendar);
                PersianDate persianDate = DateConverter.civilToPersian(civilDate);
                if (persianDate != null) {

                    if (persianDate.getYear() < todayPersianDate.getYear())
                        continue;
                    else if (persianDate.getYear() == todayPersianDate.getYear() &&
                            persianDate.getMonth() < todayPersianDate.getMonth())
                        continue;

                    int position = -1;

                    Day day = new Day();
                    day.setDayOfWeek(persianDate.getDayOfWeek());
                    day.setNum(Utils.formatNumber(persianDate.getDayOfMonth(), digits));
                    day.setPersianDate(persianDate);
                    day.setToday(false);

                    if (persianDate.getYear() == todayPersianDate.getYear()) {
                        if (persianDate.getMonth() == todayPersianDate.getMonth()) {
                            position = Constants.CALENDAR_MONTHS_LIMIT / 2;
                            if (persianDate.getDayOfMonth() == todayPersianDate.getDayOfMonth())
                                day.setToday(true);
                        } else {
                            position = Constants.CALENDAR_MONTHS_LIMIT / 2 - (persianDate.getMonth() - todayPersianDate.getMonth());
                        }
                    } else {
                        if (persianDate.getMonth() == todayPersianDate.getMonth()) {
                            position = Constants.CALENDAR_MONTHS_LIMIT / 2 - 12;
                        } else if (persianDate.getMonth() > todayPersianDate.getMonth()) {
                            position = Constants.CALENDAR_MONTHS_LIMIT / 2 - 12 - (persianDate.getMonth() - todayPersianDate.getMonth());
                        } else {
                            position = Constants.CALENDAR_MONTHS_LIMIT / 2 - (12 + persianDate.getMonth() - todayPersianDate.getMonth());
                        }
                    }

                    if (position >= 0) {
                        List<Day> days = selectedDaysMap.get(String.valueOf(position));
                        if (days == null)
                            days = new ArrayList<Day>();

                        days.add(day);
                        selectedDaysMap.put(String.valueOf(position), days);
                    }
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return selectedDaysMap;
    }

    private void openHouseDetail(String houseUrl) {
        Intent intent = new Intent(context, HouseActivity.class);
        intent.putExtra("houseUrl", houseUrl);
        context.startActivity(intent);
    }

    private void openEditHouse(House house) {
        Intent intent = new Intent(context, EditHouseActivity.class);
        intent.putExtra("house", house);
        if (imageInfoArraysMap != null && imageInfoArraysMap.get(house.getDetailUrl()) != null)
            intent.putExtra("imageInfoArray", imageInfoArraysMap.get(house.getDetailUrl()));

        if (housesSelectedDaysMap != null && housesSelectedDaysMap.get(house.getDetailUrl()) != null)
            intent.putExtra("selectedDaysMap", housesSelectedDaysMap.get(house.getDetailUrl()));

        if (context instanceof AppCompatActivity)
            ((AppCompatActivity) context).startActivityForResult(intent, 2002);
    }

    public interface RemoveHouseListener {
        public void onRemoveHouse(House house);
    }

//    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {
//
//        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());
//
//        @Override
//        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//            if (loadedImage != null) {
//                ImageView imageView = (ImageView) view;
//                boolean firstDisplay = !displayedImages.contains(imageUri);
//                if (firstDisplay) {
//                    FadeInBitmapDisplayer.animate(imageView, 500);
//                    displayedImages.add(imageUri);
//                }
//            }
//        }
//    }

    public class EmptyDataViewHolder extends RecyclerView.ViewHolder {

        public EmptyDataViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class HostHousesItemViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout houseInfoLayout;
        public ImageView houseImageView;
        public LinearLayout imageProgressBarLayout;
        public ProgressBar imageProgressBar;
        public TextView houseTitleTextView;
        public TextView houseDatesTextView;
        public TextView housePriceTextView;

        public TextView houseTypeTextView;
        public TextView houseBedsTextView;
        public TextView houseRoomsTextView;

        public Button viewHouseButton;
        public Button editHouseButton;
        public Button removeHouseButton;

        public HostHousesItemViewHolder(View itemView) {
            super(itemView);

            houseInfoLayout = (LinearLayout) itemView.findViewById(R.id.host_my_houses_itemInfoLayout);
            houseImageView = (ImageView) itemView.findViewById(R.id.host_my_houses_itemImage);
            houseTitleTextView = (TextView) itemView.findViewById(R.id.host_my_houses_itemTitle);
            houseDatesTextView = (TextView) itemView.findViewById(R.id.host_my_houses_itemAvailableDate);
            housePriceTextView = (TextView) itemView.findViewById(R.id.host_my_houses_itemPrice);
            imageProgressBarLayout = (LinearLayout) itemView.findViewById(R.id.host_my_houses_progressBarLayout);
            imageProgressBar = (ProgressBar) itemView.findViewById(R.id.host_my_houses_progressBar);

            houseTypeTextView = (TextView) itemView.findViewById(R.id.host_my_houses_itemHouseType);
            houseBedsTextView = (TextView) itemView.findViewById(R.id.host_my_houses_itemBeds);
            houseRoomsTextView = (TextView) itemView.findViewById(R.id.host_my_houses_itemRooms);

            viewHouseButton = (Button) itemView.findViewById(R.id.host_my_houses_itemViewButton);
            editHouseButton = (Button) itemView.findViewById(R.id.host_my_houses_itemEditButton);
            removeHouseButton = (Button) itemView.findViewById(R.id.host_my_houses_itemRemoveButton);
        }
    }

    public class ImageDownloaderAsyncTask extends AsyncTask {
        private Context context;
        private HostHousesItemViewHolder viewHolder;
        private String imageUrl;
        private String authorization;

        public ImageDownloaderAsyncTask(Context context, HostHousesItemViewHolder viewHolder, String authorization, String imageUrl) {
            this.context = context;
            this.viewHolder = viewHolder;
            this.authorization = authorization;
            this.imageUrl = imageUrl;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
//            return getImage();

            URL aURL;
            try {
                aURL = new URL(imageUrl);
                URLConnection conn = aURL.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                Bitmap bitmap = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();

                return bitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        private Object getImage() {
            Picasso.with(context).load(imageUrl).into(viewHolder.houseImageView, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    if (viewHolder.imageProgressBarLayout != null && viewHolder.imageProgressBar != null) {
                        viewHolder.imageProgressBar.setVisibility(View.GONE);
                        viewHolder.imageProgressBarLayout.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onError() {
                    if (viewHolder.imageProgressBarLayout != null && viewHolder.imageProgressBar != null) {
                        viewHolder.imageProgressBar.setVisibility(View.GONE);
                        viewHolder.imageProgressBarLayout.setVisibility(View.GONE);
                    }
                    viewHolder.houseImageView.setImageDrawable(new ColorDrawable(context.getResources().getColor(R.color.gray_light)));
                }
            });

            return null;
        }
    }
}
