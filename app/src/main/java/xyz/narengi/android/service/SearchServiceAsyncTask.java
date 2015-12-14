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

    private class AroundLocationDeserializer implements JsonDeserializer<AroundLocation> {
        @Override
        public AroundLocation deserialize(JsonElement jsonElement, Type typeOF,
                                          JsonDeserializationContext context)
                throws JsonParseException {
            Gson gson;

            JsonElement typeElement = jsonElement.getAsJsonObject().get("Type");

            String type = typeElement.getAsString();
            if ("House".equals(type)) {
                AroundLocation<AroundPlaceHouse> location = new AroundLocation<AroundPlaceHouse>();
                JsonElement dataElement = jsonElement.getAsJsonObject().get("Data");
//                gson = new GsonBuilder().registerTypeAdapter(AroundPlaceHouse.class, new AroundPlaceHouseDeserializer()).create();
                gson = new GsonBuilder().create();
                AroundPlaceHouse house = gson.fromJson(dataElement, AroundPlaceHouse.class);
                location.setType(type);
                location.setData(house);
                return location;
            } else if ("City".equals(type)) {

                AroundLocation<AroundPlaceCity> location = new AroundLocation<AroundPlaceCity>();
                JsonElement dataElement = jsonElement.getAsJsonObject().get("Data");
                gson = new GsonBuilder().create();
                AroundPlaceCity city = gson.fromJson(dataElement, AroundPlaceCity.class);
                location.setType(type);
                location.setData(city);
                return location;
            } else if ("Attraction".equals(type)) {

                AroundLocation<AroundPlaceAttraction> location = new AroundLocation<AroundPlaceAttraction>();
                JsonElement dataElement = jsonElement.getAsJsonObject().get("Data");
                gson = new GsonBuilder().create();
                AroundPlaceAttraction attraction = gson.fromJson(dataElement, AroundPlaceAttraction.class);
                location.setType(type);
                location.setData(attraction);
                return location;
            } else {
                gson = new Gson();
            }

            return gson.fromJson(jsonElement, AroundLocation.class);
        }
    }

    private class AroundPlaceAttractionDeserializer implements JsonDeserializer<AroundPlaceAttraction> {
        @Override
        public AroundPlaceAttraction deserialize(JsonElement jsonElement, Type typeOF,
                                                 JsonDeserializationContext context)
                throws JsonParseException {
            String userString = jsonElement.getAsString();
            JsonElement userJson = new JsonParser().parse(userString);

            return new Gson().fromJson(userJson, AroundPlaceAttraction.class);
        }
    }

    private class AroundPlaceCityDeserializer implements JsonDeserializer<AroundPlaceCity> {
        @Override
        public AroundPlaceCity deserialize(JsonElement jsonElement, Type typeOF,
                                           JsonDeserializationContext context)
                throws JsonParseException {
            String userString = jsonElement.getAsString();
            JsonElement userJson = new JsonParser().parse(userString);

            return new Gson().fromJson(userJson, AroundPlaceCity.class);
        }
    }

    private class AroundPlaceHouseDeserializer implements JsonDeserializer<AroundPlaceHouse> {
        @Override
        public AroundPlaceHouse deserialize(JsonElement jsonElement, Type typeOF,
                                            JsonDeserializationContext context)
                throws JsonParseException {
            String userString = jsonElement.getAsString();
            JsonElement userJson = new JsonParser().parse(userString);

            return new Gson().fromJson(userJson, AroundPlaceHouse.class);
        }
    }
}