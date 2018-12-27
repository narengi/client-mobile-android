package xyz.narengi.android.common.dto;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

import xyz.narengi.android.util.SharedPref;

/**
 * @author Siavash Mahmoudpour
 */
public class AccountProfile implements Serializable {
    private static final String ACCOUNT_PROFILE_JSON_STRING_SHARED_PREF_KEY = "accountProfile";

    private static final String ID_JSON_KEY = "id";
    private static final String REGISTRATION_SOURCE_JSON_KEY = "registrationSource";
    private static final String USERNAME_SOURCE_JSON_KEY = "username";
    private static final String CELL_NUMBER_JSON_KEY = "cellNumber";
    private static final String DISPLAY_NAME_JSON_KEY = "displayName";
    private static final String CREATED_AT_JSON_KEY = "createdAt";
    private static final String UPDATED_AT_JSON_KEY = "updatedAt";
    private static final String ENABLED_JSON_KEY = "enabled";
    private static final String LAST_LOGIN_DATE_JSON_KEY = "lastLoginDate";
    private static final String EMAIL_JSON_KEY = "email";
    private static final String PROFILE_JSON_KEY = "profile";
    private static final String TOKEN_JSON_KEY = "token";
    private static final String VERIFICATION_JSON_KEY = "verifications";
    private static final String AVATAR_JSON_KEY = "avatar";
    private static AccountProfile loggedInAccountProfile;
    private String registrationSource;
    private String id;
    private String cellNumber;
    private String displayName;
    private String createdAt;
    private String updatedAt;
    private boolean enabled;
    private String lastLoginDate;
    private String email;
    private String username;
    private String avatar;
    private Profile profile;
    private AccessToken token;
    private List<AccountVerification> verifications;

    public static AccountProfile fromJsonObject(JSONObject object) {
        if (object == null)
            return null;
        AccountProfile result = new AccountProfile();
        try {
            result.id = object.isNull(ID_JSON_KEY) ? "" : object.getString(ID_JSON_KEY);
            result.username = object.isNull(USERNAME_SOURCE_JSON_KEY) ? "" : object.getString(USERNAME_SOURCE_JSON_KEY);
            result.avatar = object.isNull(AVATAR_JSON_KEY) ? "" : object.getString(AVATAR_JSON_KEY);
            result.email = object.isNull(EMAIL_JSON_KEY) ? "" : object.getString(EMAIL_JSON_KEY);
            result.displayName = object.isNull(DISPLAY_NAME_JSON_KEY) ? "" : object.getString(DISPLAY_NAME_JSON_KEY);
            result.cellNumber = object.isNull(CELL_NUMBER_JSON_KEY) ? "" : object.getString(CELL_NUMBER_JSON_KEY);
            result.enabled = !object.isNull(ENABLED_JSON_KEY) && object.getBoolean(ENABLED_JSON_KEY);
            result.lastLoginDate = object.isNull(LAST_LOGIN_DATE_JSON_KEY) ? "" : object.getString(LAST_LOGIN_DATE_JSON_KEY);
            result.registrationSource = object.isNull(REGISTRATION_SOURCE_JSON_KEY) ? "" : object.getString(REGISTRATION_SOURCE_JSON_KEY);
            result.createdAt = object.isNull(CREATED_AT_JSON_KEY) ? "" : object.getString(CREATED_AT_JSON_KEY);
            result.updatedAt = object.isNull(UPDATED_AT_JSON_KEY) ? "" : object.getString(UPDATED_AT_JSON_KEY);
            result.profile = Profile.fromJsonObject(object.isNull(PROFILE_JSON_KEY) ? new JSONObject() : object.getJSONObject(PROFILE_JSON_KEY));
            result.token = AccessToken.fromJsonObject(object.isNull(TOKEN_JSON_KEY) ? new JSONObject() : object.getJSONObject(TOKEN_JSON_KEY));
            result.verifications = AccountVerification.fromJsonArray(object.isNull(VERIFICATION_JSON_KEY) ? new JSONArray() : object.getJSONArray(VERIFICATION_JSON_KEY));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static AccountProfile getLoggedInAccountProfile(Context context) {
        if (loggedInAccountProfile != null)
            return loggedInAccountProfile;
        SharedPref pref = SharedPref.getInstance(context);
        String accountProfileJsonString = pref.getString(ACCOUNT_PROFILE_JSON_STRING_SHARED_PREF_KEY, "");
        try {
            loggedInAccountProfile = AccountProfile.fromJsonObject(new JSONObject(accountProfileJsonString));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return loggedInAccountProfile;
    }

    public static void logout(Context context) {
        loggedInAccountProfile = null;
        SharedPref pref = SharedPref.getInstance(context);
        pref.remove(ACCOUNT_PROFILE_JSON_STRING_SHARED_PREF_KEY);
    }

    public JSONObject toJsonObject() {
        JSONObject result = new JSONObject();
        try {
            result.put(ID_JSON_KEY, id == null ? JSONObject.NULL : id);
            result.put(USERNAME_SOURCE_JSON_KEY, username == null ? JSONObject.NULL : username);
            result.put(AVATAR_JSON_KEY, avatar == null ? JSONObject.NULL : avatar);
            result.put(EMAIL_JSON_KEY, email == null ? JSONObject.NULL : email);
            result.put(DISPLAY_NAME_JSON_KEY, displayName == null ? JSONObject.NULL : displayName);
            result.put(CELL_NUMBER_JSON_KEY, cellNumber == null ? JSONObject.NULL : cellNumber);
            result.put(ENABLED_JSON_KEY, enabled);
            result.put(LAST_LOGIN_DATE_JSON_KEY, lastLoginDate == null ? JSONObject.NULL : lastLoginDate);
            result.put(REGISTRATION_SOURCE_JSON_KEY, registrationSource == null ? JSONObject.NULL : registrationSource);
            result.put(CREATED_AT_JSON_KEY, createdAt == null ? JSONObject.NULL : createdAt);
            result.put(UPDATED_AT_JSON_KEY, updatedAt == null ? JSONObject.NULL : updatedAt);
            result.put(PROFILE_JSON_KEY, profile == null ? JSONObject.NULL : profile.toJsonObject());
            result.put(TOKEN_JSON_KEY, token == null ? JSONObject.NULL : token.toJsonObject());
            JSONArray verificationArray = AccountVerification.toJsonArray(verifications);
            result.put(VERIFICATION_JSON_KEY, verificationArray == null ? JSONObject.NULL : verificationArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public void saveToSharedPref(Context context) {
        if (loggedInAccountProfile != null)
            setToken(loggedInAccountProfile.getToken());
        SharedPref pref = SharedPref.getInstance(context);
        String accountJsonString = toJsonObject().toString();
        pref.save(ACCOUNT_PROFILE_JSON_STRING_SHARED_PREF_KEY, accountJsonString);
        loggedInAccountProfile = this;
    }

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

    public List<AccountVerification> getVerifications() {
        return verifications;
    }

    public void setVerifications(List<AccountVerification> verifications) {
        this.verifications = verifications;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
