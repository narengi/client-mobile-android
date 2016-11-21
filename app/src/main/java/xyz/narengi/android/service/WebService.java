package xyz.narengi.android.service;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Locale;

/**
 * Created by Sepehr Behroozi on 10/28/2016 AD.
 */

public class WebService {
    private static final String REQUEST_LOG_KEY = "RequestLog";
    private RequestQueue mRequestQueue;
    private ResponseHandler mainResponseHandler;
    private String token;

    public WebService() {
        mRequestQueue = VolleySingleton.getInstance().getRequestQueue();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setResponseHandler(ResponseHandler responseHandler) {
        this.mainResponseHandler = responseHandler;
    }


    public void getJsonObject(final String url) {
        if (mainResponseHandler != null)
            mainResponseHandler.onPreRequest(url);

        Request request;

        //log url
        Log.d(REQUEST_LOG_KEY, "GET : " + url);

        request = new CustomJsonObjectRequestJsonObjectResponse(Request.Method.GET, url, token, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    Log.d(REQUEST_LOG_KEY, "Response : " + response.toString());
                }
                if (mainResponseHandler != null)
                    mainResponseHandler.onSuccess(url, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null) {
                    Log.d(REQUEST_LOG_KEY, "ERROR : " + error.toString());
                }
                if (mainResponseHandler != null)
                    mainResponseHandler.onError(url, error);
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);
    }

    public void getJsonObject(String url, JSONObject params) {
        String parametrizedUrl = url;
        if (params != null && params.length() > 0) {
            boolean firstTime = true;
            if (!parametrizedUrl.contains("?"))
                firstTime = false;
            Iterator<String> keys = params.keys();
            while (keys.hasNext()) {
                try {
                    String key = keys.next();
                    String value = params.getString(key);
                    parametrizedUrl += String.format(Locale.ENGLISH, "%s%s=%s", firstTime ? "?" : "&", key, URLEncoder.encode(value, "utf-8"));
                } catch (JSONException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        getJsonObject(parametrizedUrl);
    }

    public void getJsonArray(String url, JSONObject params) {
        String parametrizedUrl = url;
        if (params != null && params.length() > 0) {
            boolean firstTime = true;
            if (parametrizedUrl.contains("?"))
                firstTime = false;
            Iterator<String> keys = params.keys();
            while (keys.hasNext()) {
                try {
                    String key = keys.next();
                    String value = params.getString(key);
                    parametrizedUrl += String.format(Locale.ENGLISH, "%s%s=%s", firstTime ? "?" : "&", key, URLEncoder.encode(value, "utf-8"));
                    firstTime = false;
                } catch (JSONException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        getJsonArray(parametrizedUrl);
    }

    public void getJsonArray(final String url) {
        if (mainResponseHandler != null)
            mainResponseHandler.onPreRequest(url);

        Request request;

        //log url
        Log.d(REQUEST_LOG_KEY, "GET : " + url);

        request = new CustomJsonObjectRequestJsonArrayResponse(Request.Method.GET, url, token, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response != null) {
                    Log.d(REQUEST_LOG_KEY, "Response : " + response.toString());
                }
                if (mainResponseHandler != null)
                    mainResponseHandler.onSuccess(url, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null) {
                    Log.d(REQUEST_LOG_KEY, "ERROR : " + error.toString());
                }
                if (mainResponseHandler != null)
                    mainResponseHandler.onError(url, error);
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);
    }

    public void postJsonObject(final String url, JSONObject params) {
        if (mainResponseHandler != null)
            mainResponseHandler.onPreRequest(url);

        Request request = null;

        //log url
        Log.d(REQUEST_LOG_KEY, "POST : " + url);
        if (params != null)
            Log.d(REQUEST_LOG_KEY, "POST - params: " + params.toString());

        request = new CustomJsonObjectRequestJsonObjectResponse(Request.Method.POST, url, token, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    Log.d(REQUEST_LOG_KEY, "Response : " + response.toString());
                }
                if (mainResponseHandler != null)
                    mainResponseHandler.onSuccess(url, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null) {
                    Log.d(REQUEST_LOG_KEY, "ERROR : " + error.toString());
                }
                if (mainResponseHandler != null)
                    mainResponseHandler.onError(url, error);
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);
    }

    public interface ResponseHandler {
        void onPreRequest(String requestUrl);

        void onSuccess(String requestUrl, Object response);

        void onError(String requestUrl, VolleyError error);
    }

    public abstract class SimpleResponseHandler implements ResponseHandler {

        @Override
        public void onPreRequest(String requestUrl) {

        }

        @Override
        public void onSuccess(String requestUrl, Object response) {

        }

        @Override
        public void onError(String requestUrl, VolleyError error) {

        }
    }

}
