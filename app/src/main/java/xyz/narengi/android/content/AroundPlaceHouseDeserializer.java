package xyz.narengi.android.content;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.lang.reflect.Type;

import xyz.narengi.android.common.dto.AroundPlaceHouse;

/**
 * @author Siavash Mahmoudpour
 */
public class AroundPlaceHouseDeserializer  implements JsonDeserializer<AroundPlaceHouse> {
    @Override
    public AroundPlaceHouse deserialize(JsonElement jsonElement, Type typeOF,
                                        JsonDeserializationContext context)
            throws JsonParseException {
        String userString = jsonElement.getAsString();
        JsonElement userJson = new JsonParser().parse(userString);

        return new Gson().fromJson(userJson, AroundPlaceHouse.class);
    }
}
