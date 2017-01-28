package xyz.narengi.android.service;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Retrofit;
import xyz.narengi.android.armin.model.network.RetrofitApiEndpoints;
import xyz.narengi.android.common.dto.SuggestionsResult;
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

        Retrofit retrofit = RetrofitService.getInstance().getRetrofit();

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
