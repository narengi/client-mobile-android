package xyz.narengi.android.ui.activity;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.narengi.android.R;
import xyz.narengi.android.common.Constants;
import xyz.narengi.android.common.dto.AroundLocation;
import xyz.narengi.android.common.dto.AroundPlaceAttraction;
import xyz.narengi.android.common.dto.AroundPlaceCity;
import xyz.narengi.android.common.dto.AroundPlaceHouse;
import xyz.narengi.android.common.dto.SuggestionsResult;
import xyz.narengi.android.content.AroundLocationDeserializer;
import xyz.narengi.android.content.AroundPlaceAttractionDeserializer;
import xyz.narengi.android.content.AroundPlaceCityDeserializer;
import xyz.narengi.android.content.AroundPlaceHouseDeserializer;
import xyz.narengi.android.service.RetrofitApiEndpoints;
import xyz.narengi.android.service.SuggestionsServiceAsyncTask;
import xyz.narengi.android.ui.adapter.RecyclerAdapter;
import xyz.narengi.android.ui.adapter.SuggestionsExpandableListAdapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author Siavash Mahmoudpour
 */
public class ExploreActivity extends ActionBarActivity {

    private AroundLocation[] aroundLocations;
    private List<AroundLocation> aroundLocationList;
    private RecyclerAdapter recyclerAdapter;
    private DrawerLayout drawerLayout;
    private EditText searchEditText;
    private View.OnFocusChangeListener searchOnFocusChangeListener;
    private boolean loadingMore;
    private ExpandableListView searchResultListView;
    private ExpandableListView searchHistoryListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        setupToolbar();
        aroundLocationList = new ArrayList<AroundLocation>();
        setupListView(aroundLocationList);

        LoadDataAsyncTask loadDataAsyncTask = new LoadDataAsyncTask("");
        loadDataAsyncTask.execute();
    }

    private void showProgress() {
        LinearLayout progressBarLayout = (LinearLayout)findViewById(R.id.explore_progressLayout);
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.explore_progressBar);

        progressBar.setVisibility(View.VISIBLE);
        progressBarLayout.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        LinearLayout progressBarLayout = (LinearLayout)findViewById(R.id.explore_progressLayout);
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.explore_progressBar);

        progressBar.setVisibility(View.GONE);
        progressBarLayout.setVisibility(View.GONE);
    }

    private void showSearchHistoryListView() {

        searchHistoryListView = (ExpandableListView)findViewById(R.id.search_results_list);

        final String[] historyItems = getResources().getStringArray(R.array.search_history_array);

        ArrayList<String> headerItemsList = new ArrayList<String>();
        headerItemsList.add("");

        HashMap<String, List<Object>> childItemsHashMap = new HashMap<String, List<Object>>();
        List historyItemsList = Arrays.asList(historyItems);

        childItemsHashMap.put(headerItemsList.get(0), historyItemsList);

        SuggestionsExpandableListAdapter listAdapter = new SuggestionsExpandableListAdapter(this,
                headerItemsList, childItemsHashMap);

        searchHistoryListView.setAdapter(listAdapter);
        searchHistoryListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);


        // Define the on-click listener for the list items
        searchHistoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (historyItems.length > position) {
                    String historyItem = historyItems[position];
                    searchEditText.setText(historyItem);
                }
            }
        });

        searchHistoryListView.setVisibility(View.VISIBLE);
        searchHistoryListView.invalidate();
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
            return (SuggestionsResult)object;

        return null;
    }

    private void showSearchResultListView(String query) {

        final String[] searchHeaderItems = getResources().getStringArray(R.array.search_suggestion_titles_array);

        List<String> headerItemsList = new ArrayList<String>();

        HashMap<String, List<Object>> childItemsHashMap = new HashMap<String, List<Object>>();

        SuggestionsResult suggestionsResult = getAroundLocationSuggestions(query);
        if (suggestionsResult != null) {

            if (suggestionsResult.getCity() != null && suggestionsResult.getCity().length > 0) {
                headerItemsList.add(searchHeaderItems[0]);
                List cities = Arrays.asList(suggestionsResult.getCity());
                childItemsHashMap.put(searchHeaderItems[0], cities);
            }
            if (suggestionsResult.getAttraction() != null && suggestionsResult.getAttraction().length > 0) {
                headerItemsList.add(searchHeaderItems[1]);
                List attractions = Arrays.asList(suggestionsResult.getAttraction());
                childItemsHashMap.put(searchHeaderItems[1], attractions);
            }
            if (suggestionsResult.getHouse() != null && suggestionsResult.getHouse().length > 0) {
                headerItemsList.add(searchHeaderItems[2]);
                List houses = Arrays.asList(suggestionsResult.getHouse());
                childItemsHashMap.put(searchHeaderItems[2], houses);
            }
        }

        searchResultListView = (ExpandableListView)findViewById(R.id.search_results_list);

        SuggestionsExpandableListAdapter listAdapter = new SuggestionsExpandableListAdapter(this,
                headerItemsList, childItemsHashMap);

        searchResultListView.setAdapter(listAdapter);

        searchResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Build the Intent used to open WordActivity with a specific word Uri
//                    Intent detailIntent = new Intent(getApplicationContext(), DetailActivity.class);
//                    Uri data = Uri.withAppendedPath(SearchSuggestionProvider.CONTENT_URI,
//                            String.valueOf(id));
//                    detailIntent.setData(data);
//                    startActivity(detailIntent);
            }
        });

        searchResultListView.setVisibility(View.VISIBLE);
        searchResultListView.invalidate();
    }

    private void setupToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);

        if (toolbar != null) {
            final ImageButton settingsImageButton = (ImageButton)findViewById(R.id.toolbar_action_settings);
            final ImageButton mapImageButton = (ImageButton)findViewById(R.id.toolbar_action_map);
            searchEditText = (EditText)findViewById(R.id.toolbar_action_search);
            searchEditText.setHint(R.string.search_hint);

            final Handler handler = new Handler();

            final TextWatcher searchTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    final String s = editable.toString();
                    if (s.length() == 0) {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showSearchHistoryListView();
                            }

                        }, 5);
                    } else {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showSearchResultListView(searchEditText.getText().toString());
                            }
                        }, 5);
                    }
                }
            };

            searchEditText.addTextChangedListener(searchTextWatcher);

            searchOnFocusChangeListener = new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        if (searchEditText.getText().toString().length() == 0) {
                            handler.postAtFrontOfQueue(new Runnable() {
                                @Override
                                public void run() {
                                    showSearchHistoryListView();
                                }

                            });
                        } else {
                            handler.postAtFrontOfQueue(new Runnable() {
                                @Override
                                public void run() {
                                    showSearchResultListView(searchEditText.getText().toString());
                                }
                            });
                        }
                        view.requestFocus();

                        settingsImageButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_back));
                        mapImageButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_close_clear_cancel));
                    }
                }
            };

            searchEditText.setOnFocusChangeListener(searchOnFocusChangeListener);

            settingsImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final boolean hasText = !TextUtils.isEmpty(searchEditText.getText().toString());
                    ExpandableListView listView = (ExpandableListView)findViewById(R.id.search_results_list);
                    if (hasText || searchEditText.hasFocus() || listView.getVisibility() == View.VISIBLE) {
                        searchEditText.setOnFocusChangeListener(null);
                        searchEditText.removeTextChangedListener(searchTextWatcher);
                        searchEditText.setText("");
//                        searchEditText.dismissDropDown();
                        searchEditText.clearFocus();
                        settingsImageButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_navigation_menu));
                        mapImageButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_location_on));



                        listView.setVisibility(View.INVISIBLE);
                        listView.invalidate();

                        searchEditText.setOnFocusChangeListener(searchOnFocusChangeListener);
                        searchEditText.addTextChangedListener(searchTextWatcher);
                    } else {
                        if (drawerLayout != null) {
                            if (drawerLayout.isDrawerOpen(GravityCompat.END))
                                drawerLayout.closeDrawer(GravityCompat.END);
                            else
                                drawerLayout.openDrawer(GravityCompat.END);
                        }
                    }

                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(searchEditText.getWindowToken(), InputMethodManager.SHOW_FORCED);
                }
            });

            mapImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final boolean hasText = !TextUtils.isEmpty(searchEditText.getText().toString());
                    if (hasText) {
                        searchEditText.setText("");
                    } else {
                        if (!searchEditText.hasFocus()) {
                            Toast.makeText(ExploreActivity.this, "Map icon clicked!", Toast.LENGTH_LONG).show();
                        }
                    }
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

    private void setupListView(List<AroundLocation> aroundLocations) {

        RecyclerView mRecyclerView = (RecyclerView)findViewById(R.id.scrollRecyclerView);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        recyclerAdapter = new RecyclerAdapter(aroundLocations, this);
        mRecyclerView.setAdapter(recyclerAdapter);

        final GestureDetector mGestureDetector;
        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View childView = rv.findChildViewUnder(e.getX(), e.getY());
                if (childView != null && mGestureDetector.onTouchEvent(e)) {

                    int position = rv.getChildAdapterPosition(childView);
                    if (aroundLocationList != null && aroundLocationList.size() > position) {
                        AroundLocation aroundLocation = aroundLocationList.get(position);
                        if (aroundLocation.getType() != null && "City".equals(aroundLocation.getType()) &&
                                aroundLocation.getData() instanceof AroundPlaceCity) {
                            openCityDetail((AroundPlaceCity) aroundLocation.getData());
                            return true;
                        } else if (aroundLocation.getType() != null && "House".equals(aroundLocation.getType()) &&
                                aroundLocation.getData() instanceof AroundPlaceHouse) {
                            openHouseDetail((AroundPlaceHouse) aroundLocation.getData());
                            return true;
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
        });

    }

    private void openCityDetail(AroundPlaceCity city) {
        String cityUrl = city.getURL();
        Intent intent = new Intent(this, CityActivity.class);
        intent.putExtra("cityUrl", cityUrl);
        startActivity(intent);
    }

    private void openHouseDetail(AroundPlaceHouse house) {
        String houseUrl = house.getURL();
        Intent intent = new Intent(this, HouseActivity.class);
        intent.putExtra("houseUrl", houseUrl);
        intent.putExtra("house", house);
        startActivity(intent);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
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

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.SERVER_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);
            Call<AroundLocation[]> call = apiEndpoints.getAroundLocations(query, "30", "0");

            call.enqueue(new Callback<AroundLocation[]>() {
                @Override
                public void onResponse(Response<AroundLocation[]> response, Retrofit retrofit) {
                    int statusCode = response.code();
                    AroundLocation[] aroundLocations = response.body();

                    if (aroundLocations != null && aroundLocations.length > 0) {
                        aroundLocationList = Arrays.asList(aroundLocations);
                        setupListView(aroundLocationList);
//                        recyclerAdapter.notifyDataSetChanged();

                        loadingMore = false;
                        hideProgress();
                    } else
                        Log.d("RetrofitService", "Around locations is empty");
                }

                @Override
                public void onFailure(Throwable t) {
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
}