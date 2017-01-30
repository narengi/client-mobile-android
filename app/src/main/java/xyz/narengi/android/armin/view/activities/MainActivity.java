package xyz.narengi.android.armin.view.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import xyz.narengi.android.R;
import xyz.narengi.android.armin.presenter.main.MainActivityPresenter;
import xyz.narengi.android.armin.view.fragments.main.ExploreFragment;
import xyz.narengi.android.armin.view.fragments.main.UserPlacesForHostingFragment;
import xyz.narengi.android.armin.view.interfaces.main.MainActivityView;
import xyz.narengi.android.armin.view.interfaces.main.UserPlacesForHostingView;

public class MainActivity extends AppCompatActivity implements NavigationView
        .OnNavigationItemSelectedListener, ExploreFragment.ExplorerFragmentClickListener,
        MainActivityView, View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String EXPLORE_FRAGMENT_TAG = "EXPLORE_FRAGMENT";
    private static final String USER_PLACES_FOR_HOSTING_FRAGMENT_TAG = "USER_PLACES_FOR_HOSTING_FRAGMENT";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView textViewHosting;
    private MainActivityPresenter mainActivityPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        textViewHosting = (TextView) findViewById(R.id.TextViewHosting);
        textViewHosting.setOnClickListener(this);
        drawerLayout = (DrawerLayout) findViewById(R.id.DrawerLayoutMain);
        navigationView = (NavigationView) findViewById(R.id.MainNavigationView);
        navigationView.setNavigationItemSelectedListener(this);
        // Set clickListener for FrameLayout in the NavigationView Header.
        navigationView.getHeaderView(0).setOnClickListener(this);

        mainActivityPresenter = new MainActivityPresenter(this);
        // Show ExplorerFragment and check the home item on NavigationView.
        navigationView.setCheckedItem(R.id.MenuHome);
        navigationView.getMenu().performIdentifierAction(R.id.MenuHome, 0);

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.MenuHome:
                mainActivityPresenter.menuHomeClick();
                break;
            case R.id.MenuLoginRegister:
                mainActivityPresenter.menuAuthenticationClick();
                break;
        }
        showNavigationView(false);
        return true;
    }

    @Override
    public void openExploreScreen() {
        ExploreFragment exploreFragment;
        exploreFragment = (ExploreFragment) getSupportFragmentManager()
                .findFragmentByTag(EXPLORE_FRAGMENT_TAG);
        if (exploreFragment == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.FrameLayoutFragmentContainer,
                            new ExploreFragment(),
                            EXPLORE_FRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    public void openAuthenticationScreen() {
        startActivity(new Intent(MainActivity.this, AuthenticationActivity.class));
    }


    @Override
    public void openProfileScreen() {

    }

    @Override
    public void openHostingScreen() {
        UserPlacesForHostingFragment userPlacesForHostingFragment;
        userPlacesForHostingFragment = (UserPlacesForHostingFragment) getSupportFragmentManager()
                .findFragmentByTag(USER_PLACES_FOR_HOSTING_FRAGMENT_TAG);
        if (userPlacesForHostingFragment == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.FrameLayoutFragmentContainer,
                            new UserPlacesForHostingFragment(),
                            USER_PLACES_FOR_HOSTING_FRAGMENT_TAG)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onImageButtonNavigationViewClick() {
        showNavigationView(!drawerLayout.isDrawerOpen(Gravity.RIGHT));
    }

    private void showNavigationView(boolean show) {
        if (show) {
            drawerLayout.openDrawer(Gravity.RIGHT);
        } else {
            drawerLayout.closeDrawer(Gravity.RIGHT);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.TextViewHosting:
                mainActivityPresenter.menuHostingClick();
                showNavigationView(!drawerLayout.isDrawerOpen(Gravity.RIGHT));
                break;
            default:
                // This is the ClickListener for NavigationView Header.
                mainActivityPresenter.menuProfileClick();
                showNavigationView(!drawerLayout.isDrawerOpen(Gravity.RIGHT));
                break;
        }
    }
}
