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
import xyz.narengi.android.common.dto.Authorization;
import xyz.narengi.android.common.dto.SuggestionsResult;
import xyz.narengi.android.content.AroundLocationDeserializer;
import xyz.narengi.android.content.AroundPlaceAttractionDeserializer;
import xyz.narengi.android.content.AroundPlaceCityDeserializer;
import xyz.narengi.android.content.AroundPlaceHouseDeserializer;
import xyz.narengi.android.content.RoundedTransformation;
import xyz.narengi.android.service.ImageDownloaderAsyncTask;
import xyz.narengi.android.service.RetrofitApiEndpoints;
import xyz.narengi.android.service.SuggestionsServiceAsyncTask;
import xyz.narengi.android.ui.adapter.RecyclerAdapter;
import xyz.narengi.android.ui.adapter.SuggestionsExpandableListAdapter;
import xyz.narengi.android.ui.adapter.SuggestionsRecyclerAdapter;
import xyz.narengi.android.util.SecurityUtils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MenuItem;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
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
    //    private ExpandableListView searchResultListView;
//    private ExpandableListView searchHistoryListView;
    private RecyclerView searchResultListView;
    private RecyclerView searchHistoryListView;
    private TextWatcher searchTextWatcher;
    private RecyclerView.OnItemTouchListener suggestionsOnItemTouchListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        setupToolbar();

        final SharedPreferences preferences = getSharedPreferences("profile", 0);
        String accessToken = preferences.getString("accessToken", "");
        String username = preferences.getString("username", "");
        if (accessToken.length() > 0 && username.length() > 0) {
            System.err.println("\n\n\naccessToken : " + accessToken);
            System.err.println("\n\nusername : " + username + "\n\n");
            setupNavigationView(true);
        } else {
            setupNavigationView(false);
        }

        aroundLocationList = new ArrayList<AroundLocation>();
        setupListView(aroundLocationList);

        LoadDataAsyncTask loadDataAsyncTask = new LoadDataAsyncTask("");
        loadDataAsyncTask.execute();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
//        if (SecurityUtils.getInstance(this).isUpdateUserTitleNeeded()) {
//            NavigationView navigationView = (NavigationView)findViewById(R.id.explore_navigationView);
//
//            View headerView = navigationView.getHeaderView(0);
//            TextView titleTextView = (TextView)headerView.findViewById(R.id.drawer_header_title);
//            SharedPreferences preferences = getSharedPreferences("profile", 0);
//            String title = preferences.getString("displayName", "");
//            if (title.length() > 0) {
//                titleTextView.setText(title);
//            }
//            SecurityUtils.getInstance(this).setUpdateUserTitleNeeded(false);
//        }
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout != null) {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END);
                return;
            } else {
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.search_results_list);
                if (recyclerView.getVisibility() == View.VISIBLE) {
                    closeSearchSuggestions(false);
                    return;
                } else {
                    super.onBackPressed();
                }
            }
        }

        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 301) {
            if (resultCode == 302) {
                //user registered, update menu and open sign up confirm
                setupNavigationView(true);
                openSignUpConfirm();
            } else if (resultCode == 303) {
                //user logged in, update menu
                setupNavigationView(true);
            }
        }

        if (resultCode == 401) {
            setupNavigationView(true);
            Intent intent = new Intent(this, ViewProfileActivity.class);
            startActivity(intent);
        }
    }

    private void openSignUpConfirm() {
        Intent intent = new Intent(this, SignUpConfirmActivity.class);
        startActivityForResult(intent, 101);
    }

    private void setupNavigationView(boolean loggedIn) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.explore_navigationView);
        View oldHeaderView = navigationView.getHeaderView(0);
        if (oldHeaderView != null)
            navigationView.removeHeaderView(oldHeaderView);

        if (navigationView.getMenu() != null)
            navigationView.getMenu().clear();

        if (loggedIn) {

            navigationView.inflateMenu(R.menu.drawer_logged_in);
            navigationView.inflateHeaderView(R.layout.drawer_header_logged_in);//TODO : change this header layout


            View headerView = navigationView.getHeaderView(0);

//            ViewGroup.LayoutParams params = headerView.getLayoutParams();
//            if (params != null && navigationView.getLayoutParams() != null)
//                params.height = navigationView.getLayoutParams().width * 9 / 16;

            TextView titleTextView = (TextView) headerView.findViewById(R.id.drawer_header_userTitle);
            SharedPreferences preferences = getSharedPreferences("profile", 0);
            String title = preferences.getString("displayName", "");
            if (title.length() > 0) {
                titleTextView.setText(title);
            }

            final ImageView userImageView = (ImageView) headerView.findViewById(R.id.drawer_header_userPicture);
            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    getProfilePicture(userImageView);
                }
            });

            headerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openViewProfile();
                }
            });

            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {

                    //Checking if the item is in checked state or not, if not make it in checked state
                    if (menuItem.isChecked())
                        menuItem.setChecked(false);
                    else
                        menuItem.setChecked(true);

                    //Closing drawer on item click
                    drawerLayout.closeDrawers();

                    switch (menuItem.getItemId()) {

                        case R.id.navigation_item_logout:
                            logout();
                            break;
                        default:
                            break;
                    }
                    return false;
                }
            });

        } else {
            navigationView.inflateMenu(R.menu.drawer);
            navigationView.inflateHeaderView(R.layout.drawer_header);


            View headerView = navigationView.getHeaderView(0);

//            ViewGroup.LayoutParams params = headerView.getLayoutParams();
//            if (params != null && navigationView.getLayoutParams() != null)
//                params.height = navigationView.getLayoutParams().width * 9 / 16;

            TextView titleTextView = (TextView) headerView.findViewById(R.id.drawer_header_welcomeMessage);
            titleTextView.setText(getString(R.string.drawer_welcome_message));

            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {

                    //Checking if the item is in checked state or not, if not make it in checked state
                    if (menuItem.isChecked())
                        menuItem.setChecked(false);
                    else
                        menuItem.setChecked(true);

                    //Closing drawer on item click
                    drawerLayout.closeDrawers();

                    switch (menuItem.getItemId()) {

                        case R.id.navigation_item_login_register:
                            openSignInSignUp();
                            break;
                        default:
                            break;
                    }
                    return false;
                }
            });

        }
    }

    private void getProfilePicture(ImageView profileImageView) {

        final SharedPreferences preferences = getSharedPreferences("profile", 0);
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

        Bitmap bitmap;

        String imageUrl = Constants.SERVER_BASE_URL + "/api/v1/user-profiles/picture";

        ImageDownloaderAsyncTask imageDownloaderAsyncTask = new ImageDownloaderAsyncTask(this, authorizationJson, imageUrl);
        AsyncTask asyncTask = imageDownloaderAsyncTask.execute();

        try {
            bitmap = (Bitmap) asyncTask.get();

            if (bitmap != null) {
                Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

                BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                Paint paint = new Paint();
                paint.setShader(shader);
                paint.setAntiAlias(true);
                Canvas c = new Canvas(circleBitmap);
                c.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);

                profileImageView.setImageBitmap(bitmap);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void logout() {

        SharedPreferences preferences = getSharedPreferences("profile", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();

        setupNavigationView(false);
    }

    private void openViewProfile() {
        Intent intent = new Intent(this, ViewProfileActivity.class);
        startActivityForResult(intent, 104);
    }

    private void setupNavigationView() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.explore_navigationView);

        View headerView = navigationView.getHeaderView(0);
        TextView titleTextView = (TextView) headerView.findViewById(R.id.drawer_header_userTitle);
        SharedPreferences preferences = getSharedPreferences("profile", 0);
        String title = preferences.getString("displayName", "");
        if (title.length() > 0) {
            titleTextView.setText(title);
        }

        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ExploreActivity.this, "Profile clicked", Toast.LENGTH_LONG).show();
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked())
                    menuItem.setChecked(false);
                else
                    menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                switch (menuItem.getItemId()) {

                    case R.id.navigation_item_login_register:
                        openSignInSignUp();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    private void openSignInSignUp() {
        Intent intent = new Intent(this, SignInSignUpActivity.class);
        startActivityForResult(intent, 301);
    }

    private void showProgress() {
        LinearLayout progressBarLayout = (LinearLayout) findViewById(R.id.explore_progressLayout);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.explore_progressBar);

        progressBar.setVisibility(View.VISIBLE);
        progressBarLayout.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        LinearLayout progressBarLayout = (LinearLayout) findViewById(R.id.explore_progressLayout);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.explore_progressBar);

        progressBar.setVisibility(View.GONE);
        progressBarLayout.setVisibility(View.GONE);
    }

    private void showSearchHistoryRecyclerView() {
        LinearLayout toolbarInnerLayout = (LinearLayout) findViewById(R.id.toolbar_main_layout);
        toolbarInnerLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                closeSearchSuggestions(false);
                return false;
            }
        });

        searchHistoryListView = (RecyclerView) findViewById(R.id.search_results_list);

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

//        final String[] historyItems = getResources().getStringArray(R.array.search_history_array);

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

//            @Override
//            public boolean onDown(MotionEvent e) {
//                return true;
//            }
//
//            @Override
//            public boolean onDoubleTap(MotionEvent e) {
//                return true;
//            }
//
//            @Override
//            public boolean onDoubleTapEvent(MotionEvent e) {
//                return true;
//            }
//
//            @Override
//            public boolean onSingleTapConfirmed(MotionEvent e) {
//                return true;
//            }
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
                    closeSearchSuggestions(false);
                } else {
                    if (position >= 0 && mGestureDetector.onTouchEvent(e)) {
                        if (historyItems.length > position) {
                            String item = historyItems[position];
                            if (item != null && item.length() > 0) {
//                                searchEditText.setText(item.toString());
                                openSearchResult(item);
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
    }

    /*private void showSearchHistoryListView() {

        searchHistoryListView = (ExpandableListView)findViewById(R.id.search_results_list);

        final String[] historyItems = getResources().getStringArray(R.array.search_history_array);

        final ArrayList<String> headerItemsList = new ArrayList<String>();
        headerItemsList.add("");

        final HashMap<String, List<Object>> childItemsHashMap = new HashMap<String, List<Object>>();
        List historyItemsList = Arrays.asList(historyItems);

        childItemsHashMap.put(headerItemsList.get(0), historyItemsList);

        SuggestionsExpandableListAdapter listAdapter = new SuggestionsExpandableListAdapter(this,
                headerItemsList, childItemsHashMap);

        searchHistoryListView.setAdapter(listAdapter);

        searchHistoryListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long id) {

                if (headerItemsList.size() > groupPosition) {
                    String headerItem = headerItemsList.get(groupPosition);
                    List childList = childItemsHashMap.get(headerItem);
                    if (childList != null && childList.size() > childPosition) {
                        Object child = childList.get(childPosition);
                        if (child != null && child instanceof String) {
                            searchEditText.setText(child.toString());
                        }
                    }
                }

                return false;
            }
        });

//        searchHistoryListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);


        // Define the on-click listener for the list items
//        searchHistoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (historyItems.length > position) {
//                    String historyItem = historyItems[position];
//                    searchEditText.setText(historyItem);
//                }
//            }
//        });

        searchHistoryListView.setVisibility(View.VISIBLE);
        searchHistoryListView.invalidate();
    }*/

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

        LinearLayout toolbarInnerLayout = (LinearLayout) findViewById(R.id.toolbar_main_layout);
        toolbarInnerLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                closeSearchSuggestions(false);
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

        searchResultListView = (RecyclerView) findViewById(R.id.search_results_list);

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
                    closeSearchSuggestions(false);
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

        /*ViewGroup vg = searchResultListView;
        int totalHeight = 0;
        for (int i = 0; i < searchResultListView.getAdapter().getCount(); i++) {
            View listItem = searchResultListView.getAdapter().getView(i, null, vg);
            if (listItem != null) {
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
        }

        ViewGroup.LayoutParams par = searchResultListView.getLayoutParams();
        par.height = totalHeight + (searchResultListView.getDividerHeight() * (searchResultListView.getAdapter().getCount() - 1));
        searchResultListView.setLayoutParams(par);
        searchResultListView.requestLayout();*/
    }

    /*private void showSearchResultListView(String query) {

        final String[] searchHeaderItems = getResources().getStringArray(R.array.search_suggestion_titles_array);

        final List<String> headerItemsList = new ArrayList<String>();

        final HashMap<String, List<Object>> childItemsHashMap = new HashMap<String, List<Object>>();

        SuggestionsResult suggestionsResult = getAroundLocationSuggestions(query);
        if (suggestionsResult != null) {

            if (suggestionsResult.getCity() != null && suggestionsResult.getCity().length > 0) {
                headerItemsList.add(searchHeaderItems[0]);
                List cities = Arrays.asList(suggestionsResult.getCity());
                childItemsHashMap.put(searchHeaderItems[0], cities);
            }
            if (suggestionsResult.getAttractions() != null && suggestionsResult.getAttractions().length > 0) {
                headerItemsList.add(searchHeaderItems[1]);
                List attractions = Arrays.asList(suggestionsResult.getAttractions());
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
//        searchResultListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        searchResultListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long id) {

                if (headerItemsList.size() > groupPosition) {
                    String headerItem = headerItemsList.get(groupPosition);
                    List childList = childItemsHashMap.get(headerItem);
                    if (childList != null && childList.size() > childPosition) {
                        Object child = childList.get(childPosition);
                        if (child instanceof AroundPlaceCity) {
                            openCityDetail((AroundPlaceCity) child);
                        } else if (child instanceof AroundPlaceAttraction) {

                        } else if (child instanceof AroundPlaceHouse) {
                            openHouseDetail((AroundPlaceHouse) child);
                        }
                    }
                }

                return false;
            }
        });

        searchResultListView.setVisibility(View.VISIBLE);
        searchResultListView.invalidate();

        ViewGroup vg = searchResultListView;
        int totalHeight = 0;
        for (int i = 0; i < searchResultListView.getAdapter().getCount(); i++) {
            View listItem = searchResultListView.getAdapter().getView(i, null, vg);
            if (listItem != null) {
//                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
        }

        ViewGroup.LayoutParams par = searchResultListView.getLayoutParams();
        par.height = totalHeight + (searchResultListView.getDividerHeight() * (searchResultListView.getAdapter().getCount() - 1));
        searchResultListView.setLayoutParams(par);
        searchResultListView.requestLayout();
    }*/

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

    private void setupToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);

        if (toolbar != null) {
            final ImageButton settingsImageButton = (ImageButton) findViewById(R.id.toolbar_action_settings);
            final ImageButton mapImageButton = (ImageButton) findViewById(R.id.toolbar_action_map);
            searchEditText = (EditText) findViewById(R.id.toolbar_action_search);
            searchEditText.setHint(R.string.search_hint);

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

//                        searchEditText.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                searchEditText.setSelection(1);
//                                searchEditText.extendSelection(0);
//                                searchEditText.setSelection(0);
////                                searchEditText.setSelection(s.length(), s.length());
//                            }
//                        }, 6);


//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                searchEditText.setSelection(s.length(), s.length());
//                            }
//                        }, 5);
//                        searchEditText.setSelection(0);
//                        searchEditText.moveCursorToVisibleOffset();
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
                    }
                }
            };

            searchEditText.setOnFocusChangeListener(searchOnFocusChangeListener);

            searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                        if (searchEditText.getText().toString().length() > 0) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(searchEditText.getWindowToken(), InputMethodManager.SHOW_FORCED);
                        storeSearchQuery(searchEditText.getText().toString());
                        openSearchResult(searchEditText.getText().toString());
//                        }
                        return true;
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

    private void openSearchResult(String query) {
        if (query != null && query.length() > 0) {
            Intent intent = new Intent(this, SearchResultActivity.class);
            intent.putExtra("query", query);
            startActivity(intent);
        }
    }

    private void updateToolbarScrollFlags(boolean canScroll) {

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        if (toolbar == null)
            return;

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.explore_collapsing_toolbar);
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) collapsingToolbar.getLayoutParams();

        if (canScroll) {
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                    | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
        } else {
            params.setScrollFlags(0);
        }

        collapsingToolbar.setLayoutParams(params);

//        AppBarLayout.LayoutParams params =
//                (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
//
//        if (canScroll) {
//            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
//                    | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
//        } else {
//            params.setScrollFlags(0);
//        }
//
//        toolbar.setLayoutParams(params);
    }

    private void searchMapButtonOnClick() {
        final boolean hasText = !TextUtils.isEmpty(searchEditText.getText().toString());
        if (hasText) {
            searchEditText.setText("");
        } else {
            if (!searchEditText.hasFocus()) {
                Toast.makeText(ExploreActivity.this, "Map icon clicked!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void searchMenuButtonOnClick() {

        final ImageButton settingsImageButton = (ImageButton) findViewById(R.id.toolbar_action_settings);
        final ImageButton mapImageButton = (ImageButton) findViewById(R.id.toolbar_action_map);

        final boolean hasText = !TextUtils.isEmpty(searchEditText.getText().toString());
        RecyclerView listView = (RecyclerView) findViewById(R.id.search_results_list);
        if (hasText || searchEditText.hasFocus() ||
                (listView.getAdapter() != null && listView.getLayoutManager() != null && listView.getLayoutParams() != null &&
                listView.getLayoutParams().height > 0)) {
            searchEditText.setOnFocusChangeListener(null);
            searchEditText.removeTextChangedListener(searchTextWatcher);
            searchEditText.setText("");
            searchEditText.clearFocus();
            settingsImageButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_navigation_menu));
            mapImageButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_mylocation));

            listView.setVisibility(View.INVISIBLE);
            listView.invalidate();

            searchEditText.setOnFocusChangeListener(searchOnFocusChangeListener);
            searchEditText.addTextChangedListener(searchTextWatcher);
            updateToolbarScrollFlags(true);
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

    private void closeSearchSuggestions(boolean shouldClearText) {
        final ImageButton settingsImageButton = (ImageButton) findViewById(R.id.toolbar_action_settings);
        final ImageButton mapImageButton = (ImageButton) findViewById(R.id.toolbar_action_map);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.search_results_list);
        if (shouldClearText) {
            searchEditText.setOnFocusChangeListener(null);
            searchEditText.removeTextChangedListener(searchTextWatcher);
            searchEditText.setText("");
            searchEditText.clearFocus();
            searchEditText.setOnFocusChangeListener(searchOnFocusChangeListener);
            searchEditText.addTextChangedListener(searchTextWatcher);
        } else {
            searchEditText.clearFocus();
        }

        settingsImageButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_navigation_menu));
        mapImageButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_mylocation));

        recyclerView.setVisibility(View.INVISIBLE);
        recyclerView.invalidate();


        updateToolbarScrollFlags(true);
    }

    private void setupListView(List<AroundLocation> aroundLocations) {

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.scrollRecyclerView);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
//        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
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

//        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                closeSearchSuggestions();
//                return true;
//            }
//        });

        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

                closeSearchSuggestions(false);

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
        closeSearchSuggestions(true);
        startActivity(intent);
    }

    private void openAttractionDetail(AroundPlaceAttraction attraction) {
        String attractionUrl = attraction.getURL();
        Intent intent = new Intent(this, AttractionActivity.class);
        intent.putExtra("attractionUrl", attractionUrl);
        closeSearchSuggestions(true);
        startActivity(intent);
    }

    private void openHouseDetail(AroundPlaceHouse house) {
        String houseUrl = house.getURL();
        Intent intent = new Intent(this, HouseActivity.class);
        intent.putExtra("houseUrl", houseUrl);
        intent.putExtra("house", house);
        closeSearchSuggestions(true);
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