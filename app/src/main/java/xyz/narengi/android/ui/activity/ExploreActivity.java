package xyz.narengi.android.ui.activity;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import xyz.narengi.android.R;
import xyz.narengi.android.common.Constants;
import xyz.narengi.android.common.adapter.SearchListAdapter;
import xyz.narengi.android.common.dto.AccountProfile;
import xyz.narengi.android.common.dto.Attraction;
import xyz.narengi.android.common.dto.City;
import xyz.narengi.android.common.dto.DrawerItem;
import xyz.narengi.android.common.model.AroundLocation;
import xyz.narengi.android.service.WebService;
import xyz.narengi.android.service.WebServiceConstants;
import xyz.narengi.android.ui.adapter.DrawerItemsListAdapter;
import xyz.narengi.android.util.Util;

/**
 * @author Siavash Mahmoudpour
 */
public class ExploreActivity extends AppCompatActivity implements View.OnClickListener, WebService.ResponseHandler, SwipeRefreshLayout.OnRefreshListener {

    private Context context;

    private DrawerLayout drawerLayout;

    private View llErrorContainer;
    private Button btnRetry;
    private ListView lstSearchList;
    private EditText etToolbarSearch;
    private View imgDrawerMenu;

    private TextView tvUserFullName;
    private ImageView imgUserAvatar;
    private View rlUserInfoContainer;
    private View tvWelcomeMessage;
    private ListView lstNavigationMenu;
    private View rlFooterContainer;
    private DrawerItemsListAdapter menuItemsListAdapter;
    private View llLoadingLayer;
    private View rlSearchContainer;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean loading;
    private int page = 1;
    private List<AroundLocation> locations;
    private SearchListAdapter adapter;
    private boolean lastPage;

    private int saveScrollY = 0;
    private boolean isSearchHidden = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        setContentView(R.layout.activity_explore);

        locations = new ArrayList<>();
        setupViews();
        page = 1;
        startDefaultSearch(1);
    }

    private void startDefaultSearch(int page) {
        WebService service = new WebService();
        service.setResponseHandler(this);

        JSONObject params = new JSONObject();
        try {
            params.put(WebServiceConstants.Search.TERM_QUERY_KEY, etToolbarSearch.getText().toString());
            params.put("perpage", 20);
            params.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        service.getJsonArray(WebServiceConstants.Search.SEARCH, params);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForLoggedInUser();
    }

    private void checkForLoggedInUser() {
        if (AccountProfile.getLoggedInAccountProfile(context) != null) {
            WebService service = new WebService();
            service.setToken(AccountProfile.getLoggedInAccountProfile(context).getToken().getAuthString());
            service.setResponseHandler(new WebService.ResponseHandler() {
                @Override
                public void onPreRequest(String requestUrl) {

                }

                @Override
                public void onSuccess(String requestUrl, Object response) {
                    JSONObject responseObject = (JSONObject) response;
                    AccountProfile loggedInProfile = AccountProfile.fromJsonObject(responseObject);
                    if (loggedInProfile != null) {
                        loggedInProfile.saveToSharedPref(context);
                    }
                    setupDrawerView(true);
                }

                @Override
                public void onError(String requestUrl, VolleyError error) {
                    try {
                        if (error.networkResponse.statusCode == 401 || error.networkResponse.statusCode == 403) {
                            AccountProfile.logout(context);
                            setupDrawerView(false);
                        } else {
                            setupDrawerView(true);
                        }
                    }catch (Exception e) {}
                }
            });
            service.getJsonObject(WebServiceConstants.Accounts.ME);
        } else {
            setupDrawerView(false);
        }
    }

    private void setupViews() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        llErrorContainer = findViewById(R.id.llErrorContainer);
        btnRetry = (Button) findViewById(R.id.btnRetry);
        lstSearchList = (ListView) findViewById(R.id.lstSearchList);
        etToolbarSearch = (EditText) findViewById(R.id.etToolbarSearch);
        imgDrawerMenu = findViewById(R.id.imgDrawerMenu);
        tvUserFullName = (TextView) findViewById(R.id.tvUserFullName);
        imgUserAvatar = (ImageView) findViewById(R.id.imgUserAvatar);
        rlUserInfoContainer = findViewById(R.id.rlUserProfileInfoContainer);
        tvWelcomeMessage = findViewById(R.id.tvWelcomeMessage);
        lstNavigationMenu = (ListView) findViewById(R.id.lstDrawerItemsList);
        rlFooterContainer = findViewById(R.id.rlFooterContainer);
        llLoadingLayer = findViewById(R.id.llLoadingLayer);
        rlSearchContainer = findViewById(R.id.rlSearchContainer);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srlSearchList);
        swipeRefreshLayout.setColorSchemeColors(Util.getColor(context, R.color.primary));
        swipeRefreshLayout.setOnRefreshListener(this);

        imgDrawerMenu.setOnClickListener(this);
        btnRetry.setOnClickListener(this);


        etToolbarSearch.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        // Identifier of the action. This will be either the identifier you supplied,
                        // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                        if (actionId == EditorInfo.IME_ACTION_SEARCH
                                || actionId == EditorInfo.IME_ACTION_DONE
                                || event.getAction() == KeyEvent.ACTION_DOWN
                                && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            View view = getCurrentFocus();
                            if (view != null) {
                                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }
                            page = 1;
                            startDefaultSearch(1);
                            return true;
                        }
                        // Return true if you have consumed the action, else false.
                        return false;
                    }
                });


        adapter = new SearchListAdapter(this, locations);
        lstSearchList.setAdapter(adapter);


        lstSearchList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.d("ScrollState", scrollState == SCROLL_STATE_TOUCH_SCROLL ? "SCROLL_STATE_TOUCH_SCROLL" : scrollState == SCROLL_STATE_FLING ? "SCROLL_STATE_FLING" : scrollState == SCROLL_STATE_IDLE ? "SCROLL_STATE_IDLE" : "");
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                View c = view.getChildAt(0);
                if (c == null)
                    return;
                int scrollY = -c.getTop() + view.getFirstVisiblePosition() * c.getHeight();

                if (scrollY - saveScrollY > 50) {
                    if (!isSearchHidden) {
                        isSearchHidden = true;
                        hideSearchBarAnimated();
                    }
                    saveScrollY = scrollY;
                } else if (saveScrollY - scrollY > 50) {
                    if (isSearchHidden) {
                        isSearchHidden = false;
                        showSearchBarAnimated();
                    }
                    saveScrollY = scrollY;
                }

                if (!loading && !lastPage) {
                    if (firstVisibleItem + visibleItemCount  >= totalItemCount) {
                        startDefaultSearch(page);
                        loading = true;
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SignInSignUpActivity.REQUEST_CODE) {
            if (resultCode == SignInSignUpActivity.RESULT_SIGN_UP_SUCCESS) {
                //user registered, update menu and open sign up confirm
                setupDrawerView(true);
                openSignUpConfirm();
            } else if (resultCode == SignInSignUpActivity.RESULT_LOGIN_SUCCESS) {
                //user logged in, update menu
                setupDrawerView(true);
            }
        } else if (requestCode == ViewProfileActivity.REQUEST_CODE && resultCode == ViewProfileActivity.RESULT_LOGOUT) {
            setupDrawerView(false);
        }

        if (resultCode == 401) {
            setupDrawerView(true);
            openViewProfile();
        }
    }

    private void openSignUpConfirm() {
        Intent intent = new Intent(this, SignUpConfirmActivity.class);
        startActivityForResult(intent, SignInSignUpActivity.REQUEST_CODE);
    }

    private void setupDrawerView(final boolean loggedIn) {
        rlUserInfoContainer.setVisibility(loggedIn ? View.VISIBLE : View.GONE);
        tvWelcomeMessage.setVisibility(loggedIn ? View.GONE : View.VISIBLE);


        List<DrawerItem> drawerItems = new ArrayList<>();
        if (loggedIn) {
            // TODO: 9/22/2016 AD fix icon and string
            drawerItems.add(new DrawerItem(getString(R.string.home), R.drawable.ic_action_inbox, DrawerItem.DrawerAction.ACTION_HOME));
            drawerItems.add(new DrawerItem(getString(R.string.drawer_menu_inbox), R.drawable.ic_action_inbox, DrawerItem.DrawerAction.ACTION_INBOX));
            drawerItems.add(new DrawerItem(getString(R.string.drawer_menu_favorites), R.drawable.ic_action_favorite_list, DrawerItem.DrawerAction.ACTION_LOGOUT));
            drawerItems.add(new DrawerItem(getString(R.string.profile), R.drawable.ic_action_inbox, DrawerItem.DrawerAction.ACTION_PROFILE));
            drawerItems.add(new DrawerItem(getString(R.string.drawer_menu_settings), R.drawable.ic_action_settings, DrawerItem.DrawerAction.ACTION_SETTINGS));
            drawerItems.add(new DrawerItem(getString(R.string.user_guide), R.drawable.ic_action_inbox, DrawerItem.DrawerAction.ACTION_USER_GUIDE));
            tvUserFullName = (TextView) findViewById(R.id.tvUserFullName);
            tvUserFullName.setText(TextUtils.isEmpty(AccountProfile.getLoggedInAccountProfile(context).getDisplayName()) ? "نام، نام خانوادگی" : AccountProfile.getLoggedInAccountProfile(context).getDisplayName());

            Picasso.with(context).load(Constants.SERVER_BASE_URL + "/v1/medias/avatar")
                    .error(R.drawable.profile_image).into(imgUserAvatar);
        } else {
            drawerItems.add(new DrawerItem(getString(R.string.drawer_menu_login_register), R.drawable.ic_action_login_signup, DrawerItem.DrawerAction.ACTION_LOGIN_SIGN_UP));
            drawerItems.add(new DrawerItem(getString(R.string.home), R.drawable.ic_action_inbox, DrawerItem.DrawerAction.ACTION_HOME));
            drawerItems.add(new DrawerItem(getString(R.string.drawer_menu_settings), R.drawable.ic_action_settings, DrawerItem.DrawerAction.ACTION_SETTINGS));
            drawerItems.add(new DrawerItem(getString(R.string.user_guide), R.drawable.ic_action_inbox, DrawerItem.DrawerAction.ACTION_USER_GUIDE));
        }

        this.rlUserInfoContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openViewProfile();
            }
        });

        menuItemsListAdapter = new DrawerItemsListAdapter(context, drawerItems);
        lstNavigationMenu.setAdapter(menuItemsListAdapter);

        lstNavigationMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                drawerLayout.closeDrawers();
                DrawerItem selectedItem = (DrawerItem) menuItemsListAdapter.getItem(position);
                if (selectedItem.getAction() == DrawerItem.DrawerAction.ACTION_HOME) {
                    // TODO: 9/22/2016 AD back to home
                } else if (selectedItem.getAction() == DrawerItem.DrawerAction.ACTION_INBOX) {

                } else if (selectedItem.getAction() == DrawerItem.DrawerAction.ACTION_FAVORITES) {

                } else if (selectedItem.getAction() == DrawerItem.DrawerAction.ACTION_PROFILE) {
                    openViewProfile();
                } else if (selectedItem.getAction() == DrawerItem.DrawerAction.ACTION_LOGIN_SIGN_UP) {
                    openSignInSignUp();
                } else if (selectedItem.getAction() == DrawerItem.DrawerAction.ACTION_USER_GUIDE) {

                } else if (selectedItem.getAction() == DrawerItem.DrawerAction.ACTION_SETTINGS) {

                } else if (selectedItem.getAction() == DrawerItem.DrawerAction.ACTION_LOGOUT) {
                    AccountProfile.logout(context);
                    setupDrawerView(false);
                }
            }
        });
        rlFooterContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loggedIn) {
					if (TextUtils.isEmpty(AccountProfile.getLoggedInAccountProfile(context).getDisplayName())) {
						openSignUpConfirm();
					} else {
						openHostHouses();
					}
                } else {
                    openSignInSignUp();
                }
            }
        });
    }

    private void hideSearchBarAnimated() {
        int startMargin = (int) Util.convertDpToPx(context, 16);
        int endMargin = rlSearchContainer.getHeight() * -1;

        ValueAnimator animator = ValueAnimator.ofInt(startMargin, endMargin);
        animator.setDuration(300);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int margin = (int) animation.getAnimatedValue();
                ((RelativeLayout.LayoutParams) rlSearchContainer.getLayoutParams()).topMargin = margin;
                rlSearchContainer.requestLayout();
            }
        });
        animator.start();
    }

    private void showSearchBarAnimated() {
        int startMargin = rlSearchContainer.getHeight() * -1;
        int endMargin = (int) Util.convertDpToPx(context, 16);

        ValueAnimator animator = ValueAnimator.ofInt(startMargin, endMargin);
        animator.setDuration(300);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int margin = (int) animation.getAnimatedValue();
                ((RelativeLayout.LayoutParams) rlSearchContainer.getLayoutParams()).topMargin = margin;
                rlSearchContainer.requestLayout();
            }
        });
        animator.start();
    }

    private void openCityDetail(String cityId) {
        String cityUrl = City.getURL(cityId);
        Intent intent = new Intent(this, CityActivity.class);
        intent.putExtra("cityUrl", cityUrl);
        startActivity(intent);
    }

    private void openAttractionDetail(String attractionId) {
        String attractionUrl = Attraction.getURL(attractionId);
        Intent intent = new Intent(this, AttractionActivity.class);
        intent.putExtra("attractionUrl", attractionUrl);
        startActivity(intent);
    }

    private void openHouseDetail(String houseId) {
        Intent intent = new Intent(context, HouseActivity.class);
        intent.putExtra("houseId", houseId);
        context.startActivity(intent);
    }

    private void openHostHouses() {
        Intent intent = new Intent(this, HostHousesActivity.class);
        startActivity(intent);
    }

    private void openSignInSignUp() {
        Intent intent = new Intent(this, SignInSignUpActivity.class);
        startActivityForResult(intent, SignInSignUpActivity.REQUEST_CODE);
    }

    private void openViewProfile() {
        Intent intent = new Intent(this, ViewProfileActivity.class);
        startActivityForResult(intent, ViewProfileActivity.REQUEST_CODE);
    }

    private void showProgress() {
        hideError();
        llLoadingLayer.setVisibility(View.VISIBLE);
//        swipeRefreshLayout.setRefreshing(true);
    }

    private void hideProgress() {
        llLoadingLayer.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void showError() {
        hideProgress();
        llErrorContainer.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        llErrorContainer.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnRetry) {
            page = 1;
            startDefaultSearch(1);
        } else if (id == R.id.imgDrawerMenu) {
            drawerLayout.openDrawer(Gravity.RIGHT, true);
        }
    }

    @Override
    public void onPreRequest(String requestUrl) {
        if (requestUrl.startsWith(WebServiceConstants.Search.SEARCH)) {
            showProgress();
        }
    }

    @Override
    public void onSuccess(String requestUrl, Object response) {
        if (requestUrl.startsWith(WebServiceConstants.Search.SEARCH)) {

            loading = false;
            hideProgress();

            if (page == 1) {
                locations.clear();
            }

            page ++;

            JSONArray responseArray = (JSONArray) response;
            for (int i = 0; i < responseArray.length(); i++) {
                try {
                    AroundLocation location = new AroundLocation(responseArray.getJSONObject(i));
                    locations.add(location);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (responseArray.length() == 0) {
                lastPage = true;
            } else {
                lastPage = false;
            }

            if (page == 1) {
                adapter.notifyDataSetInvalidated();
            } else {
                adapter.notifyDataSetChanged();
            }

//            lstSearchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    AroundLocation location = (AroundLocation) adapter.getItem(position);
//                    switch (location.getType()) {
//                        case HOUSE:
//                            openHouseDetail(((AroundLocationDataHouse) location.getData()).getId());
//                            break;
//                        case CITY:
//                            openCityDetail(((AroundLocationDataCity) location.getData()).getId());
//                            break;
//                        case ATTRACTION:
//                            openAttractionDetail(((AroundLocationDataAttraction) location.getData()).getId());
//                            break;
//                        default:
//                            break;
//                    }
//                }
//            });

        }
    }

    @Override
    public void onError(String requestUrl, VolleyError error) {
        if (requestUrl.startsWith(WebServiceConstants.Search.SEARCH)) {
            showError();
        }
    }

    @Override
    public void onRefresh() {
        page = 1;
        startDefaultSearch(1);
    }
}