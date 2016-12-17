package xyz.narengi.android.service;


import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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
import xyz.narengi.android.common.dto.UploadImage;

/**
 * @author Siavash Mahmoudpour
 */
public interface RetrofitApiEndpoints {

    @GET("/v1/search")
    Call<AroundLocation[]> getAroundLocations(@Query("term") String searchTerm, @Query("filter[limit]") String filterLimit, @Query("filter[skip]") String filterSkip);

    @GET("/v1/search")
    Call<AroundLocation[]> getAroundLocations(@Query("term") String searchTerm, @Query("filter[limit]") String filterLimit, @Query("filter[skip]") String filterSkip,
                                              @Query("position[lat]") double lat, @Query("position[lng]") double lng);

    @GET("/v1/suggestion")
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

    @POST("/v1/accounts/register")
    Call<AccountProfile> register(@Body Credential credential);

    @POST("/v1/accounts/verifications/request/{type}")
    Call<AccountVerification> requestVerification(@Path("type") String type,
                                                  @Body RequestVerification requestVerification);

    @POST("/v1/accounts/verify/{type}/{code}")
    Call<AccountVerification> verifyAccount(@Path("type") String type,
                                            @Path("code") String code);

    @PUT("/v1/accounts/update")
    Call<AccountProfile> updateProfile(@Body Profile profile);

    @Multipart
    @POST("/v1/medias/upload/userprofile")
    Call<AccountProfile> uploadProfilePicture(@Part MultipartBody.Part picture); ////
//    Call<AccountProfile> uploadProfilePicture(, @Part("picture") RequestBody picture);

    @GET("/v1/settings/provinces")
    Call<Map<String, ProvinceCity[]>> getProvinces();

    @POST("/v1/accounts/verifications/request/{type}")
    Call<AccountVerification> requestIdVerification(@Path("type") String type,
                                                    @Part("file\";name=\"picture\"; filename=\"pp.png\" ") RequestBody picture);

    @GET
    Call<HouseAvailableDates> getHouseAvailableDates(@Url String url);

    @GET("/v1/house-types")
    Call<Type[]> getHouseTypes();

    @GET("https://api.narengi.xyz/v1/house-features")
    Call<Map<String, String>[]> getHouseFeatures();

    @GET("/v1/houses/my-houses")
    Call<House[]> getHostHouses();

    @POST("/v1/houses")
    Call<House> addHouse(@Body HouseEntryInput houseEntryInput);

    @PUT
    Call<House> updateHouse(@Url String url, @Body HouseEntryInput houseEntryInput);

    @DELETE("/v1/houses/{id}")
    Call<Object> removeHouse(@Path("id") String id);

    @Multipart
    @POST("/v1/medias/upload/house/{id}")
    Call<UploadImage> uploadHouseImages(
            @Path("id") String id,
            @Part MultipartBody.Part file);

    @DELETE()
    Call<ResponseBody> removeHouseImages(
            @Url() String url);

    @GET
    Call<ImageInfo[]> getHouseImages(@Url String url);

    @GET("/v1/me/book-requests")
    Call<BookRequest[]> getBookRequests();


    @GET("/v1/me/book-requests")
    Call<BookRequestDTO[]> getBookRequestDTOs();

    @PUT("/v1/book-request/{request-id}/accept")
    Call<BookRequest> approveBookRequest(@Path("request-id") String requestId);

    @PUT("/v1/book-request/{request-id}/reject")
    Call<BookRequest> rejectBookRequest(@Path("request-id") String requestId);

}
