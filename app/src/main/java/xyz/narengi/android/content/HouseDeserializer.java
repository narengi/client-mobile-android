package xyz.narengi.android.content;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.List;

import xyz.narengi.android.common.dto.GeoPoint;
import xyz.narengi.android.common.dto.Host;
import xyz.narengi.android.common.dto.House;
import xyz.narengi.android.common.dto.HouseFeature;
import xyz.narengi.android.common.dto.HouseReview;
import xyz.narengi.android.common.dto.Location;

/**
 * @author Siavash Mahmoudpour
 */
public class HouseDeserializer implements JsonDeserializer<House> {
    @Override
    public House deserialize(JsonElement jsonElement, Type typeOF,
                            JsonDeserializationContext context)
            throws JsonParseException {
        Gson gson = new GsonBuilder().create();

        House house = new House();
        JsonElement nameElement = jsonElement.getAsJsonObject().get("Name");
        String name = nameElement.getAsString();
        house.setName(name);

        JsonElement imagesElement = jsonElement.getAsJsonObject().get("Images");
//        List<String> images = gson.fromJson(imagesElement, List[].class);
//        house.setImages(images);

        JsonElement costElement = jsonElement.getAsJsonObject().get("Cost");
        String cost = costElement.getAsString();
        house.setCost(cost);

        JsonElement positionElement = jsonElement.getAsJsonObject().get("Position");
        GeoPoint position = gson.fromJson(positionElement, GeoPoint.class);
        house.setPosition(position);

        JsonElement hostElement = jsonElement.getAsJsonObject().get("Host");
        Host host = gson.fromJson(hostElement, Host.class);
        house.setHost(host);

        JsonElement ratingElement = jsonElement.getAsJsonObject().get("Rating");
        String rating = ratingElement.getAsString();
        house.setRating(rating);

        JsonElement summaryElement = jsonElement.getAsJsonObject().get("Summary");
        String summary = summaryElement.getAsString();
        house.setSummary(summary);

        JsonElement cityNameElement = jsonElement.getAsJsonObject().get("CityName");
        String cityName = cityNameElement.getAsString();
        Location location = new Location();
        location.setCity(cityName);
        house.setLocation(location);

        JsonElement urlElement = jsonElement.getAsJsonObject().get("URL");
        String url = urlElement.getAsString();
        house.setDetailUrl(url);

        JsonElement featureSummaryElement = jsonElement.getAsJsonObject().get("featureSummary");
        String featureSummary = featureSummaryElement.getAsString();
        house.setFeatureSummary(featureSummary);

        JsonElement reviewsElement = jsonElement.getAsJsonObject().get("Reviews");
        HouseReview[] reviews = gson.fromJson(reviewsElement, HouseReview[].class);
        house.setReviews(reviews);

        JsonElement featureListElement = jsonElement.getAsJsonObject().get("FeatureList");
        HouseFeature[] featureList = gson.fromJson(featureListElement, HouseFeature[].class);
        house.setFeatureList(featureList);

        JsonElement typeElement = jsonElement.getAsJsonObject().get("type");
        String type = typeElement.getAsString();
//        house.setType(type);

//        JsonElement bedroomCountElement = jsonElement.getAsJsonObject().get("bedroomCount");
//        int bedroomCount = bedroomCountElement.getAsInt();
//        house.setBedroomCount(bedroomCount);
//
//        JsonElement guestCountElement = jsonElement.getAsJsonObject().get("guestCount");
//        int guestCount = guestCountElement.getAsInt();
//        house.setGuestCount(guestCount);
//
//        JsonElement bedCountElement = jsonElement.getAsJsonObject().get("bedCount");
//        int bedCount = bedCountElement.getAsInt();
//        house.setBedCount(bedCount);

        return house;
    }
}
