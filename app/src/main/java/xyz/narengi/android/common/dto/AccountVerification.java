package xyz.narengi.android.common.dto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Siavash Mahmoudpour
 */
public class AccountVerification implements Serializable {

    public static final String VERIFICATION_TYPE_JSON_KEY = "verificationType";
    public static final String REQUEST_DATE_JSON_KEY = "requestDate";
    public static final String CODE_JSON_KEY = "code"; // FIXME: 10/28/2016 AD
    public static final String VERIFIED_JSON_KEY = "verified";
    public static final String HANDLE_JSON_KEY = "handle"; // FIXME: 10/28/2016 AD

    private String verificationType;
    private String requestDate;
    private String code;
    private boolean verified;
    private String handle;

    public static List<AccountVerification> fromJsonArray(JSONArray jsonArray) {
        if (jsonArray == null)
            return null;
        List<AccountVerification> result = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject verificationObject = jsonArray.getJSONObject(i);
                AccountVerification verification = AccountVerification.fromJsonObject(verificationObject);
                result.add(verification);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private static AccountVerification fromJsonObject(JSONObject object) {
        if (object == null)
            return null;
        AccountVerification result = new AccountVerification();
        try {
            result.code = object.isNull(CODE_JSON_KEY) ? "" : object.getString(CODE_JSON_KEY);
            result.handle = object.isNull(HANDLE_JSON_KEY) ? "" : object.getString(HANDLE_JSON_KEY);
            result.verificationType = object.isNull(VERIFICATION_TYPE_JSON_KEY) ? "" : object.getString(VERIFICATION_TYPE_JSON_KEY);
            result.verified = !object.isNull(VERIFIED_JSON_KEY) && object.getBoolean(VERIFIED_JSON_KEY);
            result.requestDate = object.isNull(REQUEST_DATE_JSON_KEY) ? "" : object.getString(REQUEST_DATE_JSON_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static JSONArray toJsonArray(List<AccountVerification> list) {
        if (list == null)
            return null;
        JSONArray result = new JSONArray();
        for (AccountVerification verification : list)
            result.put(verification.toJsonObject());
        return result;
    }

    public JSONObject toJsonObject() {
        JSONObject result = new JSONObject();
        try {
            result.put(CODE_JSON_KEY, code == null ? JSONObject.NULL : code);
            result.put(HANDLE_JSON_KEY, handle == null ? JSONObject.NULL : handle);
            result.put(VERIFICATION_TYPE_JSON_KEY, verificationType == null ? JSONObject.NULL : verificationType);
            result.put(VERIFIED_JSON_KEY, verified);
            result.put(REQUEST_DATE_JSON_KEY, requestDate == null ? JSONObject.NULL : requestDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public String getVerificationType() {
        return verificationType;
    }

    public void setVerificationType(String verificationType) {
        this.verificationType = verificationType;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }
}
