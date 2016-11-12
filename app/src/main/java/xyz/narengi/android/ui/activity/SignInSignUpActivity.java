package xyz.narengi.android.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import info.semsamot.actionbarrtlizer.ActionBarRtlizer;
import info.semsamot.actionbarrtlizer.RtlizeEverything;
import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.AccountProfile;
import xyz.narengi.android.service.WebService;
import xyz.narengi.android.service.WebServiceConstants;
import xyz.narengi.android.ui.fragment.SignInFragment;
import xyz.narengi.android.ui.fragment.SignUpFragment;
import xyz.narengi.android.ui.util.AlertUtils;
import xyz.narengi.android.util.NetworkUtil;
import xyz.narengi.android.util.Util;

/**
 * @author Siavash Mahmoudpour
 */
public class SignInSignUpActivity extends AppCompatActivity implements SignUpFragment.OnRegisterButtonClickListener, SignInFragment.OnLoginButtonClickListener {

    public static final int RESULT_SIGN_UP_SUCCESS = 302;
    public static final int RESULT_LOGIN_SUCCESS = 303;
    public static final int REQUEST_CODE = 101;

    private Context context;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ActionBarRtlizer rtlizer;
    private ImageView imgBackground;
    private View loadingLayer;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        setContentView(R.layout.activity_sign_in_sign_up);
        setupToolbar();
//        initViews();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);

        viewPager = (ViewPager) findViewById(R.id.login_viewpager);
        loadingLayer = findViewById(R.id.llLoadingLayer);
        imgBackground = (ImageView) findViewById(R.id.imgBackground);
        setupViewPager();

        tabLayout = (TabLayout) findViewById(R.id.login_tabs);
        tabLayout.setupWithViewPager(viewPager);

        findViewById(R.id.rootLayout).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                int heightDiff = findViewById(R.id.rootLayout).getRootView().getHeight() - findViewById(R.id.rootLayout).getHeight();
                if (heightDiff > Util.convertDpToPx(SignInSignUpActivity.this, 200)) { // if more than 200 dp, it's probably a keyboard...
                    toolbar.setVisibility(View.GONE);
                    findViewById(R.id.llWelcomeContainer).setVisibility(View.GONE);
                } else {
                    toolbar.setVisibility(View.VISIBLE);
                    findViewById(R.id.llWelcomeContainer).setVisibility(View.VISIBLE);
                }
            }
        });

        imgBackground.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    imgBackground.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    imgBackground.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                DisplayMetrics screenMetric = Util.getScreenMetrics(context);
                int screenHeight = screenMetric.heightPixels;
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) imgBackground.getLayoutParams();
                params.height = screenHeight;
            }
        });


        tabLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    tabLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    tabLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
                int tabsCount = vg.getChildCount();
                for (int j = 0; j < tabsCount; j++) {
                    ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
                    int tabChildsCount = vgTab.getChildCount();
                    for (int i = 0; i < tabChildsCount; i++) {
                        View tabViewChild = vgTab.getChildAt(i);
                        if (tabViewChild instanceof TextView) {
                            ((TextView) tabViewChild).setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/IRAN-Sans.ttf"));
                        }
                    }
                }
            }
        });

    }

    private void setPageTitle(String title) {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.login_toolbar);
        if (toolbar != null) {
            TextView titleTextView = (TextView) toolbar.findViewById(R.id.text_toolbar_title);
            titleTextView.setText(title);
        }
    }

    protected void rtlizeActionBar() {
        if (getSupportActionBar() != null) {
//            rtlizer = new ActionBarRtlizer(this, "toolbar_actionbar");
            rtlizer = new ActionBarRtlizer(this);
            ;
            ViewGroup homeView = (ViewGroup) rtlizer.getHomeView();
            RtlizeEverything.rtlize(rtlizer.getActionBarView());
            if (rtlizer.getHomeViewContainer() instanceof ViewGroup) {
                RtlizeEverything.rtlize((ViewGroup) rtlizer.getHomeViewContainer());
            }
            RtlizeEverything.rtlize(homeView);
            rtlizer.flipActionBarUpIconIfAvailable(homeView);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.login_toolbar);

        /*Drawable backButtonDrawable = getResources().getDrawable(R.drawable.ic_action_back);
        backButtonDrawable.setColorFilter(getResources().getColor(android.R.color.holo_orange_dark), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationIcon(backButtonDrawable);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });*/

        setSupportActionBar(toolbar);

        if (toolbar != null) {
            ImageView backButton = (ImageView) toolbar.findViewById(R.id.icon_toolbar_back);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
//            actionBar.setHomeButtonEnabled(true);
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setDisplayShowHomeEnabled(true);
//            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SignUpFragment(), getString(R.string.register_button));
        adapter.addFragment(new SignInFragment(), getString(R.string.login_button));
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                if (state == ViewPager.SCROLL_STATE_SETTLING) {
//                    Util.hideSoftKeyboard(context, getCurrentFocus() == null ? viewPager : getCurrentFocus());
//                }
            }
        });
        viewPager.setCurrentItem(1);
    }

    private void initViews() {

//        Button loginButton = (Button)findViewById(R.id.login_loginButton);
//        Button registerButton = (Button)findViewById(R.id.login_registerButton);

//        loginButton.setOnClickListener(this);
//        registerButton.setOnClickListener(this);
    }

    //    @Override
    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.login_loginButton:
//                break;
//            case R.id.login_registerButton:
//                register();
//                break;
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void login(String email, String password) {
        JSONObject params = new JSONObject();
        try {
            params.put("username", email);
            params.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebService service = new WebService();
        service.setResponseHandler(new WebService.ResponseHandler() {
            @Override
            public void onPreRequest(String requestUrl) {
                showLoadingProgress();
            }

            @Override
            public void onSuccess(String requestUrl, Object response) {
                hideLoadingProgress();
                JSONObject responseObject = (JSONObject) response;
                AccountProfile loggedInProfile = AccountProfile.fromJsonObject(responseObject);
                loggedInProfile.saveToSharedPref(context);

                setResult(RESULT_LOGIN_SUCCESS);
                finish();
            }

            @Override
            public void onError(String requestUrl, VolleyError error) {
                hideLoadingProgress();
                String errorMessage = Util.getErrorMessage(error);
                if (errorMessage != null) {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
        service.postJsonObject(WebServiceConstants.Accounts.LOGIN, params);
    }

    private void register(String email, String password) {

        JSONObject params = new JSONObject();
        try {
            params.put("username", email);
            params.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebService service = new WebService();
        service.setResponseHandler(new WebService.ResponseHandler() {
            @Override
            public void onPreRequest(String requestUrl) {
                showLoadingProgress();
            }

            @Override
            public void onSuccess(String requestUrl, Object response) {
                hideLoadingProgress();
                JSONObject responseObject = (JSONObject) response;
                AccountProfile loggedInProfile = AccountProfile.fromJsonObject(responseObject);
                loggedInProfile.saveToSharedPref(context);
                setResult(RESULT_SIGN_UP_SUCCESS);
                finish();
            }

            @Override
            public void onError(String requestUrl, VolleyError error) {
                hideLoadingProgress();
                error.printStackTrace();
                String errorMessage = Util.getErrorMessage(error);
                if (errorMessage != null) {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
        service.postJsonObject(WebServiceConstants.Accounts.REGISTER, params);
    }

    private void showLoadingProgress() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("لطفا کمی صبر کنید...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    private void hideLoadingProgress() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 || resultCode == 101) {
            finish();
        }
    }

    @Override
    public void onRegisterButtonPressed(String email, String password) {

        if (NetworkUtil.getInstance().isNetworkConnected(this)) {
            register(email, password);
        } else {
            AlertUtils.getInstance().showNetworkErrorDialog(this, getString(R.string.network_error_alert_message));
        }
    }

    @Override
    public void onLoginButtonPressed(String email, String password) {

        if (NetworkUtil.getInstance().isNetworkConnected(this)) {
            login(email, password);
        } else {
            AlertUtils.getInstance().showNetworkErrorDialog(this, getString(R.string.network_error_alert_message));
        }
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
