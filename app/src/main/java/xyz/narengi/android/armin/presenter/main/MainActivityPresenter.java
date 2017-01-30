package xyz.narengi.android.armin.presenter.main;

import xyz.narengi.android.armin.view.interfaces.main.MainActivityView;

/**
 * Created by arminghm on 1/25/17.
 */

public class MainActivityPresenter {
    MainActivityView mainActivityView;
    boolean userLoggedIn = false;

    public MainActivityPresenter(MainActivityView mainActivityView) {
        this.mainActivityView = mainActivityView;
    }

    public void menuHomeClick() {
        mainActivityView.openExploreScreen();
    }

    public void menuAuthenticationClick() {
        mainActivityView.openAuthenticationScreen();
    }

    public void menuProfileClick() {
        if (userLoggedIn) {
            mainActivityView.openProfileScreen();
        }
    }

    public void menuHostingClick() {
        mainActivityView.openHostingScreen();
    }

}
