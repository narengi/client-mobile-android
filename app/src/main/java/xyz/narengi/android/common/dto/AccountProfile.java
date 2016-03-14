package xyz.narengi.android.common.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Siavash Mahmoudpour
 */
public class AccountProfile implements Serializable {

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
}
