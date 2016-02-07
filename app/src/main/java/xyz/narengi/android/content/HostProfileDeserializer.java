package xyz.narengi.android.content;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import xyz.narengi.android.common.dto.GeoPoint;
import xyz.narengi.android.common.dto.Host;
import xyz.narengi.android.common.dto.HostProfile;
import xyz.narengi.android.common.dto.House;
import xyz.narengi.android.common.dto.HouseReview;

/**
 * @author Siavash Mahmoudpour
 */
public class HostProfileDeserializer implements JsonDeserializer<HostProfile> {
    @Override
    public HostProfile deserialize(JsonElement jsonElement, Type typeOF,
                                   JsonDeserializationContext context)
            throws JsonParseException {

        Gson gson = new GsonBuilder().create();

        HostProfile host = new HostProfile();
        JsonElement displayNameElement = jsonElement.getAsJsonObject().get("DisplayName");
        String displayName = displayNameElement.getAsString();
        host.setDisplayName(displayName);

        JsonElement imageUrlElement = jsonElement.getAsJsonObject().get("ImageUrl");
        String imageUrl = imageUrlElement.getAsString();
        host.setImageUrl(imageUrl);

        JsonElement jobElement = jsonElement.getAsJsonObject().get("Job");
        String job = jobElement.getAsString();
        host.setJob(job);

        JsonElement cellNumberElement = jsonElement.getAsJsonObject().get("CellNumber");
        String cellNumber = cellNumberElement.getAsString();
        host.setCellNumber(cellNumber);

        JsonElement housesElement = jsonElement.getAsJsonObject().get("Houses");
        House[] houses = gson.fromJson(housesElement, House[].class);
        host.setHouses(houses);

        JsonElement memberFromElement = jsonElement.getAsJsonObject().get("MemberFrom");
        String memberFrom = memberFromElement.getAsString();
        host.setMemberFrom(memberFrom);

        JsonElement locationTextElement = jsonElement.getAsJsonObject().get("LocationText");
        String locationText = locationTextElement.getAsString();
        host.setLocationText(locationText);

        JsonElement summaryElement = jsonElement.getAsJsonObject().get("Summary");
        String summary = summaryElement.getAsString();
        host.setSummary(summary);

        JsonElement reviewsElement = jsonElement.getAsJsonObject().get("reviews");
        HouseReview[] reviews = gson.fromJson(reviewsElement, HouseReview[].class);
        host.setReviews(reviews);

        return host;
    }
}
