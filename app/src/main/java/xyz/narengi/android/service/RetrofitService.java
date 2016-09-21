package xyz.narengi.android.service;

import android.util.Log;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import xyz.narengi.android.common.Constants;

/**
 * @author Siavash Mahmoudpour
 */
public class RetrofitService {

    private static HttpLoggingInterceptor.Logger retrofitLogger = new HttpLoggingInterceptor.Logger() {
        @Override
        public void log(String message) {
            Log.d("RequestLog", message);
        }
    };

    private static RetrofitService instance;
    private Retrofit retrofit;

    public RetrofitService() {
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.SERVER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static RetrofitService getInstance() {

        if (instance == null) {
            instance = new RetrofitService();
            HttpLoggingInterceptor bodyLoggingInterceptor = new HttpLoggingInterceptor(retrofitLogger);
            bodyLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .addInterceptor(bodyLoggingInterceptor)
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS);
            instance.retrofit = new Retrofit.Builder()
                    .client(builder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(Constants.SERVER_BASE_URL)
                    .build();
        }
        return instance;
    }

    public static RetrofitService getInstance(Gson customGson) {
        RetrofitService result;
        result = new RetrofitService();
        HttpLoggingInterceptor bodyLoggingInterceptor = new HttpLoggingInterceptor(retrofitLogger);
        bodyLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(bodyLoggingInterceptor)
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS);
        result.retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create(customGson))
                .baseUrl(Constants.SERVER_BASE_URL)
                .build();
        return result;
    }

    public Retrofit getRetrofit() {
        return retrofit;
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
