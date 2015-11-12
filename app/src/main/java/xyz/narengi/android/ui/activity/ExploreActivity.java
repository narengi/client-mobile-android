package xyz.narengi.android.ui.activity;

import xyz.narengi.android.R;
import xyz.narengi.android.ui.adapter.RecyclerAdapter;
import xyz.narengi.android.util.SystemUiHider;
import android.app.SearchManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class ExploreActivity extends ActionBarActivity {

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

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        setupToolbar();
        setupListView();

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
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
        layoutParams.leftMargin = 30;
        layoutParams.rightMargin = 30;
        imageView.setLayoutParams(layoutParams);
        imageView.setPadding(4, 4, 4, 4);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg));
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

        MenuItem rightSpaceMenuItem = menu.findItem(R.id.explore_action_right_space);
        TextView rightSpaceView = new TextView(this);

        LinearLayout.LayoutParams rightSpaceLayoutParams = new LinearLayout.LayoutParams(24,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        rightSpaceLayoutParams.leftMargin = 30;
        rightSpaceLayoutParams.rightMargin = 30;
        rightSpaceView.setLayoutParams(rightSpaceLayoutParams);
        rightSpaceView.setPadding(4, 4, 4, 4);
        rightSpaceMenuItem.setActionView(rightSpaceView);

        MenuItem searchMenuItem = menu.findItem(R.id.explore_action_search);
        searchMenuItem.setOnMenuItemClickListener(onMenuItemClickListener);
        searchMenuItem.setIcon(R.drawable.ic_action_search);
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();
//        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);
        searchView.clearFocus();
        searchView.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_rect_bg));
        SearchView.SearchAutoComplete theTextArea = (SearchView.SearchAutoComplete)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        theTextArea.setTextColor(Color.RED);
        theTextArea.setHintTextColor(Color.BLACK);
        theTextArea.setHint(R.string.search_hint);
        theTextArea.setCursorVisible(false);
        theTextArea.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        theTextArea.clearFocus();
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.RIGHT);
        params.leftMargin = 20;
        params.rightMargin = 20;
        searchView.setLayoutParams(params);
        searchView.setPadding(4, 4, 4, 4);

        searchView.setLayoutParams(new ActionBar.LayoutParams(GravityCompat.START));
        searchView.setLayoutParams(new ActionBar.LayoutParams(Gravity.RIGHT));
        searchView.clearFocus();
        searchView.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_rect_bg));
        searchView.setQueryHint("\u200e" + getString(R.string.search_hint));

//        MenuItemCompat.collapseActionView(searchMenuItem);

        MenuItem leftSpaceMenuItem = menu.findItem(R.id.explore_action_left_space);
        TextView leftSpaceView = new TextView(this);

        LinearLayout.LayoutParams leftSpaceLayoutParams = new LinearLayout.LayoutParams(24,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        leftSpaceLayoutParams.leftMargin = 30;
        leftSpaceLayoutParams.rightMargin = 30;
        leftSpaceView.setLayoutParams(leftSpaceLayoutParams);
        leftSpaceView.setPadding(4, 4, 4, 4);
        leftSpaceMenuItem.setActionView(leftSpaceView);

        MenuItem mapMenuItem = menu.findItem(R.id.explore_action_map);
        mapMenuItem.setOnMenuItemClickListener(onMenuItemClickListener);
        ImageView mapImageView = new ImageView(this);
        mapImageView.setImageResource(R.drawable.ic_action_location_on);
        LinearLayout.LayoutParams mapLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mapLayoutParams.leftMargin = 30;
        mapLayoutParams.rightMargin = 30;
        mapImageView.setLayoutParams(mapLayoutParams);
        mapImageView.setPadding(4, 4, 4, 4);
        mapImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mapImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg));
        mapMenuItem.setActionView(mapImageView);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem searchViewMenuItem = menu.findItem(R.id.explore_action_search);
        SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(searchViewMenuItem);
        int searchImgId = android.support.v7.appcompat.R.id.search_mag_icon; // I used the explicit layout ID of searchview's ImageView
        ImageView v = (ImageView) mSearchView.findViewById(searchImgId);
//        v.setImageResource(R.drawable.ic_action_search);
        v.setImageDrawable(null);

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

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
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

    private void setupListView() {

        RecyclerView mRecyclerView = (RecyclerView)findViewById(R.id.scrollRecyclerView);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        RecyclerAdapter adapter = new RecyclerAdapter(initData(), this);
        mRecyclerView.setAdapter(adapter);
    }

    private List<Object> initData() {
        List<Object> objects = new ArrayList<Object>();
        for (int i=0 ; i < 10 ; i++) {
            objects.add(new Object());
        }
        return objects;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

}