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
import xyz.narengi.android.common.dto.City;
import xyz.narengi.android.common.dto.GeoPoint;

/**
 * @author Siavash Mahmoudpour
 */
public class CityDeserializer implements JsonDeserializer<City> {
    @Override
    public City deserialize(JsonElement jsonElement, Type typeOF,
                                         JsonDeserializationContext context)
            throws JsonParseException {
        Gson gson = new GsonBuilder().create();

        City city = new City();
        JsonElement nameElement = jsonElement.getAsJsonObject().get("Name");
        String name = nameElement.getAsString();
        city.setName(name);

        JsonElement imagesElement = jsonElement.getAsJsonObject().get("Images");
        String[] images = gson.fromJson(imagesElement, String[].class);
        city.setImages(images);

        JsonElement positionElement = jsonElement.getAsJsonObject().get("Position");
        GeoPoint position = gson.fromJson(positionElement, GeoPoint.class);
        city.setPosition(position);


        JsonElement summaryElement = jsonElement.getAsJsonObject().get("Summary");
        String summary = summaryElement.getAsString();
        city.setSummary(summary);

        JsonElement houseCountElement = jsonElement.getAsJsonObject().get("HouseCount");
        int houseCount = houseCountElement.getAsInt();
        city.setHouseCount(houseCount);

        JsonElement houseCountTextElement = jsonElement.getAsJsonObject().get("HouseCountText");
        String houseCountText = houseCountTextElement.getAsString();
        city.setHouseCountText(houseCountText);

        JsonElement urlElement = jsonElement.getAsJsonObject().get("URL");
        String url = urlElement.getAsString();
        city.setURL(url);

        JsonElement attractionsElement = jsonElement.getAsJsonObject().get("Attractions");
        AroundPlaceAttraction[] attractions = gson.fromJson(attractionsElement, AroundPlaceAttraction[].class);
        city.setAttractions(attractions);

        JsonElement housesElement = jsonElement.getAsJsonObject().get("Houses");
        AroundPlaceHouse[] houses = gson.fromJson(housesElement, AroundPlaceHouse[].class);
        city.setHouses(houses);

        return city;
    }
}
