package xyz.narengi.android.armin.view.adapters.auth;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

/**
 * Created by arminghm on 1/29/17.
 */

public class AuthenticationViewPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = AuthenticationViewPagerAdapter.class.getSimpleName();
    private Fragment[] fragments;

    public AuthenticationViewPagerAdapter(FragmentManager fm, Fragment[] fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(TAG, "getItem: " + fragments[position].getClass().getSimpleName());
        return fragments[position];
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragments[position].getArguments().getString("title");
    }

    @Override
    public int getCount() {
        return fragments.length;
    }
}
