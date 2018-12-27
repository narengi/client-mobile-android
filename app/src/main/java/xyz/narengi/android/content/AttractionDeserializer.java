package xyz.narengi.android.content;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import xyz.narengi.android.common.dto.AroundPlaceAttraction;
import xyz.narengi.android.common.dto.AroundPlaceHouse;
import xyz.narengi.android.common.dto.Attraction;
import xyz.narengi.android.common.dto.GeoPoint;
import xyz.narengi.android.common.dto.House;

/**
 * @author Siavash Mahmoudpour
 */
public class AttractionDeserializer implements JsonDeserializer<Attraction> {
    @Override
    public Attraction deserialize(JsonElement jsonElement, Type typeOF,
                            JsonDeserializationContext context)
            throws JsonParseException {
        Gson gson = new GsonBuilder().create();

        Attraction attraction = new Attraction();
        JsonElement nameElement = jsonElement.getAsJsonObject().get("Name");
        String name = nameElement.getAsString();
        attraction.setName(name);

        JsonElement imagesElement = jsonElement.getAsJsonObject().get("Images");
        String[] images = gson.fromJson(imagesElement, String[].class);
        attraction.setImages(images);

        JsonElement cityNameElement = jsonElement.getAsJsonObject().get("CityName");
        String cityName = cityNameElement.getAsString();
        attraction.setCityName(cityName);

        JsonElement aroundHousesTextElement = jsonElement.getAsJsonObject().get("AroundHousesText");
        String aroundHousesText = aroundHousesTextElement.getAsString();
        attraction.setAroundHousesText(aroundHousesText);

        JsonElement aroundHousesElement = jsonElement.getAsJsonObject().get("AroundHouses");
        AroundPlaceHouse[] aroundHouses = gson.fromJson(aroundHousesElement, AroundPlaceHouse[].class);
        attraction.setAroundHouses(aroundHouses);

        JsonElement positionElement = jsonElement.getAsJsonObject().get("Position");
        GeoPoint position = gson.fromJson(positionElement, GeoPoint.class);
        attraction.setPosition(position);

        JsonElement urlElement = jsonElement.getAsJsonObject().get("URL");
        String url = urlElement.getAsString();
        attraction.setURL(url);

        JsonElement housesUrlElement = jsonElement.getAsJsonObject().get("HousesUrl");
        String housesUrl = housesUrlElement.getAsString();
        attraction.setHousesUrl(housesUrl);

        JsonElement housesElement = jsonElement.getAsJsonObject().get("Houses");
        AroundPlaceHouse[] houses = gson.fromJson(housesElement, AroundPlaceHouse[].class);
        attraction.setHouses(houses);

        return attraction;
    }
}
