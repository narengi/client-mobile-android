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
import xyz.narengi.android.ui.adapter.SuggestionsRecyclerAdapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
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
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

/**
 * @author Siavash Mahmoudpour
 */
public class SearchResultActivity extends ActionBarActivity {

    private AroundLocation[] aroundLocations;
    private List<AroundLocation> aroundLocationList;
    private RecyclerAdapter recyclerAdapter;
    private EditText searchEditText;
    private View.OnFocusChangeListener searchOnFocusChangeListener;
    private boolean loadingMore;
    private RecyclerView searchResultListView;
    private RecyclerView searchHistoryListView;
    private boolean isShowingSuggestions = false;
    private TextWatcher searchTextWatcher;
    private RecyclerView.OnItemTouchListener suggestionsOnItemTouchListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        String query = "";
        if (getIntent() != null && getIntent().getStringExtra("query") != null) {
            query = getIntent().getStringExtra("query");
        }

        setupToolbar(query);
        aroundLocationList = new ArrayList<AroundLocation>();
        setupListView(aroundLocationList);

        LoadDataAsyncTask loadDataAsyncTask = new LoadDataAsyncTask(query);
        loadDataAsyncTask.execute();
    }

    @Override
    public void onBackPressed() {
//        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.search_result_suggestionsRecyclerView);
//        if (recyclerView.getVisibility() == View.VISIBLE ) {
        if (isShowingSuggestions) {
            closeSearchSuggestions(searchEditText.getText().toString());
            return;
        } else {
            super.onBackPressed();
        }
        super.onBackPressed();
    }

    private void showProgress() {
        LinearLayout progressBarLayout = (LinearLayout)findViewById(R.id.search_result_progressLayout);
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.search_result_progressBar);

        progressBar.setVisibility(View.VISIBLE);
        progressBarLayout.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        LinearLayout progressBarLayout = (LinearLayout)findViewById(R.id.search_result_progressLayout);
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.search_result_progressBar);

        progressBar.setVisibility(View.GONE);
        progressBarLayout.setVisibility(View.GONE);
    }

    private void showSearchHistoryRecyclerView() {
        LinearLayout toolbarInnerLayout = (LinearLayout)findViewById(R.id.search_result_toolbar_main_layout);
        toolbarInnerLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                closeSearchSuggestions(searchEditText.getText().toString());
                return false;
            }
        });

        searchHistoryListView = (RecyclerView)findViewById(R.id.search_result_suggestionsRecyclerView);

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
                                setupListView(aroundLocationList);
                                showProgress();
                                LoadDataAsyncTask loadDataAsyncTask = new LoadDataAsyncTask(item.toString());
                                loadDataAsyncTask.execute();
//                                searchEditText.setText(item.toString());
//                                openSearchResult(item.toString());
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
            return (SuggestionsResult)object;

        return null;
    }

    private void showSearchResultRecyclerView(String query) {

        LinearLayout toolbarInnerLayout = (LinearLayout)findViewById(R.id.search_result_toolbar_main_layout);
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

        searchResultListView = (RecyclerView)findViewById(R.id.search_result_suggestionsRecyclerView);

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
            for (int i=0 ; i < count ; i++) {
                builder.append(tokenList.get(i));

                if (i < (count - 1))
                    builder.append(",");
            }
        }

        editor.putString("searchHistory", builder.toString());
        editor.commit();
    }

    private void setupToolbar(String query) {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.search_result_toolbar);

        if (toolbar != null) {
            final ImageButton settingsImageButton = (ImageButton)findViewById(R.id.search_result_toolbar_action_settings);
            final ImageButton mapImageButton = (ImageButton)findViewById(R.id.search_result_toolbar_action_map);
            searchEditText = (EditText)findViewById(R.id.search_result_toolbar_action_search);
            searchEditText.setHint(R.string.search_hint);
            searchEditText.setText(query);

            final Handler handler = new Handler();

            searchTextWatcher = new TextWatcher() {
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
                                showSearchHistoryRecyclerView();
                                updateToolbarScrollFlags(false);
                            }

                        }, 5);
                    } else {

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showSearchResultRecyclerView(searchEditText.getText().toString());
                                updateToolbarScrollFlags(false);
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
                                    showSearchHistoryRecyclerView();
                                    updateToolbarScrollFlags(false);
                                }

                            });
                        } else {
                            handler.postAtFrontOfQueue(new Runnable() {
                                @Override
                                public void run() {
                                    showSearchResultRecyclerView(searchEditText.getText().toString());
                                    updateToolbarScrollFlags(false);
                                }
                            });
                        }
                        view.requestFocus();

                        settingsImageButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_back));
                        mapImageButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_close_clear_cancel));
                    } else {
                        mapImageButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_dialog_map));
                    }
                }
            };

            searchEditText.setOnFocusChangeListener(searchOnFocusChangeListener);

            searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        if (searchEditText.getText().toString().length() > 0) {
                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(searchEditText.getWindowToken(), InputMethodManager.SHOW_FORCED);
                            storeSearchQuery(searchEditText.getText().toString());
                            closeSearchSuggestions(searchEditText.getText().toString());
                            aroundLocationList = new ArrayList<AroundLocation>();
                            setupListView(aroundLocationList);
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.search_result_toolbar);
        if (toolbar == null)
            return;

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.search_result_collapsing_toolbar);
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) collapsingToolbar.getLayoutParams();

        if (canScroll) {
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                    | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
        } else {
            params.setScrollFlags(0);
        }

        collapsingToolbar.setLayoutParams(params);
    }

    private void searchMapButtonOnClick() {
        final boolean hasText = !TextUtils.isEmpty(searchEditText.getText().toString());

        if (searchEditText.hasFocus()) {
            searchEditText.setText("");
        } else {
            Toast.makeText(SearchResultActivity.this, "Map icon clicked!", Toast.LENGTH_LONG).show();
        }

//        if (hasText) {
//            searchEditText.setText("");
//        } else {
//            if (!searchEditText.hasFocus()) {
//                Toast.makeText(SearchResultActivity.this, "Map icon clicked!", Toast.LENGTH_LONG).show();
//            }
//        }
    }

    private void searchMenuButtonOnClick() {

//        final ImageButton settingsImageButton = (ImageButton)findViewById(R.id.search_result_toolbar_action_settings);
//        final ImageButton mapImageButton = (ImageButton)findViewById(R.id.search_result_toolbar_action_map);
//
//        final boolean hasText = !TextUtils.isEmpty(searchEditText.getText().toString());
//        RecyclerView listView = (RecyclerView)findViewById(R.id.search_result_suggestionsRecyclerView);
//        if (hasText || searchEditText.hasFocus() || listView.getVisibility() == View.VISIBLE) {
//            searchEditText.setOnFocusChangeListener(null);
//            searchEditText.removeTextChangedListener(searchTextWatcher);
//            searchEditText.setText("");
//            searchEditText.clearFocus();
//            settingsImageButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_navigation_menu));
//            mapImageButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_mylocation));
//
//            listView.setVisibility(View.INVISIBLE);
//            listView.invalidate();
//
//            searchEditText.setOnFocusChangeListener(searchOnFocusChangeListener);
//            searchEditText.addTextChangedListener(searchTextWatcher);
//            updateToolbarScrollFlags(true);
//        }
//
//        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputMethodManager.hideSoftInputFromWindow(searchEditText.getWindowToken(), InputMethodManager.SHOW_FORCED);

        isShowingSuggestions = false;
        onBackPressed();
    }

    private void closeSearchSuggestions(String newQuery) {
//        final ImageButton settingsImageButton = (ImageButton)findViewById(R.id.search_result_toolbar_action_settings);
        final ImageButton mapImageButton = (ImageButton)findViewById(R.id.search_result_toolbar_action_map);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.search_result_suggestionsRecyclerView);
        searchEditText.setOnFocusChangeListener(null);
        searchEditText.removeTextChangedListener(searchTextWatcher);
        if (newQuery != null)
            searchEditText.setText(newQuery);
        else
            searchEditText.setText("");
        searchEditText.clearFocus();
//        settingsImageButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_navigation_menu));
        mapImageButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_dialog_map));

        recyclerView.setVisibility(View.INVISIBLE);
        recyclerView.invalidate();

        searchEditText.setOnFocusChangeListener(searchOnFocusChangeListener);
        searchEditText.addTextChangedListener(searchTextWatcher);

        updateToolbarScrollFlags(true);
        isShowingSuggestions = false;
    }

    private void setupListView(List<AroundLocation> aroundLocations) {

        RecyclerView mRecyclerView = (RecyclerView)findViewById(R.id.search_result_recyclerView);

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

                closeSearchSuggestions(searchEditText.getText().toString());

                View childView = rv.findChildViewUnder(e.getX(), e.getY());
                if (childView != null && mGestureDetector.onTouchEvent(e)) {

                    int position = rv.getChildAdapterPosition(childView);
                    if (aroundLocationList != null && aroundLocationList.size() > position) {
                        AroundLocation aroundLocation = aroundLocationList.get(position);
                        if (aroundLocation.getType() != null && "City".equals(aroundLocation.getType()) &&
                                aroundLocation.getData() instanceof AroundPlaceCity) {
                            openCityDetail((AroundPlaceCity) aroundLocation.getData());
                            return true;
                        } else if (aroundLocation.getType() != null && "Attraction".equals(aroundLocation.getType()) &&
                                aroundLocation.getData() instanceof AroundPlaceAttraction) {
                            openAttractionDetail((AroundPlaceAttraction) aroundLocation.getData());
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
        closeSearchSuggestions("");
        startActivity(intent);
    }

    private void openAttractionDetail(AroundPlaceAttraction attraction) {
        String attractionUrl = attraction.getURL();
        Intent intent = new Intent(this, AttractionActivity.class);
        intent.putExtra("attractionUrl", attractionUrl);
        closeSearchSuggestions("");
        startActivity(intent);
    }

    private void openHouseDetail(AroundPlaceHouse house) {
        String houseUrl = house.getURL();
        Intent intent = new Intent(this, HouseActivity.class);
        intent.putExtra("houseUrl", houseUrl);
        closeSearchSuggestions("");
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