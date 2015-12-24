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
import xyz.narengi.android.common.dto.SuggestionsResult;

/**
 * @author Siavash Mahmoudpour
 */
public class SuggestionsResultDeserializer implements JsonDeserializer<SuggestionsResult> {
    @Override
    public SuggestionsResult deserialize(JsonElement jsonElement, Type typeOF,
                                      JsonDeserializationContext context)
            throws JsonParseException {
        Gson gson = new GsonBuilder().create();

        SuggestionsResult suggestionsResult = new SuggestionsResult();
        JsonElement cityElement = jsonElement.getAsJsonObject().get("city");
        AroundPlaceCity[] cityArray = gson.fromJson(cityElement, AroundPlaceCity[].class);
        suggestionsResult.setCity(cityArray);

        JsonElement attractionElement = jsonElement.getAsJsonObject().get("attraction");
        AroundPlaceAttraction[] attractionArray = gson.fromJson(attractionElement, AroundPlaceAttraction[].class);
        suggestionsResult.setAttraction(attractionArray);

        JsonElement houseElement = jsonElement.getAsJsonObject().get("house");
        AroundPlaceHouse[] houseArray = gson.fromJson(houseElement, AroundPlaceHouse[].class);
        suggestionsResult.setHouse(houseArray);

        return suggestionsResult;
    }
}