package xyz.narengi.android.service;

import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sepehr Behroozi on 10/28/2016 AD.
 */

public class CustomJsonObjectRequestJsonObjectResponse extends JsonObjectRequest {

    private String token;

    public CustomJsonObjectRequestJsonObjectResponse(int method, String url, String token, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
        this.token = token;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();
        if (!TextUtils.isEmpty(token))
            headers.put("Authorization", "Bearer "+token);
        return headers;
    }
}
