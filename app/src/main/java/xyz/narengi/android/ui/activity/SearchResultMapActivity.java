package xyz.narengi.android.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.byagowi.persiancalendar.Utils;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.AroundLocation;
import xyz.narengi.android.common.dto.AroundPlaceAttraction;
import xyz.narengi.android.common.dto.AroundPlaceCity;
import xyz.narengi.android.common.dto.AroundPlaceHouse;
import xyz.narengi.android.common.dto.GeoPoint;
import xyz.narengi.android.common.dto.SuggestionsResult;
import xyz.narengi.android.content.AroundLocationDeserializer;
import xyz.narengi.android.content.AroundPlaceAttractionDeserializer;
import xyz.narengi.android.content.AroundPlaceCityDeserializer;
import xyz.narengi.android.content.AroundPlaceHouseDeserializer;
import xyz.narengi.android.service.RetrofitApiEndpoints;
import xyz.narengi.android.service.RetrofitService;
import xyz.narengi.android.service.SuggestionsServiceAsyncTask;
import xyz.narengi.android.ui.adapter.SuggestionsRecyclerAdapter;

/**
 * @author Siavash Mahmoudpour
 */
public class SearchResultMapActivity extends AppCompatActivity implements OnMapReadyCallback/*, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener*/
        , ClusterManager.OnClusterClickListener<SearchResultMapActivity.AroundLocationClusterItem>,
        ClusterManager.OnClusterInfoWindowClickListener<SearchResultMapActivity.AroundLocationClusterItem>,
        ClusterManager.OnClusterItemClickListener<SearchResultMapActivity.AroundLocationClusterItem>,
        ClusterManager.OnClusterItemInfoWindowClickListener<SearchResultMapActivity.AroundLocationClusterItem> {


    private GoogleMap mMap;
    private AroundLocation[] aroundLocations;
    private List<AroundLocation> aroundLocationList;
    private EditText searchEditText;
    private View.OnFocusChangeListener searchOnFocusChangeListener;
    private boolean loadingMore;
    private RecyclerView searchResultListView;
    private RecyclerView searchHistoryListView;
    private boolean isShowingSuggestions = false;
    private TextWatcher searchTextWatcher;
    private RecyclerView.OnItemTouchListener suggestionsOnItemTouchListener;

    private GoogleApiClient googleApiClient;
    private Location lastLocation;

    private ClusterManager<SearchResultMapActivity.AroundLocationClusterItem> mClusterManager;
    private List<SearchResultMapActivity.AroundLocationClusterItem> clusterItems;
    private CameraPosition cameraPosition;
    private Cluster<AroundLocationClusterItem> clickedCluster;
    private AroundLocationClusterItem clickedClusterItem;

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            if (lastLocation == null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14));
            }
            lastLocation = location;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result_map);

        // Create an instance of GoogleAPIClient.
//        if (googleApiClient == null) {
//            googleApiClient = new GoogleApiClient.Builder(this)
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .addApi(LocationServices.API)
//                    .build();
//        }

        int isGooglePlayServicesAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (isGooglePlayServicesAvailable > 0) {
            try {
                GooglePlayServicesUtil.getErrorDialog(isGooglePlayServicesAvailable, this, 0).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        LocationManager service = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = service.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        } else {
            lastLocation = service.getLastKnownLocation(provider);
        }

        String query = "";
        if (getIntent() != null && getIntent().getStringExtra("query") != null) {
            query = getIntent().getStringExtra("query");
        }

        setupToolbar(query);
//        setupToolbar(null);

//        searchEditText.removeTextChangedListener(searchTextWatcher);
//        searchEditText.setOnFocusChangeListener(null);

//        searchEditText.setText(query);
//        closeSearchSuggestions(query);
        searchEditText.clearFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(searchEditText.getWindowToken(), InputMethodManager.SHOW_FORCED);

//        searchEditText.addTextChangedListener(searchTextWatcher);
//        searchEditText.setOnFocusChangeListener(searchOnFocusChangeListener);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.search_result_map_fragment);
        mapFragment.getMapAsync(this);

        if (getIntent() != null && getIntent().getSerializableExtra("aroundLocations") != null) {
            Object object = getIntent().getSerializableExtra("aroundLocations");
            if (object instanceof AroundLocation[]) {
                AroundLocation[] aroundLocations = (AroundLocation[]) object;
                aroundLocationList = Arrays.asList(aroundLocations);
                setupMarkers(aroundLocationList);
            }
        }
    }

    protected void onStart() {
//        googleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
//        googleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar(String query) {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.search_result_map_toolbar);

        if (toolbar != null) {
            final ImageButton settingsImageButton = (ImageButton) findViewById(R.id.search_result_toolbar_action_settings);
            final ImageButton mapImageButton = (ImageButton) findViewById(R.id.search_result_toolbar_action_map);
            mapImageButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_close_clear_cancel));
            searchEditText = (EditText) findViewById(R.id.search_result_toolbar_action_search);
            searchEditText.setHint(R.string.search_hint);
            if (query != null)
                searchEditText.setText(query);

//            final Handler handler = new Handler();

//            searchTextWatcher = new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                }
//
//                @Override
//                public void afterTextChanged(Editable editable) {
//                    final String s = editable.toString();
//
//                    if (s.length() == 0) {
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                showSearchHistoryRecyclerView();
//                                updateToolbarScrollFlags(false);
//                            }
//
//                        }, 5);
//                    } else {
//
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                showSearchResultRecyclerView(searchEditText.getText().toString());
//                                updateToolbarScrollFlags(false);
//                            }
//                        }, 5);
//                    }
//                }
//            };

//            searchEditText.addTextChangedListener(searchTextWatcher);

//            searchOnFocusChangeListener = new View.OnFocusChangeListener() {
//                @Override
//                public void onFocusChange(View view, boolean b) {
//                    if (b) {
//                        if (searchEditText.getText().toString().length() == 0) {
//                            handler.postAtFrontOfQueue(new Runnable() {
//                                @Override
//                                public void run() {
//                                    showSearchHistoryRecyclerView();
//                                    updateToolbarScrollFlags(false);
//                                }
//
//                            });
//                        } else {
//                            handler.postAtFrontOfQueue(new Runnable() {
//                                @Override
//                                public void run() {
//                                    showSearchResultRecyclerView(searchEditText.getText().toString());
//                                    updateToolbarScrollFlags(false);
//                                }
//                            });
//                        }
//                        view.requestFocus();
//
//                        settingsImageButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_back));
//                        mapImageButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_close_clear_cancel));
//                    } else {
//                        mapImageButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_sort_by_size));
//                    }
//                }
//            };
//
//            searchEditText.setOnFocusChangeListener(searchOnFocusChangeListener);

            searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        if (searchEditText.getText().toString().length() > 0) {
                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(searchEditText.getWindowToken(), InputMethodManager.SHOW_FORCED);
                            storeSearchQuery(searchEditText.getText().toString());
//                            closeSearchSuggestions(searchEditText.getText().toString());
                            aroundLocationList = new ArrayList<AroundLocation>();
                            setupMarkers(aroundLocationList);
                            showProgress();
                            LoadDataAsyncTask loadDataAsyncTask = new LoadDataAsyncTask(searchEditText.getText().toString());
                            loadDataAsyncTask.execute();
                            isShowingSuggestions = false;
                            return true;
                        }
                    }
                    return false;
                }
            });

            settingsImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    searchMenuButtonOnClick();
                }
            });

            mapImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    searchMapButtonOnClick();
                }
            });

        }

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    private void updateToolbarScrollFlags(boolean canScroll) {

        Toolbar toolbar = (Toolbar) findViewById(R.id.search_result_map_toolbar);
        if (toolbar == null)
            return;

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.search_result_map_collapsing_toolbar);
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) collapsingToolbar.getLayoutParams();

        if (canScroll) {
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                    | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
        } else {
            params.setScrollFlags(0);
        }

        collapsingToolbar.setLayoutParams(params);
    }

    private void showProgress() {
        LinearLayout progressBarLayout = (LinearLayout) findViewById(R.id.search_result_map_progressLayout);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.search_result_map_progressBar);

        if (progressBar != null && progressBarLayout != null) {
            progressBar.setVisibility(View.VISIBLE);
            progressBarLayout.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgress() {
        LinearLayout progressBarLayout = (LinearLayout) findViewById(R.id.search_result_map_progressLayout);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.search_result_map_progressBar);

        if (progressBar != null && progressBarLayout != null) {
            progressBar.setVisibility(View.GONE);
            progressBarLayout.setVisibility(View.GONE);
        }
    }

    private void showSearchHistoryRecyclerView() {
        LinearLayout toolbarInnerLayout = (LinearLayout) findViewById(R.id.search_result_toolbar_main_layout);
        toolbarInnerLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                closeSearchSuggestions(searchEditText.getText().toString());
                return false;
            }
        });

        searchHistoryListView = (RecyclerView) findViewById(R.id.search_result_suggestionsRecyclerView);

        SharedPreferences preferences = getSharedPreferences("suggestions", 0);
        String history = preferences.getString("searchHistory", "");

        StringTokenizer tokenizer = new StringTokenizer(history, ",");
        List<String> tokenList = new ArrayList<String>();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            tokenList.add(token);
        }

        final String[] historyItems;

        if (tokenList.size() > 0) {
            historyItems = new String[tokenList.size()];
            tokenList.toArray(historyItems);
        } else {
            historyItems = null;
        }

        if (historyItems == null || historyItems.length == 0)
            return;

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        searchHistoryListView.setLayoutManager(mLayoutManager);

        SuggestionsRecyclerAdapter recyclerAdapter = new SuggestionsRecyclerAdapter(this, historyItems);
        searchHistoryListView.setAdapter(recyclerAdapter);

        final GestureDetector mGestureDetector;
        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });

        if (suggestionsOnItemTouchListener != null)
            searchHistoryListView.removeOnItemTouchListener(suggestionsOnItemTouchListener);

        suggestionsOnItemTouchListener = new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent e) {

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(searchEditText.getWindowToken(), InputMethodManager.SHOW_FORCED);

                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                int position = recyclerView.getChildAdapterPosition(childView);

                if (childView == null) {
                    closeSearchSuggestions(searchEditText.getText().toString());
                } else {
                    if (position >= 0 && mGestureDetector.onTouchEvent(e)) {
                        if (historyItems.length > position) {
                            Object item = historyItems[position];
                            if (item != null) {
                                closeSearchSuggestions(item.toString());
                                aroundLocationList = new ArrayList<AroundLocation>();
                                setupMarkers(aroundLocationList);
                                showProgress();
                                LoadDataAsyncTask loadDataAsyncTask = new LoadDataAsyncTask(item.toString());
                                loadDataAsyncTask.execute();
                            }
                        }
                    }
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        };

        searchHistoryListView.addOnItemTouchListener(suggestionsOnItemTouchListener);

        searchHistoryListView.setVisibility(View.VISIBLE);
        searchHistoryListView.invalidate();
        isShowingSuggestions = true;
    }

    private SuggestionsResult getAroundLocationSuggestions(String query) {
        SuggestionsServiceAsyncTask suggestionsAsyncTask = new SuggestionsServiceAsyncTask(query);
        Object object = null;
        try {
            object = suggestionsAsyncTask.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (object != null)
            return (SuggestionsResult) object;

        return null;
    }

    private void showSearchResultRecyclerView(String query) {

        LinearLayout toolbarInnerLayout = (LinearLayout) findViewById(R.id.search_result_toolbar_main_layout);
        toolbarInnerLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                closeSearchSuggestions(searchEditText.getText().toString());
                return false;
            }
        });

        final List suggestions = new ArrayList();

        SuggestionsResult suggestionsResult = getAroundLocationSuggestions(query);
        if (suggestionsResult != null) {

            if (suggestionsResult.getCity() != null && suggestionsResult.getCity().length > 0) {
                suggestions.addAll(Arrays.asList(suggestionsResult.getCity()));
            }
            if (suggestionsResult.getAttraction() != null && suggestionsResult.getAttraction().length > 0) {
                suggestions.addAll(Arrays.asList(suggestionsResult.getAttraction()));
            }
            if (suggestionsResult.getHouse() != null && suggestionsResult.getHouse().length > 0) {
                suggestions.addAll(Arrays.asList(suggestionsResult.getHouse()));
            }
        }

        searchResultListView = (RecyclerView) findViewById(R.id.search_result_suggestionsRecyclerView);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        searchResultListView.setLayoutManager(mLayoutManager);

        SuggestionsRecyclerAdapter recyclerAdapter = new SuggestionsRecyclerAdapter(this, suggestions.toArray());
        searchResultListView.setAdapter(recyclerAdapter);

        final GestureDetector mGestureDetector;
        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        if (suggestionsOnItemTouchListener != null)
            searchResultListView.removeOnItemTouchListener(suggestionsOnItemTouchListener);
        suggestionsOnItemTouchListener = new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent e) {

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(searchEditText.getWindowToken(), InputMethodManager.SHOW_FORCED);

                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                int position = recyclerView.getChildAdapterPosition(childView);

                if (childView == null) {
                    closeSearchSuggestions(searchEditText.getText().toString());
                } else {
                    if (position >= 0 && mGestureDetector.onTouchEvent(e)) {
                        if (suggestions.size() > position) {

                            Object suggestion = suggestions.get(position);
                            if (suggestion instanceof AroundPlaceCity) {
                                openCityDetail((AroundPlaceCity) suggestion);
                            } else if (suggestion instanceof AroundPlaceAttraction) {
                                openAttractionDetail((AroundPlaceAttraction) suggestion);
                            } else if (suggestion instanceof AroundPlaceHouse) {
                                openHouseDetail((AroundPlaceHouse) suggestion);
                            }
                        }
                    }
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        };

        searchResultListView.addOnItemTouchListener(suggestionsOnItemTouchListener);

        searchResultListView.setVisibility(View.VISIBLE);
        searchResultListView.invalidate();
        isShowingSuggestions = true;
    }

    private void storeSearchQuery(String query) {
        SharedPreferences preferences = getSharedPreferences("suggestions", 0);
        SharedPreferences.Editor editor = preferences.edit();

        String history = preferences.getString("searchHistory", "");

        StringBuilder builder = new StringBuilder();
        builder.append(query);

        StringTokenizer tokenizer = new StringTokenizer(history, ",");
        List<String> tokenList = new ArrayList<String>();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (token != null && !token.equalsIgnoreCase(query))
                tokenList.add(token);
        }

        if (tokenList.size() > 0) {
            builder.append(",");

            int count = Math.min(3, tokenList.size());
            for (int i = 0; i < count; i++) {
                builder.append(tokenList.get(i));

                if (i < (count - 1))
                    builder.append(",");
            }
        }

        editor.putString("searchHistory", builder.toString());
        editor.commit();
    }

    private void searchMapButtonOnClick() {

        if (searchEditText.hasFocus()) {
            searchEditText.setText("");
        } else {
            finish();
        }
    }

    private void searchMenuButtonOnClick() {
        isShowingSuggestions = false;
        onBackPressed();
    }

    private void closeSearchSuggestions(String newQuery) {
//        final ImageButton settingsImageButton = (ImageButton)findViewById(R.id.search_result_toolbar_action_settings);
        final ImageButton mapImageButton = (ImageButton) findViewById(R.id.search_result_toolbar_action_map);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.search_result_suggestionsRecyclerView);
        searchEditText.setOnFocusChangeListener(null);
        searchEditText.removeTextChangedListener(searchTextWatcher);
        if (newQuery != null)
            searchEditText.setText(newQuery);
        else
            searchEditText.setText("");
        searchEditText.clearFocus();
//        settingsImageButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_navigation_menu));
        mapImageButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_sort_by_size));

        recyclerView.setVisibility(View.INVISIBLE);
        recyclerView.invalidate();

        searchEditText.setOnFocusChangeListener(searchOnFocusChangeListener);
        searchEditText.addTextChangedListener(searchTextWatcher);

        updateToolbarScrollFlags(true);
        isShowingSuggestions = false;
    }

    private void setupMarkers(List<AroundLocation> aroundLocations) {

        if (mMap == null)
            return;

        if (aroundLocations == null || aroundLocations.size() == 0) {
            mMap.clear();
            if (mClusterManager != null)
                mClusterManager.clearItems();
            return;
        }

        float distance = Float.MAX_VALUE;
        LatLng nearestLatLng = null;
        List<LatLng> allRandomPositions = new ArrayList<LatLng>();
        for (AroundLocation aroundLocation : aroundLocations) {

            GeoPoint position = null;
            if (aroundLocation.getType() != null && "City".equals(aroundLocation.getType()) &&
                    aroundLocation.getData() instanceof AroundPlaceCity) {
                AroundPlaceCity city = (AroundPlaceCity) aroundLocation.getData();
                position = city.getPosition();
            } else if (aroundLocation.getType() != null && "Attraction".equals(aroundLocation.getType()) &&
                    aroundLocation.getData() instanceof AroundPlaceAttraction) {
                AroundPlaceAttraction attraction = (AroundPlaceAttraction) aroundLocation.getData();
                position = attraction.getPosition();
            } else if (aroundLocation.getType() != null && "House".equals(aroundLocation.getType()) &&
                    aroundLocation.getData() instanceof AroundPlaceHouse) {
                AroundPlaceHouse house = (AroundPlaceHouse) aroundLocation.getData();
                position = house.getPosition();
            }

            if (position != null) {
                LatLng latLng = new LatLng(position.getLat(), position.getLng());

                if (lastLocation != null) {
                    Location location = new Location("");
                    location.setLatitude(latLng.latitude);
                    location.setLongitude(latLng.longitude);
                    float newDistance = lastLocation.distanceTo(location);
                    if (newDistance < distance) {
                        distance = newDistance;
                        nearestLatLng = latLng;
                    }
                }

                List<LatLng> randomPositions = getRandomLocation(latLng, 1000);
                if (randomPositions != null && randomPositions.size() > 0) {
                    allRandomPositions.addAll(randomPositions);
                    for (LatLng randomLatLng : randomPositions) {
//                        mMap.addMarker(new MarkerOptions().position(randomLatLng));
                        if (lastLocation != null) {
                            Location location = new Location("");
                            location.setLatitude(randomLatLng.latitude);
                            location.setLongitude(randomLatLng.longitude);
                            float newDistance = lastLocation.distanceTo(location);
                            if (newDistance < distance) {
                                distance = newDistance;
                                nearestLatLng = randomLatLng;
                            }
                        }
                    }
                }

//                mMap.addMarker(new MarkerOptions().position(latLng));
            }
        }

        setUpCluster();
        addClusterItems(allRandomPositions);

        if (nearestLatLng != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nearestLatLng, 14));
        } else if (lastLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), 14));
        }
    }

    public List<LatLng> getRandomLocation(LatLng point, int radius) {

        List<LatLng> randomPoints = new ArrayList<>();
//        List<Float> randomDistances = new ArrayList<>();
        Location myLocation = new Location("");
        myLocation.setLatitude(point.latitude);
        myLocation.setLongitude(point.longitude);

        //This is to generate 10 random points
        for (int i = 0; i < 20; i++) {
            double x0 = point.latitude;
            double y0 = point.longitude;

            Random random = new Random();

            // Convert radius from meters to degrees
            double radiusInDegrees = radius / 111000f;

            double u = random.nextDouble();
            double v = random.nextDouble();
            double w = radiusInDegrees * Math.sqrt(u);
            double t = 2 * Math.PI * v;
            double x = w * Math.cos(t);
            double y = w * Math.sin(t);

            // Adjust the x-coordinate for the shrinking of the east-west distances
            double new_x = x / Math.cos(y0);

            double foundLatitude = new_x + x0;
            double foundLongitude = y + y0;
            LatLng randomLatLng = new LatLng(foundLatitude, foundLongitude);
            randomPoints.add(randomLatLng);
//            Location l1 = new Location("");
//            l1.setLatitude(randomLatLng.latitude);
//            l1.setLongitude(randomLatLng.longitude);
//            randomDistances.add(l1.distanceTo(myLocation));
        }
        //Get nearest point to the centre
//        int indexOfNearestPointToCentre = randomDistances.indexOf(Collections.min(randomDistances));
//        return randomPoints.get(indexOfNearestPointToCentre);

        return randomPoints;
    }

    private void setUpCluster() {

        mClusterManager = new ClusterManager<AroundLocationClusterItem>(this, mMap) {


            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                super.onCameraChange(cameraPosition);

                if (mMap == null)
                    return;
                LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;

                LatLng northeast = bounds.northeast;
                String boundLat = String.valueOf(northeast.latitude);
                String boundLong = String.valueOf(northeast.longitude);

                LatLng southwest = bounds.southwest;

                String boundLat2 = String.valueOf(southwest.latitude);
                String boundLong2 = String.valueOf(southwest.longitude);

//                addClusterItems();

                mClusterManager.cluster();

            }
        };
        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraChangeListener(mClusterManager);
        mClusterManager.setRenderer(new AroundLocationClusterRenderer(this, mMap, mClusterManager));
        mMap.setOnInfoWindowClickListener(mClusterManager);
        mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());
        mClusterManager.getClusterMarkerCollection().setOnInfoWindowAdapter(new MyCustomAdapterForClusters(getLayoutInflater()));
        mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(new MyCustomAdapterForItems(getLayoutInflater()));
        mMap.setOnMarkerClickListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);

        mClusterManager
                .setOnClusterClickListener(new ClusterManager.OnClusterClickListener<AroundLocationClusterItem>() {
                    @Override
                    public boolean onClusterClick(Cluster<AroundLocationClusterItem> cluster) {
                        clickedCluster = cluster;
                        return false;
                    }
                });

        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<AroundLocationClusterItem>() {
            @Override
            public boolean onClusterItemClick(AroundLocationClusterItem item) {
                clickedClusterItem = item;
                return false;
            }
        });
    }

    private void addClusterItems(List<LatLng> randomPositions) {

        if (clusterItems == null)
            clusterItems = new ArrayList<AroundLocationClusterItem>();

        if (aroundLocationList != null) {
            for (AroundLocation aroundLocation : aroundLocationList) {
                if (aroundLocation.getData() != null) {
                    GeoPoint position = null;
                    if (aroundLocation.getType() != null && "City".equals(aroundLocation.getType()) &&
                            aroundLocation.getData() instanceof AroundPlaceCity) {
                        AroundPlaceCity city = (AroundPlaceCity) aroundLocation.getData();
                        position = city.getPosition();
                    } else if (aroundLocation.getType() != null && "Attraction".equals(aroundLocation.getType()) &&
                            aroundLocation.getData() instanceof AroundPlaceAttraction) {
                        AroundPlaceAttraction attraction = (AroundPlaceAttraction) aroundLocation.getData();
                        position = attraction.getPosition();
                    } else if (aroundLocation.getType() != null && "House".equals(aroundLocation.getType()) &&
                            aroundLocation.getData() instanceof AroundPlaceHouse) {
                        AroundPlaceHouse house = (AroundPlaceHouse) aroundLocation.getData();
                        position = house.getPosition();
                    }

                    if (position != null) {
                        AroundLocationClusterItem clusterItem = new AroundLocationClusterItem(aroundLocation,
                                new LatLng(position.getLat(), position.getLng()));
                        clusterItems.add(clusterItem);
                        mClusterManager.addItem(clusterItem);
                    }
                }
            }
        }

//        if (randomPositions != null) {
//            for (LatLng latLng:randomPositions) {
//                AroundLocationClusterItem clusterItem = new AroundLocationClusterItem(latLng);
//                clusterItems.add(clusterItem);
//                mClusterManager.addItem(clusterItem);
//            }
//        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        } else {
            googleMap.setMyLocationEnabled(true);
            googleMap.setOnMyLocationChangeListener(myLocationChangeListener);

            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        }

        TypedValue typedValue = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true)) {
            int actionBarHeight = TypedValue.complexToDimensionPixelSize(typedValue.data, getResources().getDisplayMetrics());
            mMap.setPadding(0, actionBarHeight + 10, 0, 0);
        }

        if (aroundLocationList != null && aroundLocationList.size() > 0)
            setupMarkers(aroundLocationList);
        else mMap.clear();
    }

    private void openCityDetail(AroundPlaceCity city) {
        String cityUrl = city.getURL();
        Intent intent = new Intent(this, CityActivity.class);
        intent.putExtra("cityUrl", cityUrl);
//        closeSearchSuggestions("");
        startActivity(intent);
    }

    private void openAttractionDetail(AroundPlaceAttraction attraction) {
        String attractionUrl = attraction.getURL();
        Intent intent = new Intent(this, AttractionActivity.class);
        intent.putExtra("attractionUrl", attractionUrl);
//        closeSearchSuggestions("");
        startActivity(intent);
    }

    private void openHouseDetail(AroundPlaceHouse house) {
        String houseUrl = house.getURL();
        Intent intent = new Intent(this, HouseActivity.class);
        intent.putExtra("houseUrl", houseUrl);
//        closeSearchSuggestions("");
        startActivity(intent);
    }

    @Override
    public boolean onClusterClick(Cluster<AroundLocationClusterItem> cluster) {
        return false;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<AroundLocationClusterItem> cluster) {

    }

    @Override
    public boolean onClusterItemClick(AroundLocationClusterItem aroundLocationClusterItem) {
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(AroundLocationClusterItem aroundLocationClusterItem) {

        if (aroundLocationClusterItem.getAroundLocation() != null) {
            AroundLocation aroundLocation = aroundLocationClusterItem.getAroundLocation();

            if (aroundLocation.getType() != null && "City".equals(aroundLocation.getType()) &&
                    aroundLocation.getData() instanceof AroundPlaceCity) {
                AroundPlaceCity city = (AroundPlaceCity) aroundLocation.getData();
                openCityDetail(city);
            } else if (aroundLocation.getType() != null && "Attraction".equals(aroundLocation.getType()) &&
                    aroundLocation.getData() instanceof AroundPlaceAttraction) {
                AroundPlaceAttraction attraction = (AroundPlaceAttraction) aroundLocation.getData();
                openAttractionDetail(attraction);
            } else if (aroundLocation.getType() != null && "House".equals(aroundLocation.getType()) &&
                    aroundLocation.getData() instanceof AroundPlaceHouse) {
                AroundPlaceHouse house = (AroundPlaceHouse) aroundLocation.getData();
                openHouseDetail(house);
            }
        }

    }

    /*@Override
    public void onConnected(Bundle bundle) {
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }*/

    public class AroundLocationClusterItem implements ClusterItem {

        private AroundLocation aroundLocation;
        private LatLng latLng;

        public AroundLocationClusterItem(AroundLocation aroundLocation, LatLng latLng) {
            this.aroundLocation = aroundLocation;
            this.latLng = latLng;
        }

        public AroundLocationClusterItem(LatLng latLng) {
            this.latLng = latLng;
        }

        public AroundLocation getAroundLocation() {
            return aroundLocation;
        }

        public void setAroundLocation(AroundLocation aroundLocation) {
            this.aroundLocation = aroundLocation;
        }

        @Override
        public LatLng getPosition() {
            return latLng;
        }
    }

    class AroundLocationClusterRenderer extends DefaultClusterRenderer<AroundLocationClusterItem> {

        private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext());
        private final ImageView mImageView;
        private final ImageView mClusterImageView;
//        private final int mDimension;

        public AroundLocationClusterRenderer(Context context, GoogleMap map,
                                             ClusterManager<AroundLocationClusterItem> clusterManager) {
            super(context, map, clusterManager);

            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            int imageWidth = (int) (80 * displayMetrics.density);
            int imageHeight = (int) (60 * displayMetrics.density);
//            int padding = (int)(2 * displayMetrics.density);
            int padding = 0;

            View imageMarkerView = getLayoutInflater().inflate(R.layout.map_image_marker, null);
            mClusterIconGenerator.setContentView(imageMarkerView);
            mClusterImageView = (ImageView) imageMarkerView.findViewById(R.id.map_image_marker_image);

            mImageView = new ImageView(getApplicationContext());
//            mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
//            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
//            int padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
//            mImageView.setPadding(padding, padding, padding, padding);
            mImageView.setPadding(padding, padding, padding, padding);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(imageWidth, imageHeight));
            mIconGenerator.setContentView(mImageView);
        }

        @Override
        protected void onBeforeClusterItemRendered(AroundLocationClusterItem item,
                                                   MarkerOptions markerOptions) {
            super.onBeforeClusterItemRendered(item, markerOptions);

//            mImageView.setImageResource(person.profilePhoto);
//            Bitmap icon = mIconGenerator.makeIcon();
//            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(person.name);

            if (item.getAroundLocation() != null) {

                DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                int imageWidth = (int) (80 * displayMetrics.density);
                int imageHeight = (int) (60 * displayMetrics.density);

                AroundLocation aroundLocation = item.getAroundLocation();
                IconGenerator iconGenerator = new IconGenerator(SearchResultMapActivity.this);
                iconGenerator.setColor(getResources().getColor(R.color.orange_light));
                iconGenerator.setTextAppearance(R.style.MapMarkerTextAppearance);

                if (aroundLocation.getType() != null && "City".equals(aroundLocation.getType()) &&
                        aroundLocation.getData() instanceof AroundPlaceCity) {
                    AroundPlaceCity city = (AroundPlaceCity) aroundLocation.getData();

//                    iconGenerator.setBackground(new ColorDrawable(getResources().getColor(R.color.green_dark)));

                    if (city.getImages() != null && city.getImages().length > 0) {
                        ImageDownloaderAsyncTask imageDownloaderAsyncTask = new ImageDownloaderAsyncTask(
                                SearchResultMapActivity.this, city.getImages()[0], imageWidth, imageHeight);
                        AsyncTask asyncTask = imageDownloaderAsyncTask.execute();
                        try {
                            Object asyncTaskResult = asyncTask.get();
                            if (asyncTaskResult != null) {
                                markerOptions.icon(BitmapDescriptorFactory.fromBitmap((Bitmap) asyncTaskResult));

                                mImageView.setImageBitmap((Bitmap) asyncTaskResult);
                                Bitmap icon = mIconGenerator.makeIcon();
                                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(city.getName());

                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Bitmap iconBitmap = iconGenerator.makeIcon(city.getName());
                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(iconBitmap));
                    }
//                    titleTextView.setText("\u200e" + city.getName());

                } else if (aroundLocation.getType() != null && "Attraction".equals(aroundLocation.getType()) &&
                        aroundLocation.getData() instanceof AroundPlaceAttraction) {
                    AroundPlaceAttraction attraction = (AroundPlaceAttraction) aroundLocation.getData();
//                    iconGenerator.setBackground(new ColorDrawable(getResources().getColor(R.color.green)));

                    if (attraction.getImages() != null && attraction.getImages().length > 0) {
                        ImageDownloaderAsyncTask imageDownloaderAsyncTask = new ImageDownloaderAsyncTask(
                                SearchResultMapActivity.this, attraction.getImages()[0], imageWidth, imageHeight);
                        AsyncTask asyncTask = imageDownloaderAsyncTask.execute();
                        try {
                            Object asyncTaskResult = asyncTask.get();
                            if (asyncTaskResult != null) {
                                markerOptions.icon(BitmapDescriptorFactory.fromBitmap((Bitmap) asyncTaskResult));

                                mImageView.setImageBitmap((Bitmap) asyncTaskResult);
                                Bitmap icon = mIconGenerator.makeIcon();
                                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(attraction.getName());
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Bitmap iconBitmap = iconGenerator.makeIcon(attraction.getName());
                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(iconBitmap));
                    }

                } else if (aroundLocation.getType() != null && "House".equals(aroundLocation.getType()) &&
                        aroundLocation.getData() instanceof AroundPlaceHouse) {
                    AroundPlaceHouse house = (AroundPlaceHouse) aroundLocation.getData();

//                    iconGenerator.setBackground(new ColorDrawable(getResources().getColor(R.color.orange_light)));
                    Bitmap iconBitmap = iconGenerator.makeIcon(house.getCost());
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(iconBitmap));
                }
            }
        }

        @Override
        protected void onClusterItemRendered(AroundLocationClusterItem clusterItem, Marker marker) {
            super.onClusterItemRendered(clusterItem, marker);
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            return cluster.getSize() > 10;
        }
    }

    // InfoWindowAdapter for Main Clusters.
    public class MyCustomAdapterForClusters implements GoogleMap.InfoWindowAdapter {

        private View myContentsView;
        private LayoutInflater inflater;

        MyCustomAdapterForClusters(LayoutInflater inflater) {
            this.inflater = inflater;
        }

        @Override
        public View getInfoContents(Marker marker) {
            myContentsView = inflater.inflate(
                    R.layout.cluster_info_window, null);
            TextView textView = ((TextView) myContentsView
                    .findViewById(R.id.cluster_info_window_text));

            if (clickedCluster != null) {

                char[] digits = Utils.getInstance().preferredDigits(SearchResultMapActivity.this);


                textView.setText(Utils.formatNumber(clickedCluster.getSize(), digits));
            }
            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }
    }

    // Custom adapter info view for cluster items
    public class MyCustomAdapterForItems implements GoogleMap.InfoWindowAdapter {

        private View myContentsView;
        private LayoutInflater inflater;

        MyCustomAdapterForItems(LayoutInflater inflater) {
            this.inflater = inflater;
        }

        @Override
        public View getInfoContents(Marker marker) {

            myContentsView = inflater.inflate(
                    R.layout.info_window, null);

            if (clickedClusterItem != null && clickedClusterItem.getAroundLocation() != null) {

                AroundLocation aroundLocation = clickedClusterItem.getAroundLocation();

                DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
                int imageWidth = (int) ((dpWidth - 80) * displayMetrics.density);
                int imageHeight = imageWidth * 38 / 62;

                if (aroundLocation.getType() != null && "City".equals(aroundLocation.getType()) &&
                        aroundLocation.getData() instanceof AroundPlaceCity) {
                    AroundPlaceCity city = (AroundPlaceCity) aroundLocation.getData();

                    myContentsView = inflater.inflate(
                            R.layout.map_city_info_window, null);


                    TextView titleTextView = ((TextView) myContentsView
                            .findViewById(R.id.map_city_info_window_title));
                    titleTextView.setText("\u200e" + city.getName());

                    if (city.getImages() != null && city.getImages().length > 0) {

                        ImageView cityImageView = (ImageView) myContentsView
                                .findViewById(R.id.map_city_info_window_image);
                        Picasso.with(getApplicationContext()).load(city.getImages()[0]).resize(imageWidth, imageHeight).into(cityImageView);
                    }

                    /*ViewPager viewPager = (ViewPager)myContentsView.findViewById(R.id.map_city_info_window_imageViewpager);

                    if (viewPager.getLayoutParams() != null) {

                        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
                        int viewPagerWidth = (int)((dpWidth - 64) * displayMetrics.density);
                        int viewPagerHeight = viewPagerWidth * 38 / 62;

//                        viewPager.getLayoutParams().width = viewPagerWidth;
//                        viewPager.getLayoutParams().height = viewPagerHeight;
                    }

                    ImageViewPagerAdapter adapter = new ImageViewPagerAdapter(SearchResultMapActivity.this, city.getImages());
                    viewPager.setAdapter(adapter);

                    CirclePageIndicator pageIndicator = (CirclePageIndicator)myContentsView.findViewById(R.id.map_city_info_window_imagePageIndicator);
                    pageIndicator.setViewPager(viewPager);*/

                } else if (aroundLocation.getType() != null && "Attraction".equals(aroundLocation.getType()) &&
                        aroundLocation.getData() instanceof AroundPlaceAttraction) {
                    AroundPlaceAttraction attraction = (AroundPlaceAttraction) aroundLocation.getData();

                    myContentsView = inflater.inflate(
                            R.layout.map_attraction_info_window, null);


                    TextView titleTextView = ((TextView) myContentsView
                            .findViewById(R.id.map_attraction_info_window_title));
                    titleTextView.setText("\u200e" + attraction.getName());

                    if (attraction.getImages() != null && attraction.getImages().length > 0) {

                        ImageView attractionImageView = (ImageView) myContentsView
                                .findViewById(R.id.map_attraction_info_window_image);
                        Picasso.with(getApplicationContext()).load(attraction.getImages()[0]).resize(imageWidth, imageHeight).into(attractionImageView);
                    }

                    /*ViewPager viewPager = (ViewPager)myContentsView.findViewById(R.id.map_attraction_info_window_imageViewpager);

                    if (viewPager.getLayoutParams() != null) {

                        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
                        int viewPagerWidth = (int)((dpWidth - 64) * displayMetrics.density);
                        int viewPagerHeight = viewPagerWidth * 38 / 62;

//                        viewPager.getLayoutParams().width = viewPagerWidth;
//                        viewPager.getLayoutParams().height = viewPagerHeight;
                    }

                    ImageViewPagerAdapter adapter = new ImageViewPagerAdapter(SearchResultMapActivity.this, attraction.getImages());
                    viewPager.setAdapter(adapter);

                    CirclePageIndicator pageIndicator = (CirclePageIndicator)myContentsView.findViewById(R.id.map_attraction_info_window_imagePageIndicator);
                    pageIndicator.setViewPager(viewPager);*/

                } else if (aroundLocation.getType() != null && "House".equals(aroundLocation.getType()) &&
                        aroundLocation.getData() instanceof AroundPlaceHouse) {
                    AroundPlaceHouse house = (AroundPlaceHouse) aroundLocation.getData();

                    myContentsView = inflater.inflate(
                            R.layout.map_house_info_window, null);


                    if (myContentsView.getLayoutParams() != null) {
                        myContentsView.getLayoutParams().width = imageWidth;
                        myContentsView.getLayoutParams().height = imageHeight;
                    }

                    TextView priceTextView = ((TextView) myContentsView
                            .findViewById(R.id.map_house_info_window_price));
                    priceTextView.setText(/*"\u200e" +*/ house.getCost());

                    TextView featureSummaryTextView = ((TextView) myContentsView
                            .findViewById(R.id.map_house_info_window_featureSummary));
                    featureSummaryTextView.setText("\u200e" + house.getFeatureSummray());

                    if (house.getImages() != null && house.getImages().length > 0) {

                        ImageView houseImageView = (ImageView) myContentsView
                                .findViewById(R.id.map_house_info_window_image);
                        Picasso.with(getApplicationContext()).load(house.getImages()[0]).resize(imageWidth, imageHeight).into(houseImageView);
                    }


                    RatingBar houseRatingBar = (RatingBar) myContentsView.findViewById(R.id.map_house_info_window_ratingBar);

                    Drawable drawable = houseRatingBar.getProgressDrawable();
                    drawable.setColorFilter(getResources().getColor(R.color.orange_light), PorterDuff.Mode.SRC_ATOP);
                }
            }
            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }
    }

    private class LoadDataAsyncTask extends AsyncTask {

        private String query;

        public LoadDataAsyncTask(String query) {
            this.query = query;
        }

        @Override
        protected void onPreExecute() {
            showProgress();
            loadingMore = true;
        }


        @Override
        protected Object doInBackground(Object[] objects) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(AroundLocation.class, new AroundLocationDeserializer())
                    .registerTypeAdapter(AroundPlaceCity.class, new AroundPlaceCityDeserializer())
                    .registerTypeAdapter(AroundPlaceAttraction.class, new AroundPlaceAttractionDeserializer())
                    .registerTypeAdapter(AroundPlaceHouse.class, new AroundPlaceHouseDeserializer())
                    .create();

            Retrofit retrofit = RetrofitService.getInstance(gson).getRetrofit();

            RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);
            Call<AroundLocation[]> call = apiEndpoints.getAroundLocations(query, "100", "0");

            call.enqueue(new Callback<AroundLocation[]>() {
                @Override
                public void onResponse(Call<AroundLocation[]> call, Response<AroundLocation[]> response) {
                    int statusCode = response.code();
                    AroundLocation[] aroundLocations = response.body();

                    if (aroundLocations != null && aroundLocations.length > 0) {
                        aroundLocationList = Arrays.asList(aroundLocations);
                        setupMarkers(aroundLocationList);

                        loadingMore = false;
                        hideProgress();
                    } else
                        Log.d("RetrofitService", "Around locations is empty");
                }

                @Override
                public void onFailure(Call<AroundLocation[]> call, Throwable t) {
                    // Log error here since request failed
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
//            loadingMore = false;
        }
    }

    public class ImageDownloaderAsyncTask extends AsyncTask {
        private Context context;
        private String imageUrl;
        private int width, height;

        public ImageDownloaderAsyncTask(Context context, String imageUrl, int width, int height) {
            this.context = context;
            this.imageUrl = imageUrl;
            this.width = width;
            this.height = height;
        }


        @Override
        protected Object doInBackground(Object[] objects) {
            return getImageBitmap(imageUrl);
        }

        private Bitmap getImageBitmap(String url) {

            Picasso picasso;
            try {

                picasso = Picasso.with(context);

                if (width > 0 && height > 0) {
                    return picasso.load(imageUrl).resize(width, height).centerCrop().get();
                } else {
                    return picasso.load(imageUrl).resize(80, 60).centerCrop().get();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
