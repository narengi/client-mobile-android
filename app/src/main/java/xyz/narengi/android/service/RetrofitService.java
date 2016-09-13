package xyz.narengi.android.service;

import android.util.Log;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import xyz.narengi.android.common.Constants;
import xyz.narengi.android.common.dto.AroundLocation;
import xyz.narengi.android.common.dto.SearchResult;

/**
 * @author Siavash Mahmoudpour
 */
public class RetrofitService {

    private static RetrofitService instance;
    private Retrofit retrofit;

    public RetrofitService() {
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.SERVER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static RetrofitService getInstance() {

        if (instance == null)
            instance = new RetrofitService();
        return instance;
    }

    public void searchAroundLocations() {

//        RetrofitApiEndpoints apiEndpoints = retrofit.create(RetrofitApiEndpoints.class);
//        Call<SearchResult> call = apiEndpoints.getAroundLocations();
//        call.enqueue(new Callback<SearchResult>() {
//            @Override
//            public void onResponse(Response<SearchResult> response, Retrofit retrofit) {
//                int statusCode = response.code();
//                AroundLocation[] aroundLocations = response.body();
//
//                if (aroundLocations != null && aroundLocations.length > 0) {
//                    System.err.println("Around locations count : " + aroundLocations.length);
//                    Log.d("RetrofitService", "Around locations count : " + aroundLocations.length);
//
//                    for (AroundLocation location:aroundLocations) {
//                        System.err.println("Around location title : " + location.getTitle());
//                        Log.d("RetrofitService", "Around location title : " + location.getTitle());
//                    }
//                }
//                else
////                    System.err.println("Around locations is empty");
//                    Log.d("RetrofitService", "Around locations is empty");
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                // Log error here since request failed
//            }
//        });
    }
}
