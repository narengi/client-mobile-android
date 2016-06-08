package xyz.narengi.android.service;

import com.squareup.okhttp.RequestBody;

import java.util.Map;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.Url;
import xyz.narengi.android.common.dto.AccountProfile;
import xyz.narengi.android.common.dto.AccountVerification;
import xyz.narengi.android.common.dto.AroundLocation;
import xyz.narengi.android.common.dto.Attraction;
import xyz.narengi.android.common.dto.City;
import xyz.narengi.android.common.dto.Credential;
import xyz.narengi.android.common.dto.HostProfile;
import xyz.narengi.android.common.dto.House;
import xyz.narengi.android.common.dto.HouseAvailableDates;
import xyz.narengi.android.common.dto.Profile;
import xyz.narengi.android.common.dto.ProvinceCity;
import xyz.narengi.android.common.dto.RequestVerification;
import xyz.narengi.android.common.dto.SuggestionsResult;

/**
 * @author Siavash Mahmoudpour
 */
public interface RetrofitApiEndpoints {

    @GET("/api/v1/search")
    Call<AroundLocation[]> getAroundLocations(@Query("term") String searchTerm, @Query("filter[limit]") String filterLimit, @Query("filter[skip]") String filterSkip);

    @GET("/api/v1/suggestion")
    Call<SuggestionsResult> getAroundLocationSuggestions(@Query("term") String searchTerm,
                                                         @Query("filter[selection][city]") String cityCount,
                                                         @Query("filter[selection][attraction]") String attractionCount,
                                                         @Query("filter[selection][house]") String houseCount);

//    @GET("/api/v1/cities/3")
    @GET
    Call<City> getCity(@Url String url);

    @GET
    Call<House> getHouse(@Url String url);

    @GET
    Call<HostProfile> getHostProfile(@Url String url);

    @GET
    Call<Attraction> getAttraction(@Url String url);

    @POST("/api/v1/accounts/register")
    Call<AccountProfile> register(@Body Credential credential);

    @POST("/api/v1/accounts/verifications/request/{type}")
    Call<AccountVerification> requestVerification(@Path("type") String type, @Body RequestVerification requestVerification,
                                                  @Header("authorization") String authorization);

    @POST("/api/v1/accounts/verify/{type}/{code}")
    Call<AccountVerification> verifyAccount(@Path("type") String type, @Path("code") String code,
                                                  @Header("authorization") String authorization);

    @GET("/api/v1/user-profiles")
    Call<AccountProfile> getProfile(@Header("authorization") String authorization);

    @PUT("/api/v1/user-profiles")
    Call<AccountProfile> updateProfile(@Header("authorization") String authorization,
                                       @Body Profile profile);

    @POST("/api/v1/accounts/login")
    Call<AccountProfile> login(@Body Credential credential);

//    @Multipart
    @POST("/api/v1/user-profiles/picture")
    Call<AccountProfile> uploadProfilePicture(@Header("authorization") String authorization, @Body RequestBody picture);
//    Call<AccountProfile> uploadProfilePicture(@Header("authorization") String authorization, @Part("picture") RequestBody picture);

    @GET("/api/v1/basic-info/provinces")
    Call<Map<String,ProvinceCity[]>> getProvinces();

    @POST("/api/v1/accounts/verifications/request/{type}")
    Call<AccountVerification> requestIdVerification(@Header("authorization") String authorization, @Path("type") String type,
                                                    @Body RequestBody picture);

    @GET
    Call<HouseAvailableDates> getHouseAvailableDates(@Url String url);

    @GET("/api/v1/houses/settings/house-types")
    Call<Map<String, String>[]> getHouseTypes();

    @GET("/api/v1/houses/settings/features")
    Call<Map<String, String>[]> getHouseFeatures();
}
