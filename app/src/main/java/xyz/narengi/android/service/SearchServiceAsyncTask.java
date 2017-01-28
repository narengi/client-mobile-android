package xyz.narengi.android.service;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Retrofit;
import xyz.narengi.android.armin.model.network.RetrofitApiEndpoints;
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

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(AroundLocation.class, new AroundLocationDeserializer())
                .registerTypeAdapter(AroundPlaceCity.class, new AroundPlaceCityDeserializer())
                .registerTypeAdapter(AroundPlaceAttraction.class, new AroundPlaceAttractionDeserializer())
                .registerTypeAdapter(AroundPlaceHouse.class, new AroundPlaceHouseDeserializer())
                .create();

        Retrofit retrofit = RetrofitService.getInstance().getRetrofit();

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