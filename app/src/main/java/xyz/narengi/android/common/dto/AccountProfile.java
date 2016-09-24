package xyz.narengi.android.common.dto;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.io.Serializable;

import xyz.narengi.android.ui.NarengiApplication;
import xyz.narengi.android.util.SharedPref;

/**
 * @author Siavash Mahmoudpour
 */
public class AccountProfile implements Serializable {
    private static final String REGISTRATION_SOURCE_PREF_KEY = "registration_source";
    private static final String CELL_NUMBER_PREF_KEY = "cell_number";
    private static final String DISPLAY_NAME_PREF_KEY = "display_name";
    private static final String CREATED_AT_PREF_KEY = "created_at";
    private static final String ENABLED_PREF_KEY = "enabled";
    private static final String LAST_LOGIN_DATE_PREF_KEY = "last_login_date";
    private static final String EMAIL_PREF_KEY = "email";
    private static final String PROFILE_PREF_KEY = "profile";
    private static final String TOKEN_PREF_KEY = "token";
    private static final String VERIFICATION_PREF_KEY = "verification";

    private String registrationSource;
    private String cellNumber;
    private String displayName;
    private String createdAt;
    private boolean enabled;
    private String lastLoginDate;
    private String email;
    private Profile profile;
    private AccessToken token;
    private AccountVerification[] verification;


    public String getRegistrationSource() {
        return registrationSource;
    }

    public void setRegistrationSource(String registrationSource) {
        this.registrationSource = registrationSource;
    }

    public String getCellNumber() {
        return cellNumber;
    }

    public void setCellNumber(String cellNumber) {
        this.cellNumber = cellNumber;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(String lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public AccessToken getToken() {
        return token;
    }

    public void setToken(AccessToken token) {
        this.token = token;
    }

    public AccountVerification[] getVerification() {
        return verification;
    }

    public void setVerification(AccountVerification[] verification) {
        this.verification = verification;
    }

    public String getTokenString() {
        return token == null ? "" : TextUtils.isEmpty(token.getToken()) ? "" : token.getToken();
    }

    public static AccountProfile getLoggedInProfile(Context context) {
        return null;
        // TODO: 9/22/2016 AD implement
    }
}
