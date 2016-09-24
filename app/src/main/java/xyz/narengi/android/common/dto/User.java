package xyz.narengi.android.common.dto;

import android.content.Context;
import android.text.TextUtils;

import xyz.narengi.android.util.SharedPref;

/**
 * @author Siavash Mahmoudpour
 */
public class User {

    private static final String USER_ID_SHARED_PREF_KEY = "user_id";
    private static final String USER_DISPLAY_NAME_SHARED_PREF_KEY = "user_name";
    private static final String USER_CELL_NUMBER_SHARED_PREF_KEY = "user_cell_number";
    private static final String USER_EMAIL_SHARED_PREF_KEY = "user_email";
    private static final String USER_AUTHORIZATION_TOKEN_SHARED_PREF_KEY = "user_authorization_token";

    private String id;
    private String displayName;
    private String cellNumber;
    private String email;
    private String authorizationToken;

    public User(String id, String displayName, String cellNumber, String email, String authorizationToken) {
        this.id = id;
        this.displayName = displayName;
        this.cellNumber = cellNumber;
        this.email = email;
        this.authorizationToken = authorizationToken;
    }

    public User() {
        this("", "", "", "", "");
    }

    public static User getLoggedInUser(Context context) {
        User result = new User();
        result.loadUserFromSharedPref(context);
        return result.isEmpty() ? null : result;
    }

    public String getID() {
        return id;
    }

    public void setID(String ID) {
        this.id = ID;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getCellNumber() {
        return cellNumber;
    }

    public void setCellNumber(String cellNumber) {
        this.cellNumber = cellNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }

    public void setAuthorizationToken(String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }

    public boolean isEmpty() {
        return TextUtils.isEmpty(this.id);
    }

    private void loadUserFromSharedPref(Context context) {
        SharedPref pref = SharedPref.getInstance(context);
        String id = pref.getString(USER_ID_SHARED_PREF_KEY);
        String displayName = pref.getString(USER_DISPLAY_NAME_SHARED_PREF_KEY);
        String cellNumber = pref.getString(USER_CELL_NUMBER_SHARED_PREF_KEY);
        String email = pref.getString(USER_EMAIL_SHARED_PREF_KEY);
        String authorizationToken = pref.getString(USER_AUTHORIZATION_TOKEN_SHARED_PREF_KEY);


        this.id = id;
        this.displayName = displayName;
        this.cellNumber = cellNumber;
        this.email = email;
        this.authorizationToken = authorizationToken;
    }


}
