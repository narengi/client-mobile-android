package xyz.narengi.android.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.Toast;

import com.byagowi.persiancalendar.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import calendar.PersianDate;
import ir.smartlab.persindatepicker.util.PersianCalendar;
import ir.smartlab.persindatepicker.util.PersianCalendarUtils;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.narengi.android.R;
import xyz.narengi.android.common.Constants;
import xyz.narengi.android.common.dto.AroundPlaceHouse;
import xyz.narengi.android.common.dto.Authorization;
import xyz.narengi.android.common.dto.House;
import xyz.narengi.android.common.dto.HouseAvailableDates;
import xyz.narengi.android.common.dto.ImageInfo;
import xyz.narengi.android.service.RetrofitApiEndpoints;
import xyz.narengi.android.ui.activity.HouseActivity;

/**
 * @author Siavash Mahmoudpour
 */
public class HostHousesContentRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private House[] houses;
    private RemoveHouseListener removeHouseListener;

    public HostHousesContentRecyclerAdapter(Context context, House[] houses, RemoveHouseListener removeHouseListener) {
        this.context = context;
        this.houses = houses;
        this.removeHouseListener = removeHouseListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (houses == null || houses.length == 0) {
            View view = inflater.inflate(R.layout.host_my_houses_empty_data, parent, false);
            viewHolder = new EmptyDataViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.host_my_houses_item, parent, false);
            viewHolder = new HostHousesItemViewHolder(view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        if (houses != null && houses.length > 0) {
            HostHousesItemViewHolder housesItemViewHolder = (HostHousesItemViewHolder)viewHolder;
            setupHousesItem(housesItemViewHolder, position);
        } else {
            EmptyDataViewHolder emptyDataViewHolder = (EmptyDataViewHolder)viewHolder;
        }
    }

    @Override
    public int getItemCount() {

        if (houses != null && houses.length > 0)
            return houses.length;
        return 1;
    }

    private void setupHousesItem(HostHousesItemViewHolder viewHolder, int position) {

        if (houses == null || position > houses.length)
            return;

        final House house = houses[position];

        viewHolder.houseTitleTextView.setText(house.getName());
        if (house.getPrice() != null) {
            viewHolder.housePriceTextView.setText(String.valueOf(house.getPrice().getPrice()) + " " + house.getPrice().getCurrencyText());
        }

        viewHolder.houseDatesTextView.setText(context.getString(R.string.host_houses_first_available_date, ""));

        getHouseImage(house.getURL(), viewHolder);

        if (house.getType() != null) {
            switch (house.getType()) {
                case "apartment":
                    viewHolder.houseTypeTextView.setText(context.getString(R.string.host_houses_type_apartment));
                    break;
                case "villa":
                    viewHolder.houseTypeTextView.setText(context.getString(R.string.host_houses_type_villa));
                    break;
                case "house":
                    viewHolder.houseTypeTextView.setText(context.getString(R.string.host_houses_type_house));
                    break;
                default:
                    viewHolder.houseTypeTextView.setText(context.getString(R.string.host_houses_type_house));
                    break;
            }
        } else {
            viewHolder.houseTypeTextView.setText(context.getString(R.string.host_houses_type_house));
        }

        int bedsCount = 0, roomsCount = 0;
        if (house.getSpec() != null) {
            bedsCount = house.getSpec().getBedCount();
            roomsCount = house.getSpec().getBedroomCount();
        }

        viewHolder.houseRoomsTextView.setText(context.getString(R.string.host_houses_spec_rooms, String.valueOf(roomsCount)));
        viewHolder.houseBedsTextView.setText(context.getString(R.string.host_houses_spec_beds, String.valueOf(bedsCount)));

        getHouseAvailableDates(house, viewHolder);

        viewHolder.viewHouseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHouseDetail(house.getURL());
            }
        });

        viewHolder.editHouseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                openEditHouse(house);
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

    private void getHouseImage(String houseUrl, final HostHousesItemViewHolder viewHolder) {

        final SharedPreferences preferences = context.getSharedPreferences("profile", 0);
        String accessToken = preferences.getString("accessToken", "");
        String username = preferences.getString("username", "");

        Authorization authorization = new Authorization();
        authorization.setUsername(username);
        authorization.setToken(accessToken);

        Gson gson = new GsonBuilder().create();

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
        Call<ImageInfo[]> call = apiEndpoints.getHouseImages(houseUrl + "/pictures");

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        float thumbnailWidthDp = dpWidth / 3;
        final int thumbnailWidthPx = (int)(thumbnailWidthDp * displayMetrics.density);
//                    final int thumbnailHeightPx = thumbnailWidthPx * 38 / 62;
        final int thumbnailHeightPx = thumbnailWidthPx * 7 / 10;

        RelativeLayout.LayoutParams imageLayoutParams = (RelativeLayout.LayoutParams)viewHolder.houseImageView.getLayoutParams();
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
            public void onResponse(Response<ImageInfo[]> response, Retrofit retrofit) {
                int statusCode = response.code();
                ImageInfo[] result = response.body();
                if (result != null && result.length > 0) {


//                    imageLayoutParams.width = thumbnailWidthPx;
//                    imageLayoutParams.height = thumbnailHeightPx;
//                    viewHolder.houseImageView.setLayoutParams(imageLayoutParams);

//                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)viewHolder.houseInfoLayout.getLayoutParams();
//                    layoutParams.height = thumbnailWidthPx * 7 / 10;
//                    viewHolder.houseInfoLayout.setLayoutParams(layoutParams);

//                    Picasso.with(context).load(result[0].getUrl()).resize(thumbnailWidthPx, thumbnailHeightPx).into(viewHolder.houseImageView, new com.squareup.picasso.Callback() {
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
                            //                                viewHolder.houseImageView.setBackgroundDrawable(context.getResources().getDrawable(android.R.drawable.stat_notify_error));
//                            viewHolder.houseImageView.setVisibility(View.GONE);
                            viewHolder.houseImageView.setImageDrawable(new ColorDrawable(context.getResources().getColor(R.color.gray_light)));
                        }
                    });
                } else {
                    if (viewHolder.imageProgressBarLayout != null && viewHolder.imageProgressBar != null) {
                        viewHolder.imageProgressBar.setVisibility(View.GONE);
                        viewHolder.imageProgressBarLayout.setVisibility(View.GONE);
                    }
//                    viewHolder.houseImageView.setBackgroundDrawable(context.getResources().getDrawable(android.R.drawable.stat_notify_error));
//                    viewHolder.houseImageView.setVisibility(View.GONE);
                    viewHolder.houseImageView.setImageDrawable(new ColorDrawable(context.getResources().getColor(R.color.gray_light)));
                }
            }

            @Override
            public void onFailure(Throwable t) {
//                viewHolder.houseImageView.setBackgroundDrawable(context.getResources().getDrawable(android.R.drawable.stat_notify_error));
//                viewHolder.houseImageView.setVisibility(View.GONE);
                viewHolder.houseImageView.setImageDrawable(new ColorDrawable(context.getResources().getColor(R.color.gray_light)));
//                Toast.makeText(context, "get house image exception : " + t.getMessage(), Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    private void getHouseAvailableDates(House house, final HostHousesItemViewHolder viewHolder) {

        Gson gson = new GsonBuilder().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.SERVER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = new Date();
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.MONTH, 3);
        Date endDate = calendar.getTime();
        String startDateString = simpleDateFormat.format(startDate);
        String endDateString = simpleDateFormat.format(endDate);

        String url = house.getURL();
        url = url + "/available-dates/start-" + startDateString + "/end-" + endDateString;

        Call<HouseAvailableDates> call = apiEndpoints.getHouseAvailableDates(url);
        call.enqueue(new Callback<HouseAvailableDates>() {
            @Override
            public void onResponse(Response<HouseAvailableDates> response, Retrofit retrofit) {
                HouseAvailableDates houseAvailableDates = response.body();
                if (houseAvailableDates != null && houseAvailableDates.getStartDate() != null) {

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
            public void onFailure(Throwable t) {
                t.printStackTrace();
                Log.d("HostHousesContent", "getHouseAvailableDates onFailure : " + t.getMessage(), t);
                viewHolder.houseDatesTextView.setText(context.getString(R.string.host_houses_first_available_date, ""));
            }
        });
    }

    private void openHouseDetail(String houseUrl) {
        Intent intent = new Intent(context, HouseActivity.class);
        intent.putExtra("houseUrl", houseUrl);
        context.startActivity(intent);
    }

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

            houseInfoLayout = (LinearLayout)itemView.findViewById(R.id.host_my_houses_itemInfoLayout);
            houseImageView = (ImageView)itemView.findViewById(R.id.host_my_houses_itemImage);
            houseTitleTextView = (TextView)itemView.findViewById(R.id.host_my_houses_itemTitle);
            houseDatesTextView = (TextView)itemView.findViewById(R.id.host_my_houses_itemAvailableDate);
            housePriceTextView = (TextView)itemView.findViewById(R.id.host_my_houses_itemPrice);
            imageProgressBarLayout = (LinearLayout)itemView.findViewById(R.id.host_my_houses_progressBarLayout);
            imageProgressBar = (ProgressBar)itemView.findViewById(R.id.host_my_houses_progressBar);

            houseTypeTextView = (TextView)itemView.findViewById(R.id.host_my_houses_itemHouseType);
            houseBedsTextView = (TextView)itemView.findViewById(R.id.host_my_houses_itemRemoveBeds);
            houseRoomsTextView = (TextView)itemView.findViewById(R.id.host_my_houses_itemRooms);

            viewHouseButton = (Button)itemView.findViewById(R.id.host_my_houses_itemViewButton);
            editHouseButton = (Button)itemView.findViewById(R.id.host_my_houses_itemEditButton);
            removeHouseButton = (Button)itemView.findViewById(R.id.host_my_houses_itemRemoveButton);
        }
    }

    public interface RemoveHouseListener {
        public void onRemoveHouse(House house);
    }
}
