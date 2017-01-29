package xyz.narengi.android.armin.view.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import xyz.narengi.android.R;
import xyz.narengi.android.armin.view.adapters.auth.AuthenticationViewPagerAdapter;
import xyz.narengi.android.armin.view.fragments.auth.LoginFragment;
import xyz.narengi.android.armin.view.fragments.auth.RegisterFragment;

public class AuthenticationActivity extends AppCompatActivity {

    private ViewPager viewPagerAuthentication;
    private TabLayout tabLayoutAuthentication;
    private ImageButton imageButtonBack;
    private Fragment[] fragments = new Fragment[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_sign_up);

        viewPagerAuthentication = (ViewPager) findViewById(R.id.login_viewpager);
        tabLayoutAuthentication = (TabLayout) findViewById(R.id.login_tabs);
        imageButtonBack = (ImageButton) findViewById(R.id.ImageButtonBack);

        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fragments[0] = RegisterFragment.newInstance("ثبت نام");
        fragments[1] = LoginFragment.newInstance("ورود");

        AuthenticationViewPagerAdapter authenticationViewPagerAdapter =
                new AuthenticationViewPagerAdapter(getSupportFragmentManager(), fragments);
        viewPagerAuthentication.setAdapter(authenticationViewPagerAdapter);
        tabLayoutAuthentication.setupWithViewPager(viewPagerAuthentication);

        viewPagerAuthentication.setCurrentItem(1,true);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
