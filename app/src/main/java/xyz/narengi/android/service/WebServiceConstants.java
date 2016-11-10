package xyz.narengi.android.service;

/**
 * Created by Sepehr Behroozi on 10/28/2016 AD.
 */

public class WebServiceConstants {
    public static final String HOST_NAME = "http://api.narengi.xyz";

    public static class Accounts {
        public static final String LOGIN = HOST_NAME + "/api/accounts/login";
        public static final String REGISTER = HOST_NAME + "/api/accounts/register";
        public static final java.lang.String ME = HOST_NAME + "/api/accounts/me";
    }

    public static class ProvinceCity {
        public static final String GET_PROVINCE_CITIES = HOST_NAME + "/api/settings/provinces";
    }
}
