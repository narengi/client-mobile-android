package xyz.narengi.android.service;

import com.squareup.okhttp.RequestBody;

import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
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
import xyz.narengi.android.common.dto.Type;

/**
 * @author Siavash Mahmoudpour
 */
public interface RetrofitApiEndpoints {

    @GET("/api/search")
    Call<AroundLocation[]> getAroundLocations(@Query("term") String searchTerm, @Query("filter[limit]") String filterLimit, @Query("filter[skip]") String filterSkip);

    @GET("/api/search")
    Call<AroundLocation[]> getAroundLocations(@Query("term") String searchTerm, @Query("filter[limit]") String filterLimit, @Query("filter[skip]") String filterSkip,
                                              @Query("position[lat]") double lat, @Query("position[lng]") double lng);

    @GET("/api/suggestion")
    Call<SuggestionsResult> getAroundLocationSuggestions(@Query("term") String searchTerm,
                                                         @Query("filter[selection][city]") String cityCount,
                                                         @Query("filter[selection][attraction]") String attractionCount,
                                                         @Query("filter[selection][house]") String houseCount);

    //    @GET("/api/cities/3")
    @GET
    Call<City> getCity(@Url String url);

    @GET
    Call<House> getHouse(@Url String url);

    @GET
    Call<HostProfile> getHostProfile(@Url String url);

    @GET
    Call<Attraction> getAttraction(@Url String url);

    @POST("/api/accounts/register")
    Call<AccountProfile> register(@Body Credential credential);

    @POST("/api/accounts/verifications/request/{type}")
    Call<AccountVerification> requestVerification(@Path("type") String type,
                                                  @Body RequestVerification requestVerification);

    @POST("/api/accounts/verify/{type}/{code}")
    Call<AccountVerification> verifyAccount(@Path("type") String type,
                                            @Path("code") String code);

    @PUT("/api/accounts/update")
    Call<AccountProfile> updateProfile(@Body Profile profile);

    @Multipart
    @POST("/api/user-profiles/picture")
    Call<AccountProfile> uploadProfilePicture(@Part MultipartBody.Part picture);
//    Call<AccountProfile> uploadProfilePicture(, @Part("picture") RequestBody picture);

    @GET("/api/settings/provinces")
    Call<Map<String, ProvinceCity[]>> getProvinces();

    @POST("/api/accounts/verifications/request/{type}")
    Call<AccountVerification> requestIdVerification(@Path("type") String type,
                                                    @Body RequestBody picture);

    @GET
    Call<HouseAvailableDates> getHouseAvailableDates(@Url String url);

    @GET("/api/house-types")
    Call<Type[]> getHouseTypes();

    @GET("http://api.narengi.xyz/api/house-features")
    Call<Map<String, String>[]> getHouseFeatures();

    @GET("/api/houses/my-houses")
    Call<House[]> getHostHouses();

    @POST("/api/houses")
    Call<House> addHouse(@Body HouseEntryInput houseEntryInput);

    @PUT
    Call<House> updateHouse(@Url String url, @Body HouseEntryInput houseEntryInput);

    @DELETE
    Call<Object> removeHouse(@Url String url);

    @Multipart
    @POST
    Call<ImageInfo[]> uploadHouseImages(@Url String url, @PartMap() Map<String, RequestBody> picture);

    @POST
    Call<ImageInfo[]> removeHouseImages(@Url String url, @Body RemoveHouseImagesInfo removeHouseImagesInfo);

    @GET
    Call<ImageInfo[]> getHouseImages(@Url String url);

    @GET("/api/me/book-requests")
    Call<BookRequest[]> getBookRequests();


    @GET("/api/me/book-requests")
    Call<BookRequestDTO[]> getBookRequestDTOs();

    @PUT("/api/book-request/{request-id}/accept")
    Call<BookRequest> approveBookRequest(@Path("request-id") String requestId);

    @PUT("/api/book-request/{request-id}/reject")
    Call<BookRequest> rejectBookRequest(@Path("request-id") String requestId);

}
