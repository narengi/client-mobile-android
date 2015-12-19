package xyz.narengi.android.service;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.lang.reflect.Type;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import xyz.narengi.android.common.dto.AroundLocation;
import xyz.narengi.android.common.dto.AroundPlaceAttraction;
import xyz.narengi.android.common.dto.AroundPlaceCity;
import xyz.narengi.android.common.dto.AroundPlaceHouse;
import xyz.narengi.android.content.AroundLocationDeserializer;
import xyz.narengi.android.content.AroundPlaceAttractionDeserializer;
import xyz.narengi.android.content.AroundPlaceCityDeserializer;
import xyz.narengi.android.content.AroundPlaceHouseDeserializer;

/**
 * @author Siavash Mahmoudpour
 */
public class SearchServiceAsyncTask extends AsyncTask {

    private String query;
    public SearchServiceAsyncTask(String query) {
        this.query = query;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        String BASE_URL = "http://149.202.20.233:3500";

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(AroundLocation.class, new AroundLocationDeserializer())
                .registerTypeAdapter(AroundPlaceCity.class, new AroundPlaceCityDeserializer())
                .registerTypeAdapter(AroundPlaceAttraction.class, new AroundPlaceAttractionDeserializer())
                .registerTypeAdapter(AroundPlaceHouse.class, new AroundPlaceHouseDeserializer())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);
        Call<AroundLocation[]> call = apiEndpoints.getAroundLocations(query, "30", "0");

        try {
            AroundLocation[] aroundLocations = call.execute().body();
            if (aroundLocations != null)
                return aroundLocations;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}