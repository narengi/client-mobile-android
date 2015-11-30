package xyz.narengi.android.service;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import xyz.narengi.android.common.dto.AroundLocation;
import xyz.narengi.android.common.dto.SearchResult;

/**
 * @author Siavash Mahmoudpour
 */
public interface RetrofitApiEndpoints {

    @GET("/api/v1/search/")
    Call<AroundLocation[]> getAroundLocations();
//    Call<SearchResult> getAroundLocations();
//    Call<AroundLocation[]> getAroundLocations(@Path("term") String term);

}
