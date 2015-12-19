package xyz.narengi.android.ui.activity;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.AroundLocation;
import xyz.narengi.android.common.dto.AroundPlaceAttraction;
import xyz.narengi.android.common.dto.AroundPlaceCity;
import xyz.narengi.android.common.dto.AroundPlaceHouse;
import xyz.narengi.android.content.AroundLocationDeserializer;
import xyz.narengi.android.content.AroundPlaceAttractionDeserializer;
import xyz.narengi.android.content.AroundPlaceCityDeserializer;
import xyz.narengi.android.content.AroundPlaceHouseDeserializer;
import xyz.narengi.android.content.SearchSuggestionProvider;
import xyz.narengi.android.service.RetrofitApiEndpoints;
import xyz.narengi.android.service.RetrofitService;
import xyz.narengi.android.service.SearchServiceAsyncTask;
import xyz.narengi.android.ui.adapter.RecyclerAdapter;
import xyz.narengi.android.ui.adapter.SearchResultsAdapter;
import xyz.narengi.android.ui.widget.CustomSearchView;
import xyz.narengi.android.util.SystemUiHider;
import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.internal.widget.TintImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.quinny898.library.persistentsearch.SearchBox;
import com.quinny898.library.persistentsearch.SearchResult;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author Siavash Mahmoudpour
 */
public class ExploreActivity extends ActionBarActivity implements SearchView.OnQueryTextListener, SearchView.OnSuggestionListener {

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    private AroundLocation[] aroundLocations;
    private List<AroundLocation> aroundLocationList;
    private RecyclerAdapter recyclerAdapter;
    private DrawerLayout drawerLayout;
    private CustomSearchView searchView;
    private SearchBox searchBox;
    private SearchResultsAdapter mSearchViewAdapter;
//    public static String[] columns = new String[]{"_id", "FEED_URL", "FEED_ICON", "FEED_SUBSCRIBERS", "FEED_TITLE"};
    public static String[] columns = new String[]{"_id", "FEED_TITLE"};
    private boolean loadingMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        setupToolbar();
//        final Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
//        if (toolbar != null) {
////            setSupportActionBar(toolbar);
//            ActionBar actionBar = getSupportActionBar();
//            if (actionBar != null) {
//                actionBar.setDisplayHomeAsUpEnabled(false);
//                actionBar.setDisplayShowHomeEnabled(false);
//                actionBar.setDisplayShowTitleEnabled(false);
//                actionBar.setDisplayUseLogoEnabled(false);
//            }
//        }
//        setupSearchBox();


        aroundLocationList = new ArrayList<AroundLocation>();
        setupListView(aroundLocationList);

        LoadDataAsyncTask loadDataAsyncTask = new LoadDataAsyncTask("");
        loadDataAsyncTask.execute();

//        Handler handler = new Handler();
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                aroundLocations = searchAroundLocations();
//                setupListView(aroundLocations);
//            }
//        });
    }

    private void setupSearchBox() {

//        searchBox = (SearchBox) findViewById(R.id.searchbox);
        searchBox.hideCircularly(this);
        for(int x = 0; x < 10; x++){
            SearchResult option = new SearchResult("Result " + Integer.toString(x), getResources().getDrawable(android.R.drawable.ic_menu_recent_history));
            searchBox.addSearchable(option);
        }
        searchBox.setLogoText("My App");
        searchBox.setMenuListener(new SearchBox.MenuListener(){

            @Override
            public void onMenuClick() {
                //Hamburger has been clicked
                Toast.makeText(ExploreActivity.this, "Menu click", Toast.LENGTH_LONG).show();
            }

        });
        searchBox.setSearchListener(new SearchBox.SearchListener(){

            @Override
            public void onSearchOpened() {
                //Use this to tint the screen
            }

            @Override
            public void onSearchClosed() {
                //Use this to un-tint the screen
            }

            @Override
            public void onSearchTermChanged(String s) {

            }

            @Override
            public void onSearch(String searchTerm) {
                Toast.makeText(ExploreActivity.this, searchTerm +" Searched", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onResultClick(com.quinny898.library.persistentsearch.SearchResult searchResult) {

            }


            @Override
            public void onSearchCleared() {

            }

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SearchBox.VOICE_RECOGNITION_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            searchBox.populateEditText(matches.get(0));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public AroundLocation[] searchAroundLocations() {

        SearchServiceAsyncTask searchAsyncTask = new SearchServiceAsyncTask("");
        Object object = null;
        try {
            object = searchAsyncTask.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (object != null)
            return (AroundLocation[])object;

        return null;
    }

//    @Override
    public boolean onCreateOptionsMenu_old(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.explore_menu, menu);

        MenuItem.OnMenuItemClickListener onMenuItemClickListener = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return onOptionsItemSelected(menuItem);
            }
        };

        // Retrieve the SearchView and plug it into SearchManager

        MenuItem settingsMenuItem = menu.findItem(R.id.explore_action_settings);
        settingsMenuItem.setOnMenuItemClickListener(onMenuItemClickListener);
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.ic_action_menu);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
//        layoutParams.leftMargin = 30;
//        layoutParams.rightMargin = 30;
        imageView.setLayoutParams(layoutParams);
//        imageView.setPadding(4, 4, 4, 4);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//        imageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.right_round_rect_bg));
        imageView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout != null) {
                    if (drawerLayout.isDrawerOpen(GravityCompat.END))
                        drawerLayout.closeDrawer(GravityCompat.END);
                    else
                        drawerLayout.openDrawer(GravityCompat.END);
                }
            }
        });
        settingsMenuItem.setActionView(imageView);
//        MenuItemCompat.getActionView(settingsMenuItem).setBackgroundColor(Color.WHITE);

        /*MenuItem rightSpaceMenuItem = menu.findItem(R.id.explore_action_right_space);
        TextView rightSpaceView = new TextView(this);

        LinearLayout.LayoutParams rightSpaceLayoutParams = new LinearLayout.LayoutParams(24,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        rightSpaceLayoutParams.leftMargin = 30;
        rightSpaceLayoutParams.rightMargin = 30;
        rightSpaceView.setLayoutParams(rightSpaceLayoutParams);
        rightSpaceView.setPadding(4, 4, 4, 4);
        rightSpaceMenuItem.setActionView(rightSpaceView);*/

        MenuItem searchMenuItem = menu.findItem(R.id.explore_action_search);
        searchMenuItem.setOnMenuItemClickListener(onMenuItemClickListener);
        searchMenuItem.setIcon(R.drawable.ic_action_search);
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();
//        int height = searchView.getLayoutParams().height;
//        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);
        searchView.clearFocus();
//        searchView.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_rect_bg));

        SearchView.SearchAutoComplete theTextArea = (SearchView.SearchAutoComplete)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        theTextArea.setHeight(40);
        theTextArea.setTextColor(Color.RED);
        theTextArea.setHintTextColor(Color.BLACK);
        theTextArea.setHint(R.string.search_hint);
        theTextArea.setCursorVisible(false);
        theTextArea.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        theTextArea.clearFocus();
        theTextArea.setGravity(Gravity.RIGHT);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.RIGHT);
//        params.leftMargin = 20;
//        params.rightMargin = 20;
        searchView.setLayoutParams(params);
//        searchView.setPadding(4, 4, 4, 4);

        searchView.setLayoutParams(new ActionBar.LayoutParams(GravityCompat.START));
        searchView.setLayoutParams(new ActionBar.LayoutParams(Gravity.RIGHT));
        searchView.clearFocus();
//        searchView.setBackgroundDrawable(getResources().getDrawable(R.drawable.rect_bg));
        searchView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        searchView.setQueryHint("\u200e" + getString(R.string.search_hint));

//        MenuItemCompat.collapseActionView(searchMenuItem);

        /*MenuItem leftSpaceMenuItem = menu.findItem(R.id.explore_action_left_space);
        TextView leftSpaceView = new TextView(this);

        LinearLayout.LayoutParams leftSpaceLayoutParams = new LinearLayout.LayoutParams(24,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        leftSpaceLayoutParams.leftMargin = 30;
        leftSpaceLayoutParams.rightMargin = 30;
        leftSpaceView.setLayoutParams(leftSpaceLayoutParams);
        leftSpaceView.setPadding(4, 4, 4, 4);
        leftSpaceMenuItem.setActionView(leftSpaceView);*/

        MenuItem mapMenuItem = menu.findItem(R.id.explore_action_map);
        mapMenuItem.setOnMenuItemClickListener(onMenuItemClickListener);
        ImageView mapImageView = new ImageView(this);
        mapImageView.setImageResource(R.drawable.ic_action_location_on);
        LinearLayout.LayoutParams mapLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
//        mapLayoutParams.leftMargin = 30;
//        mapLayoutParams.rightMargin = 30;
        mapImageView.setLayoutParams(mapLayoutParams);
//        mapImageView.setPadding(4, 4, 4, 4);
        mapImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//        mapImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.left_round_rect_bg));
        mapImageView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        mapMenuItem.setActionView(mapImageView);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        /*MenuItem searchViewMenuItem = menu.findItem(R.id.explore_action_search);
        SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(searchViewMenuItem);
        int searchImgId = android.support.v7.appcompat.R.id.search_mag_icon; // I used the explicit layout ID of searchview's ImageView
        ImageView v = (ImageView) mSearchView.findViewById(searchImgId);
//        v.setImageResource(R.drawable.ic_action_search);
        v.setImageDrawable(null);*/

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.explore_action_settings) {
            if (drawerLayout != null) {
                if (drawerLayout.isDrawerOpen(GravityCompat.END))
                    drawerLayout.closeDrawer(GravityCompat.END);
                else
                    drawerLayout.openDrawer(GravityCompat.END);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        // Because this activity has set launchMode="singleTop", the system calls this method
//        // to deliver the intent if this activity is currently the foreground activity when
//        // invoked again (when the user executes a search from this activity, we don't create
//        // a new instance of this activity, so the system delivers the search intent here)
//        handleIntent(intent);
//    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            // handles a click on a search suggestion; launches activity to show around location detail

//            Intent detailIntent = new Intent(this, DetailActivity.class);
//            Uri data = intent.getData();
//            detailIntent.setData(data);
//            startActivity(detailIntent);
//            finish();
//            Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
//            PopupWindow searchResultPopupWindow = createSearchResultPopup("");
//            searchResultPopupWindow.showAsDropDown(toolbar);
        } else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // handles a search query
            String query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(this, "This is query : " + query, Toast.LENGTH_LONG).show();
//            Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
//            PopupWindow searchResultPopupWindow = createSearchResultPopup(query);
//            searchResultPopupWindow.showAsDropDown(toolbar);
        }
    }

//    @Override
//    public boolean onSearchRequested() {
////        return super.onSearchRequested();
//        startSearch("testtest", false, null, false);
//        return true;
//    }

//    @Override
//    public void startSearch(String initialQuery, boolean selectInitialQuery, Bundle appSearchData, boolean globalSearch) {
////        super.startSearch(initialQuery, selectInitialQuery, appSearchData, globalSearch);
//
//        Toast.makeText(this, "This is query : " + initialQuery, Toast.LENGTH_LONG).show();
//        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
//        PopupWindow searchResultPopupWindow = createSearchResultPopup(initialQuery);
//        searchResultPopupWindow.showAsDropDown(toolbar);
//    }

    public PopupWindow createSearchResultPopup(String query, Toolbar toolbar) {

        // initialize a pop up window type
        PopupWindow popupWindow = new PopupWindow(this);

        // the drop down list is a list view
        ListView searchResultListView = getSearchResultListView(query);
        searchResultListView.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        LinearLayout innerLayout = (LinearLayout)toolbar.findViewById(R.id.toolbar_inner_layout);
        // some other visual settings
        popupWindow.setFocusable(true);
        popupWindow.setWidth(innerLayout.getWidth());
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        // set the list view as pop up window content
        popupWindow.setContentView(searchResultListView);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        return popupWindow;
    }

    private ListView getSearchResultListView(String query) {

        ListView listView = null;

        query = "qqq";
        String contentUri = "content://" + SearchSuggestionProvider.AUTHORITY + '/' + SearchManager.SUGGEST_URI_PATH_QUERY;
        Uri uri = Uri.parse(contentUri);
        Cursor cursor = managedQuery(uri, null, null,
                new String[] {query}, null);

//        Cursor cursor = managedQuery(SearchSuggestionProvider.CONTENT_URI, null, null,
//                new String[]{query}, null);

        if (cursor == null) {
            // There are no results
        } else {

            // Specify the columns we want to display in the result
            String[] from = new String[] { SearchManager.SUGGEST_COLUMN_TEXT_1,
                    SearchManager.SUGGEST_COLUMN_TEXT_2 };

            // Specify the corresponding layout elements where we want the columns to go
            int[] to = new int[] {R.id.search_result_item_title,
                    R.id.search_result_item_type};
//            int[] to = new int[] {R.id.search_result_item_title};

            listView = new ListView(this);

            // Create a simple cursor adapter for the definitions and apply them to the ListView
            SimpleCursorAdapter words = new SimpleCursorAdapter(this,
                    R.layout.search_result_item, cursor, from, to);
            listView.setAdapter(words);

            // Define the on-click listener for the list items
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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
        }

        return listView;
    }

    private void setupToolbar_old() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);

        if (toolbar != null) {
            final LinearLayout toolbarInnerLayout = (LinearLayout) toolbar.findViewById(R.id.toolbar_inner_layout);
            final AutoCompleteTextView searchView = (AutoCompleteTextView)toolbar.findViewById(R.id.toolbar_action_search);
            searchView.setDropDownAnchor(R.id.toolbar_inner_layout);

            final View dropDownAnchor = searchView.findViewById(searchView.getDropDownAnchor());
            if (dropDownAnchor != null) {
                dropDownAnchor.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                               int oldLeft, int oldTop, int oldRight, int oldBottom) {

                        // calculate width of DropdownView


                        int point[] = new int[2];
//                        dropDownAnchor.getLocationOnScreen(point);
                        toolbarInnerLayout.getLocationOnScreen(point);
                        // x coordinate of DropDownView
                        int dropDownPadding = point[0] + searchView.getDropDownHorizontalOffset();

                        Rect screenSize = new Rect();
                        getWindowManager().getDefaultDisplay().getRectSize(screenSize);
                        // screen width
                        int screenWidth = screenSize.width();

                        Rect layoutSize = new Rect();
                        toolbarInnerLayout.getGlobalVisibleRect(layoutSize);
                        DisplayMetrics metrics = getResources().getDisplayMetrics();
                        // set DropDownView width
//                        theTextArea.setDropDownWidth(screenWidth - dropDownPadding * 2);
//                        theTextArea.setDropDownWidth(toolbarInnerLayout.getWidth() - dropDownPadding);
//                        theTextArea.setDropDownWidth(right - left);
//                        theTextArea.setDropDownHorizontalOffset(theTextArea.getDropDownHorizontalOffset() - dropDownPadding);
//                        theTextArea.setDropDownHorizontalOffset(layoutSize.left + (int)metrics.density*dropDownPadding*3);
//                        theTextArea.setDropDownHorizontalOffset(left);

//                        searchView.setDropDownHorizontalOffset(searchView.getDropDownHorizontalOffset() - dropDownPadding / 2);
                        searchView.setDropDownWidth(toolbarInnerLayout.getWidth() + dropDownPadding);
//                        searchView.setDropDownWidth(toolbarInnerLayout.getWidth());
                    }
                });
            }

            String[] countries = new String[]{"Text 1", "Text 2", "Text 3", "Text 4"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,countries);
            searchView.setAdapter(adapter);

            searchView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    searchView.showDropDown();
                }
            });
            searchView.setThreshold(1);
        }
    }

    private void setupToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
//        final Toolbar toolbar = null;


        if (toolbar != null) {
//            final LinearLayout toolbarInnerLayout = (LinearLayout)toolbar.findViewById(R.id.toolbar_inner_layout);
//            AutoCompleteTextView searchView = (AutoCompleteTextView)toolbar.findViewById(R.id.toolbar_action_search);
            searchView = (CustomSearchView)toolbar.findViewById(R.id.toolbar_action_search);
            searchView.setIconified(false);
            searchView.setSubmitButtonEnabled(true);
            final SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setQueryHint(getString(R.string.search_hint));

//            searchView.setOnQueryTextListener(this);
//            searchView.setOnSuggestionListener(this);
//            mSearchViewAdapter = new SearchResultsAdapter(this, R.layout.search_result_item, null, columns,null, -1000);
//            searchView.setSuggestionsAdapter(mSearchViewAdapter);

            View searchEditFrame = searchView.findViewById(android.support.v7.appcompat.R.id.search_edit_frame);
            searchEditFrame.setBackgroundColor(getResources().getColor(android.R.color.white));

//            TintImageView searchCloseImageButton = (TintImageView)searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
//            searchCloseImageButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
//            searchCloseImageButton.setImageDrawable(null);

            View searchPlate = searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
            searchPlate.setBackgroundColor(getResources().getColor(android.R.color.white));

            final CustomSearchView.SearchAutoComplete theTextArea = (CustomSearchView.SearchAutoComplete)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            theTextArea.setHeight(searchView.getHeight());
//            theTextArea.setDropDownHorizontalOffset((int) toolbar.getX());
//            theTextArea.setTextColor(Color.RED);
//            theTextArea.setHintTextColor(Color.BLACK);
            theTextArea.setHint(R.string.search_hint);
//            theTextArea.setCursorVisible(false);
            theTextArea.setBackgroundColor(getResources().getColor(android.R.color.white));
            theTextArea.clearFocus();
            theTextArea.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
//            theTextArea.setDropDownAnchor(R.id.toolbar_action_search);

//            theTextArea.setDropDownWidth(ViewGroup.LayoutParams.MATCH_PARENT);
//            theTextArea.setDropDownWidth(theTextArea.getDropDownWidth() + 20);
            final View dropDownAnchor = searchView.findViewById(theTextArea.getDropDownAnchor());
//            if (dropDownAnchor != null) {
            if (2 < 1) {
                dropDownAnchor.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                               int oldLeft, int oldTop, int oldRight, int oldBottom) {

                        // calculate width of DropdownView


                        int point[] = new int[2];
//                        dropDownAnchor.getLocationOnScreen(point);
//                        toolbarInnerLayout.getLocationOnScreen(point);
                        searchView.getLocationOnScreen(point);
                        // x coordinate of DropDownView
                        int dropDownPadding = point[0] + theTextArea.getDropDownHorizontalOffset();

                        Rect screenSize = new Rect();
                        getWindowManager().getDefaultDisplay().getRectSize(screenSize);
                        // screen width
                        int screenWidth = screenSize.width();

                        Rect layoutSize = new Rect();
//                        toolbarInnerLayout.getGlobalVisibleRect(layoutSize);
                        searchView.getGlobalVisibleRect(layoutSize);
                        DisplayMetrics metrics = getResources().getDisplayMetrics();
                        // set DropDownView width
//                        theTextArea.setDropDownWidth(screenWidth - dropDownPadding * 2);
//                        theTextArea.setDropDownWidth(toolbarInnerLayout.getWidth() - dropDownPadding);
//                        theTextArea.setDropDownWidth(right - left);
//                        theTextArea.setDropDownHorizontalOffset(theTextArea.getDropDownHorizontalOffset() - dropDownPadding);
//                        theTextArea.setDropDownHorizontalOffset(layoutSize.left + (int)metrics.density*dropDownPadding*3);
//                        theTextArea.setDropDownHorizontalOffset(left);
//                        theTextArea.setDropDownHorizontalOffset(theTextArea.getDropDownHorizontalOffset() - dropDownPadding + 2);
//                        theTextArea.setDropDownHorizontalOffset(theTextArea.getDropDownHorizontalOffset() - dropDownPadding / 2);
//                        theTextArea.setDropDownWidth(toolbarInnerLayout.getWidth() + dropDownPadding/2);
//                        theTextArea.setDropDownWidth(toolbarInnerLayout.getWidth() - dropDownPadding);
                        theTextArea.setDropDownHorizontalOffset(theTextArea.getDropDownHorizontalOffset() - dropDownPadding);
                        theTextArea.setDropDownWidth(searchView.getWidth() - dropDownPadding);
                    }
                });
            }

            searchView.setOnMenuClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final boolean hasText = !TextUtils.isEmpty(theTextArea.getText());
                    if (hasText || theTextArea.hasFocus()) {
                        theTextArea.setText("");
                        searchView.dismissSuggestions();
                        searchView.clearFocus();
                    } else {
                        if (drawerLayout != null) {
                            if (drawerLayout.isDrawerOpen(GravityCompat.END))
                                drawerLayout.closeDrawer(GravityCompat.END);
                            else
                                drawerLayout.openDrawer(GravityCompat.END);
                        }
                    }
                }
            });

            searchView.clearFocus();
            searchView.setBackgroundColor(getResources().getColor(android.R.color.white));

//            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//                @Override
//                public boolean onQueryTextSubmit(String query) {
//                    showSearchResultsPopup(query, toolbarInnerLayout);
//                    return true;
//                }
//
//                @Override
//                public boolean onQueryTextChange(String newText) {
//                    showSearchResultsPopup(newText, toolbarInnerLayout);
//                    return true;
//                }
//            });

//            theTextArea.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    searchView.setQuery("", false);
//                }
//            });

//            ImageView settingsImageView = (ImageView)toolbar.findViewById(R.id.toolbar_action_settings);
//            settingsImageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (drawerLayout != null) {
//                        if (drawerLayout.isDrawerOpen(GravityCompat.END))
//                            drawerLayout.closeDrawer(GravityCompat.END);
//                        else
//                            drawerLayout.openDrawer(GravityCompat.END);
//                    }
//                }
//            });
        }

//        toolbar.getBackground().setAlpha(100);
        setSupportActionBar(toolbar);
//        toolbar.inflateMenu(R.menu.explore_menu);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayUseLogoEnabled(false);
        }
    }

    private void showSearchResultsPopup(String query, View toolbarInnerLayout) {
        int point[] = new int[2];
        toolbarInnerLayout.getLocationOnScreen(point);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        PopupWindow searchResultPopupWindow = createSearchResultPopup("", toolbar);
//                    searchResultPopupWindow.showAsDropDown(toolbarInnerLayout, (int)toolbarInnerLayout.getX(), (int)toolbarInnerLayout.getY());
//                    searchResultPopupWindow.showAsDropDown(toolbarInnerLayout, point[0], point[1]);
        searchResultPopupWindow.showAsDropDown(toolbarInnerLayout);
    }

    private void setupListView(List<AroundLocation> aroundLocations) {

        RecyclerView mRecyclerView = (RecyclerView)findViewById(R.id.scrollRecyclerView);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        recyclerAdapter = new RecyclerAdapter(aroundLocations, this);
        mRecyclerView.setAdapter(recyclerAdapter);
    }

    private List<Object> initData() {
        List<Object> objects = new ArrayList<Object>();
        for (int i=0 ; i < 10 ; i++) {
            objects.add(new Object());
        }
        return objects;
    }

    private void loadData(String searchText) {

        String BASE_URL = "http://149.202.20.233:3500";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);
        Call<AroundLocation[]> call = apiEndpoints.getAroundLocations(searchText, "30", "0");
        call.enqueue(new Callback<AroundLocation[]>() {
            @Override
            public void onResponse(Response<AroundLocation[]> response, Retrofit retrofit) {
//                int statusCode = response.code();
                AroundLocation[] aroundLocations = response.body();
                if (aroundLocations != null) {
                    MatrixCursor matrixCursor = convertToCursor(aroundLocations);
                    mSearchViewAdapter.changeCursor(matrixCursor);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                // Log error here since request failed
                t.printStackTrace();
                Log.d("RetrofitService", "onFailure : " + t.getMessage(), t);
            }
        });
    }

    private MatrixCursor convertToCursor(AroundLocation[] aroundLocations) {
        MatrixCursor cursor = new MatrixCursor(columns);
        int i = 0;
        for (AroundLocation aroundLocation : aroundLocations) {
//            String[] temp = new String[5];
            String[] temp = new String[2];
            i = i + 1;
            temp[0] = Integer.toString(i);

//            String feedUrl = aroundLocation.getFeedId();
//            if (feedUrl != null) {
//                int index = feedUrl.indexOf("feed/");
//                if (index != -1) {
//                    feedUrl = feedUrl.substring(5);
//                }
//            }
            temp[1] = aroundLocation.getType();
            cursor.addRow(temp);
        }
        return cursor;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (query.length() > 2) {
            loadData(query);
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.length() > 2) {
            loadData(newText);
        }
        return true;
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(position);
        String feedName = cursor.getString(4);
        searchView.setQuery(feedName, false);
        searchView.clearFocus();
        return true;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(position);
        String feedName = cursor.getString(1);
        searchView.setQuery(feedName, false);
        searchView.clearFocus();
        return true;
    }

    private class LoadDataAsyncTask extends AsyncTask {

        private String query;
        public LoadDataAsyncTask(String query) {
            this.query = query;
        }

        @Override
        protected void onPreExecute() {
            LinearLayout linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
            linlaHeaderProgress.setVisibility(View.VISIBLE);
        }


        @Override
        protected Object doInBackground(Object[] objects) {
            String BASE_URL = "http://149.202.20.233:3500";

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(AroundLocation.class, new AroundLocationDeserializer())
                    .registerTypeAdapter(AroundPlaceCity.class, new AroundPlaceCityDeserializer())
                    .registerTypeAdapter(AroundPlaceAttraction.class, new AroundPlaceAttractionDeserializer())
                    .registerTypeAdapter(AroundPlaceHouse.class, new AroundPlaceHouseDeserializer())
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
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

                        System.err.println("Around locations count : " + aroundLocations.length);
                        Log.d("RetrofitService", "Around locations count : " + aroundLocations.length);

                        loadingMore = false;

                        LinearLayout linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
                        linlaHeaderProgress.setVisibility(View.GONE);

                    } else
//                    System.err.println("Around locations is empty");
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