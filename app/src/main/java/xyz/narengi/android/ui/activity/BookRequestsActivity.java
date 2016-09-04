package xyz.narengi.android.ui.activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import info.semsamot.actionbarrtlizer.ActionBarRtlizer;
import info.semsamot.actionbarrtlizer.RtlizeEverything;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.narengi.android.R;
import xyz.narengi.android.common.Constants;
import xyz.narengi.android.common.dto.Authorization;
import xyz.narengi.android.common.dto.BookRequest;
import xyz.narengi.android.common.dto.BookRequestDTO;
import xyz.narengi.android.common.dto.Credential;
import xyz.narengi.android.common.dto.House;
import xyz.narengi.android.content.CredentialDeserializer;
import xyz.narengi.android.service.RetrofitApiEndpoints;
import xyz.narengi.android.ui.fragment.BookRequestListFragment;

public class BookRequestsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ActionBarRtlizer rtlizer;
    private BookRequest[] bookRequests;
    private BookRequestDTO[] bookRequestDTOs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_requests);
        setupToolbar();
        setPageTitle(getString(R.string.book_requests_page_title));
//        showProgress();

        tabLayout = (TabLayout) findViewById(R.id.book_requests_tabs);
        viewPager = (ViewPager) findViewById(R.id.book_requests_viewpager);
        setupViewPager(viewPager, createDummyBookRequests());

        tabLayout.setupWithViewPager(viewPager);
        if (tabLayout != null && tabLayout.getTabCount() > 0) {
            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                tabLayout.getTabAt(i).setIcon(R.drawable.ic_action_hosting);
            }
        }
    }

    private BookRequestDTO[] createDummyBookRequests() {

        BookRequestDTO[] bookRequestDTOs = new BookRequestDTO[3];

        BookRequestDTO bookRequestDTO1 = new BookRequestDTO();
        bookRequestDTO1.setHouseTitle("ویلای رامسر");

        BookRequest[] bookRequests1 = new BookRequest[3];
        BookRequest bookRequest11 = new BookRequest();
        bookRequests1[0] = bookRequest11;
        BookRequest bookRequest12 = new BookRequest();
        bookRequests1[1] = bookRequest12;
        BookRequest bookRequest13 = new BookRequest();
        bookRequests1[2] = bookRequest13;

        bookRequestDTO1.setBookRequests(bookRequests1);
        bookRequestDTOs[0] = bookRequestDTO1;

        BookRequestDTO bookRequestDTO2 = new BookRequestDTO();
        bookRequestDTO2.setHouseTitle("سوئیت تهران");

        BookRequest[] bookRequests2 = new BookRequest[2];
        BookRequest bookRequest21 = new BookRequest();
        bookRequests2[0] = bookRequest21;
        BookRequest bookRequest22 = new BookRequest();
        bookRequests2[1] = bookRequest22;

        bookRequestDTO2.setBookRequests(bookRequests2);
        bookRequestDTOs[1] = bookRequestDTO2;

        BookRequestDTO bookRequestDTO3 = new BookRequestDTO();
        bookRequestDTO3.setHouseTitle("ویلای رامسر");

        BookRequest[] bookRequests3 = new BookRequest[3];
        BookRequest bookRequest31 = new BookRequest();
        bookRequests3[0] = bookRequest31;
        BookRequest bookRequest32 = new BookRequest();
        bookRequests3[1] = bookRequest32;
        BookRequest bookRequest33 = new BookRequest();
        bookRequests3[2] = bookRequest33;

        bookRequestDTO3.setBookRequests(bookRequests3);
        bookRequestDTOs[2] = bookRequestDTO3;

        return bookRequestDTOs;
    }

    private void setPageTitle(String title) {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.book_requests_toolbar);
        if (toolbar != null) {
            TextView titleTextView = (TextView)toolbar.findViewById(R.id.text_toolbar_title);
            titleTextView.setText(title);
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

    private void showProgress() {
        LinearLayout progressBarLayout = (LinearLayout)findViewById(R.id.book_requests_progressLayout);
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.book_requests_progressBar);

        if (progressBarLayout != null && progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
            progressBarLayout.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgress() {
        LinearLayout progressBarLayout = (LinearLayout)findViewById(R.id.book_requests_progressLayout);
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.book_requests_progressBar);

        if (progressBarLayout != null && progressBar != null) {
            progressBar.setVisibility(View.GONE);
            progressBarLayout.setVisibility(View.GONE);
        }
    }


    private void setupToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.book_requests_toolbar);

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

    private void setupViewPager(ViewPager viewPager, BookRequest[] bookRequests) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        for (BookRequest bookRequest:bookRequests) {

            if (bookRequest.getHouse() != null) {
                adapter.addFragment(new BookRequestListFragment(), bookRequest.getHouse().getName());
            }
        }

        viewPager.setAdapter(adapter);

        viewPager.setCurrentItem(bookRequests.length - 1);
    }

    private void setupViewPager(ViewPager viewPager, BookRequestDTO[] bookRequestDTOs) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        for (BookRequestDTO bookRequestDTO:bookRequestDTOs) {

            if (bookRequestDTO.getHouseTitle() != null) {
                BookRequestListFragment bookRequestListFragment = new BookRequestListFragment();
                bookRequestListFragment.setBookRequests(bookRequestDTO.getBookRequests());
                adapter.addFragment(bookRequestListFragment, bookRequestDTO.getHouseTitle());
            }
        }

        viewPager.setAdapter(adapter);

        viewPager.setCurrentItem(bookRequestDTOs.length - 1);
    }

    private void getBookRequests() {
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
        Call<BookRequest[]> call = apiEndpoints.getBookRequests(authorizationJson);

        call.enqueue(new Callback<BookRequest[]>() {
            @Override
            public void onResponse(Response<BookRequest[]> response, Retrofit retrofit) {
                hideProgress();
                int statusCode = response.code();
                bookRequests = response.body();

                if (bookRequests == null || bookRequests.length == 0) {
                    setupViewPager(viewPager, bookRequests);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                hideProgress();
                t.printStackTrace();
            }
        });
    }


    private void getBookRequestDTOs() {
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
        Call<BookRequestDTO[]> call = apiEndpoints.getBookRequestDTOs(authorizationJson);

        call.enqueue(new Callback<BookRequestDTO[]>() {
            @Override
            public void onResponse(Response<BookRequestDTO[]> response, Retrofit retrofit) {
                hideProgress();
                int statusCode = response.code();
                bookRequestDTOs = response.body();

                if (bookRequestDTOs == null || bookRequestDTOs.length == 0) {
                    setupViewPager(viewPager, bookRequestDTOs);
                }            }

            @Override
            public void onFailure(Throwable t) {
                hideProgress();
                t.printStackTrace();
            }
        });
    }


    private void acceptBookRequest(String requestId) {

        showProgress();

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
        Call<BookRequest> call = apiEndpoints.approveBookRequest(authorizationJson, requestId);

        call.enqueue(new Callback<BookRequest>() {
            @Override
            public void onResponse(Response<BookRequest> response, Retrofit retrofit) {
                int statusCode = response.code();
                BookRequest bookRequest = response.body();
                if (statusCode != 204) {
                    try {
                        if (response.errorBody() != null) {
                            Toast.makeText(BookRequestsActivity.this, response.errorBody().string(), Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(BookRequestsActivity.this, "Book request accepted", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Throwable t) {
                hideProgress();
                Toast.makeText(BookRequestsActivity.this, "Exception : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
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
