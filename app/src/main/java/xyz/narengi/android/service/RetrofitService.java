package xyz.narengi.android.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import xyz.narengi.android.common.Constants;
import xyz.narengi.android.common.dto.AccessToken;
import xyz.narengi.android.common.dto.AccountProfile;
import xyz.narengi.android.ui.NarengiApplication;

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
                    .addInterceptor(new RequestAuthorizationInterceptor())
                    .addInterceptor(new ResponseInterceptor())
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
                .addInterceptor(new RequestAuthorizationInterceptor())
                .addInterceptor(new ResponseInterceptor())
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

    public static class RequestAuthorizationInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request.Builder builder = chain.request().newBuilder();

            AccountProfile loggedInProfile = AccountProfile.getLoggedInAccountProfile(NarengiApplication.getInstance());
            if(loggedInProfile != null && loggedInProfile.getToken() != null) {
                builder.addHeader("authorization", loggedInProfile.getToken().getAuthString());
            }
            return chain.proceed(builder.build());
        }
    }

    private static class ResponseInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Response response = chain.proceed(chain.request());
            if (response.code() == 401) {
                // TODO: 9/22/2016 AD logout user
            }
            return response;
        }
    }
}
