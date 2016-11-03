package xyz.narengi.android.common.dto;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * @author Siavash Mahmoudpour
 */
public class Profile implements Serializable {

    public static final String FIRST_NAME_JSON_KEY = "firstName";
    public static final String LAST_NAME_JSON_KEY = "lastName";
    public static final String GENDER_JSON_KEY = "gender";
    public static final String PROVINCE_JSON_KEY = "province";
    public static final String CITY_JSON_KEY = "city";
    public static final String BIRTHDAY_JSON_KEY = "birthday";
    public static final String BIO_JSON_KEY = "bio";
    public static final String AVATAR_JSON_KEY = "avatar";
    public static final String STATUS_JSON_KEY = "status";

    private String gender;
    private String firstName;
    private String lastName;
    private String province;
    private String city;
    private String birthDate;
    private String bio;
    private Status status;
    private String avatar;

    public static Profile fromJsonObject(JSONObject object) {
        if (object == null)
            return null;
        Profile result = new Profile();
        try {
            result.firstName = object.isNull(FIRST_NAME_JSON_KEY) ? "" : object.getString(FIRST_NAME_JSON_KEY);
            result.lastName = object.isNull(LAST_NAME_JSON_KEY) ? "" : object.getString(LAST_NAME_JSON_KEY);
            result.gender = object.isNull(GENDER_JSON_KEY) ? "" : object.getString(GENDER_JSON_KEY);
            result.province = object.isNull(PROVINCE_JSON_KEY) ? "" : object.getString(PROVINCE_JSON_KEY);
            result.city = object.isNull(CITY_JSON_KEY) ? "" : object.getString(CITY_JSON_KEY);
            result.birthDate = object.isNull(BIRTHDAY_JSON_KEY) ? "" : object.getString(BIRTHDAY_JSON_KEY);
            result.bio = object.isNull(BIO_JSON_KEY) ? "" : object.getString(BIO_JSON_KEY);
            result.avatar = object.isNull(AVATAR_JSON_KEY) ? "" : object.getString(AVATAR_JSON_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public JSONObject toJsonObject() {
        JSONObject result = new JSONObject();
        try {
            result.put(FIRST_NAME_JSON_KEY, firstName == null ? JSONObject.NULL : firstName);
            result.put(LAST_NAME_JSON_KEY, lastName == null ? JSONObject.NULL : lastName);
            result.put(GENDER_JSON_KEY, gender == null ? JSONObject.NULL : gender);
            result.put(PROVINCE_JSON_KEY, province == null ? JSONObject.NULL : province);
            result.put(CITY_JSON_KEY, city == null ? JSONObject.NULL : city);
            result.put(BIRTHDAY_JSON_KEY, birthDate == null ? JSONObject.NULL : birthDate);
            result.put(BIO_JSON_KEY, bio == null ? JSONObject.NULL : bio);
            result.put(AVATAR_JSON_KEY, avatar == null ? JSONObject.NULL : avatar);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public class Status implements Serializable {
        private int completed;
        private String[] fields;

        public int getCompleted() {
            return completed;
        }

        public void setCompleted(int completed) {
            this.completed = completed;
        }

        public String[] getFields() {
            return fields;
        }

        public void setFields(String[] fields) {
            this.fields = fields;
        }
    }
}
