package xyz.narengi.android.service;

import com.squareup.okhttp.RequestBody;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Map;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.DELETE;
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
import xyz.narengi.android.common.dto.BookRequest;
import xyz.narengi.android.common.dto.BookRequestDTO;
import xyz.narengi.android.common.dto.City;
import xyz.narengi.android.common.dto.Credential;
import xyz.narengi.android.common.dto.HostProfile;
import xyz.narengi.android.common.dto.House;
import xyz.narengi.android.common.dto.HouseAvailableDates;
import xyz.narengi.android.common.dto.HouseEntryInput;
import xyz.narengi.android.common.dto.ImageInfo;
import xyz.narengi.android.common.dto.Profile;
import xyz.narengi.android.common.dto.ProvinceCity;
import xyz.narengi.android.common.dto.RemoveHouseImagesInfo;
import xyz.narengi.android.common.dto.RequestVerification;
import xyz.narengi.android.common.dto.SuggestionsResult;
import xyz.narengi.android.ui.fragment.HouseImagesEntryFragment;

/**
 * @author Siavash Mahmoudpour
 */
public interface RetrofitApiEndpoints {

    @GET("/api/v1/search")
    Call<AroundLocation[]> getAroundLocations(@Query("term") String searchTerm, @Query("filter[limit]") String filterLimit, @Query("filter[skip]") String filterSkip);

    @GET("/api/v1/search")
    Call<AroundLocation[]> getAroundLocations(@Query("term") String searchTerm, @Query("filter[limit]") String filterLimit, @Query("filter[skip]") String filterSkip,
                                              @Query("position[lat]") double lat, @Query("position[lng]") double lng);

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

    @GET("/api/v1/houses/my-houses")
    Call<House[]> getHostHouses(@Header("authorization") String authorization);

    @POST("/api/v1/houses")
    Call<House> addHouse(@Header("authorization") String authorization, @Body HouseEntryInput houseEntryInput);

    @PUT
    Call<House> updateHouse(@Header("authorization") String authorization,@Url String url, @Body HouseEntryInput houseEntryInput);

    @DELETE
    Call<Object> removeHouse(@Header("authorization") String authorization,@Url String url);

    @POST
    Call<ImageInfo[]> uploadHouseImages(@Header("authorization") String authorization, @Url String url, @Body RequestBody picture);

    @POST
    Call<ImageInfo[]> removeHouseImages(@Header("authorization") String authorization, @Url String url, @Body RemoveHouseImagesInfo removeHouseImagesInfo);

    @GET
    Call<ImageInfo[]> getHouseImages(@Url String url);

    @GET("/api/v1/me/book-requests")
    Call<BookRequest[]> getBookRequests(@Header("authorization") String authorization);


    /**
     * This service should be removed or renamed after new services for book requests created in back-end
     * @param authorization
     * @return
     */
    @GET("/api/v1/me/book-requests")
    Call<BookRequestDTO[]> getBookRequestDTOs(@Header("authorization") String authorization);

    @PUT("/api/v1/book-request/{request-id}/accept")
    Call<BookRequest> approveBookRequest(@Header("authorization") String authorization, @Path("request-id") String requestId);

    @PUT("/api/v1/book-request/{request-id}/reject")
    Call<BookRequest> rejectBookRequest(@Header("authorization") String authorization, @Path("request-id") String requestId);

}
