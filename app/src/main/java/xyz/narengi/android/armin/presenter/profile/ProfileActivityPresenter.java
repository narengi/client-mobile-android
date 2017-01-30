package xyz.narengi.android.armin.presenter.profile;

import xyz.narengi.android.armin.view.interfaces.profile.ProfileActivityView;

/**
 * Created by arminghm on 1/30/17.
 */

public class ProfileActivityPresenter {

    private ProfileActivityView profileActivityView;

    public void attachView(ProfileActivityView profileActivityView) {
        this.profileActivityView = profileActivityView;
    }

    public void detachView() {
        profileActivityView = null;
    }

    public void destroy() {
        // TODO: arminghm 1/30/17 Cancel any API call.
    }

    public void showProfileDetailsView() {
        profileActivityView.showProfileDetailsView();
    }

    public void showEditProfileView() {
        profileActivityView.showEditProfileView();
    }
}
