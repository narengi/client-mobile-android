package xyz.narengi.android;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

/**
 * Created by Sepehr Behroozi on 10/28/2016 AD.
 */

@RunWith(JUnit4.class)
public class JavaTest {

    @Test
    public void test() {
        String url = "http://narengi.xyz/api/search";
        JSONObject params = new JSONObject();
        try {
            params.put("q", "salam");
            params.put("area", "tehran");
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

        assertEquals("http://narengi.xyz/api/search?q=salam&area=tehran", parametrizedUrl);
    }
}
