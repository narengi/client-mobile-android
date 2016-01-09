package xyz.narengi.android.service;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import xyz.narengi.android.common.Constants;
import xyz.narengi.android.common.dto.AroundLocation;
import xyz.narengi.android.common.dto.AroundPlaceAttraction;
import xyz.narengi.android.common.dto.AroundPlaceCity;
import xyz.narengi.android.common.dto.AroundPlaceHouse;
import xyz.narengi.android.common.dto.SuggestionsResult;
import xyz.narengi.android.content.AroundLocationDeserializer;
import xyz.narengi.android.content.AroundPlaceAttractionDeserializer;
import xyz.narengi.android.content.AroundPlaceCityDeserializer;
import xyz.narengi.android.content.AroundPlaceHouseDeserializer;
import xyz.narengi.android.content.SuggestionsResultDeserializer;

/**
 * @author Siavash Mahmoudpour
 */
public class SuggestionsServiceAsyncTask extends AsyncTask {

    private String query;
    public SuggestionsServiceAsyncTask(String query) {
        this.query = query;
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(SuggestionsResult.class, new SuggestionsResultDeserializer()).create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.SERVER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);
        Call<SuggestionsResult> call = apiEndpoints.getAroundLocationSuggestions(query, "2", "2", "5");

        try {
            SuggestionsResult aroundLocationSuggestionsResult = call.execute().body();
            if (aroundLocationSuggestionsResult != null)
                return aroundLocationSuggestionsResult;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
