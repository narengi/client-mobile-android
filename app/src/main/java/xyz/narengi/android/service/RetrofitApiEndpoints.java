package xyz.narengi.android.service;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import xyz.narengi.android.common.dto.AroundLocation;
import xyz.narengi.android.common.dto.SearchResult;
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
}
