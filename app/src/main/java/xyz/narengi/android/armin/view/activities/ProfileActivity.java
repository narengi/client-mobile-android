package xyz.narengi.android.armin.view.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import xyz.narengi.android.R;
import xyz.narengi.android.armin.presenter.profile.ProfileActivityPresenter;
import xyz.narengi.android.armin.view.fragments.profile.ProfileDetailsFragment;
import xyz.narengi.android.armin.view.interfaces.profile.ProfileActivityView;

public class ProfileActivity extends AppCompatActivity implements ProfileActivityView {

    private static final String PROFILE_DETAILS_FRAGMENT_TAG = "PROFILE_DETAILS";

    private ProfileActivityPresenter profileActivityPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (profileActivityPresenter == null) {
            profileActivityPresenter = new ProfileActivityPresenter();

        }
        profileActivityPresenter.attachView(this);
        profileActivityPresenter.showProfileDetailsView();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        profileActivityPresenter.destroy();
    }

    @Override
    public void showProfileDetailsView() {
        ProfileDetailsFragment profileDetailsFragment;
        profileDetailsFragment = ((ProfileDetailsFragment)
                getSupportFragmentManager().findFragmentByTag(PROFILE_DETAILS_FRAGMENT_TAG));
        if (profileDetailsFragment == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.FrameLayoutFragmentContainer,
                            new ProfileDetailsFragment(), PROFILE_DETAILS_FRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    public void showEditProfileView() {
        // TODO: arminghm 1/31/17 Show EditProfileFragment.
    }
}
