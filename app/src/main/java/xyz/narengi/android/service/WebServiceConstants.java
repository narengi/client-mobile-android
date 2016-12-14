package xyz.narengi.android.service;

/**
 * Created by Sepehr Behroozi on 10/28/2016 AD.
 */

public class WebServiceConstants {
    public static final String HOST_NAME = "https://api.narengi.xyz";

    public static class Accounts {
        public static final String LOGIN = HOST_NAME + "/v1/accounts/login";
        public static final String REGISTER = HOST_NAME + "/v1/accounts/register";
        public static final java.lang.String ME = HOST_NAME + "/v1/accounts/me";
    }

    public static class ProvinceCity {
        public static final String GET_PROVINCE_CITIES = HOST_NAME + "/v1/settings/provinces";
    }

    public static class Search {
        public static final String SEARCH = HOST_NAME + "/v1/search";
        public static final String FILTER_LIMIT_QUERY_KEY = "filter[limit]";
        public static final String FILTER_SKIP_QUERY_KEY = "filter[skip]";
        public static final String TERM_QUERY_KEY = "term";
        public static final String POSITION_LAT_QUERY_KEY = "position[lat]";
        public static final String POSITION_LNG_QUERY_KEY = "position[lng]";
    }

    public static class City {
        public static final String CITY_DETAILS_API_FORMAT = HOST_NAME + "/v1/cities/%s";
    }

    public static class Attraction {
        public static final String ATTRACTION_DETAILS_API_FORMAT = HOST_NAME + "/v1/attractions/%s";
    }

    public static class House {
        public static final String HOUSE_DETAILS_API_FORMAT = HOST_NAME + "/v1/houses/%s";
    }
}
