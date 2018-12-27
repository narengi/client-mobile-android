package xyz.narengi.android.content;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import xyz.narengi.android.common.dto.AroundLocation;
import xyz.narengi.android.common.dto.AroundPlaceAttraction;
import xyz.narengi.android.common.dto.AroundPlaceCity;
import xyz.narengi.android.common.dto.AroundPlaceHouse;

/**
 * @author Siavash Mahmoudpour
 */
public class AroundLocationDeserializer implements JsonDeserializer<AroundLocation> {
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
