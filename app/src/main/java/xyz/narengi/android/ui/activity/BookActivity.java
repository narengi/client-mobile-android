package xyz.narengi.android.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.narengi.android.R;
import xyz.narengi.android.common.Constants;
import xyz.narengi.android.common.dto.House;
import xyz.narengi.android.common.dto.HouseAvailableDates;
import xyz.narengi.android.service.RetrofitApiEndpoints;
import xyz.narengi.android.ui.adapter.BookContentRecyclerAdapter;
import xyz.narengi.android.ui.fragment.CalendarFragment;


/**
 * @author Siavash Mahmoudpour
 */
public class BookActivity extends AppCompatActivity {

    private House house;
    private BookContentRecyclerAdapter bookContentRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_calendar);
        setContentView(R.layout.activity_book);
        setupToolbar();

        if (getIntent() != null && getIntent().getSerializableExtra("house") != null) {
            Serializable houseSerializable = getIntent().getSerializableExtra("house");
            if (houseSerializable instanceof House) {
                house = (House) houseSerializable;
                if (house.getURL() != null) {
                    getHouseAvailableDates();
                }
            }
        }

//        setupCalendar();
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
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 501) {
            finish();
        }
    }

    private void setupToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.book_toolbar);

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

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.book_collapse_toolbar);
        collapsingToolbarLayout.setTitle(getString(R.string.book_page_title));
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(android.R.color.black));
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.black));
    }

    private void getHouseAvailableDates() {

        Gson gson = new GsonBuilder().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.SERVER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.MONTH, 3);
        Date endDate = calendar.getTime();
        String startDateString = dateFormat.format(startDate);
        String endDateString = dateFormat.format(endDate);

        String url = house.getURL();
        url = url + "/available-dates/start-" + startDateString + "/end-" + endDateString;

//        System.out.println("getHouseAvailableDates url : " + url);

        Call<HouseAvailableDates> call = apiEndpoints.getHouseAvailableDates(url);
        call.enqueue(new Callback<HouseAvailableDates>() {
            @Override
            public void onResponse(Response<HouseAvailableDates> response, Retrofit retrofit) {
//                int statusCode = response.code();
//                hideProgress();
                HouseAvailableDates houseAvailableDates = response.body();
                if (houseAvailableDates != null) {
                    setupContentRecyclerView(houseAvailableDates);
                    Button continueButton = (Button) findViewById(R.id.book_continueButton);
                    continueButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openBookSummary();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Throwable t) {
                // Log error here since request failed
                t.printStackTrace();
                Log.d("BookActivity", "getHouseAvailableDates onFailure : " + t.getMessage(), t);
//                hideProgress();
            }
        });
    }

    private void setupContentRecyclerView(HouseAvailableDates houseAvailableDates) {
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.book_recyclerView);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        bookContentRecyclerAdapter = new BookContentRecyclerAdapter(this, house, houseAvailableDates);
        mRecyclerView.setAdapter(bookContentRecyclerAdapter);

    }

    private void openBookSummary() {
        if (bookContentRecyclerAdapter != null && bookContentRecyclerAdapter.getBookProperties() != null &&
                bookContentRecyclerAdapter.getBookProperties().getArriveDay() != null && bookContentRecyclerAdapter.getBookProperties().getDepartDay() != null &&
                bookContentRecyclerAdapter.getBookProperties().getDaysCount() > 0 && bookContentRecyclerAdapter.getBookProperties().getGuestsCount() > 0) {

            Intent intent = new Intent(this, BookSummaryActivity.class);
            intent.putExtra("bookProperties" , bookContentRecyclerAdapter.getBookProperties());
            startActivity(intent);
        } else {
            Toast.makeText(this, getString(R.string.book_dates_not_selected_alert), Toast.LENGTH_LONG).show();
        }
    }

    private void setupCalendar() {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.book_calendar_fragment, new CalendarFragment(), "CalendarFragment")
                .commit();
//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.calendar_fragment, new CalendarFragment(), "CalendarFragment")
//                .commit();
    }
}
