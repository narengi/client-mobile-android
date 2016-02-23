package xyz.narengi.android.content;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import xyz.narengi.android.common.dto.Credential;

/**
 * @author Siavash Mahmoudpour
 */
public class CredentialDeserializer implements JsonDeserializer<Credential> {
    @Override
    public Credential deserialize(JsonElement jsonElement, Type typeOF,
                             JsonDeserializationContext context)
            throws JsonParseException {
        Gson gson = new GsonBuilder().create();

        Credential credential = new Credential();
        JsonElement emailElement = jsonElement.getAsJsonObject().get("email");
        String email = emailElement.getAsString();
        credential.setEmail(email);

        JsonElement displayNameElement = jsonElement.getAsJsonObject().get("displayName");
        String displayName = displayNameElement.getAsString();
        credential.setDisplayName(displayName);

        JsonElement cellNumberElement = jsonElement.getAsJsonObject().get("cellNumber");
        String cellNumber = cellNumberElement.getAsString();
        credential.setCellNumber(cellNumber);

        JsonElement usernameElement = jsonElement.getAsJsonObject().get("username");
        String username = usernameElement.getAsString();
        credential.setUsername(username);

        JsonElement passwordElement = jsonElement.getAsJsonObject().get("password");
        String password = passwordElement.getAsString();
        credential.setPassword(password);

        return credential;
    }
}
